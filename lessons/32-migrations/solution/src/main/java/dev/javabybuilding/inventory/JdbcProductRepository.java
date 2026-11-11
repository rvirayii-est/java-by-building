package dev.javabybuilding.inventory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The ProductRepository that keeps products in the product table through JDBC. It is lesson 29's ProductDao with a
 * new name and an interface: the SQL is unchanged, but the rest of the app now depends on ProductRepository, not on
 * this class, so it never learns that JDBC exists.
 *
 * <p>Every statement is a {@link PreparedStatement}: the SQL has ? holes and the values are handed over separately, so
 * they can never be mistaken for SQL. Building SQL by gluing text together ("... WHERE name = '" + name + "'")
 * lets a name such as {@code x'; DROP TABLE product; --} run as a command. That attack is called SQL injection.
 */
public class JdbcProductRepository implements ProductRepository {

    private static final String UNIQUE_VIOLATION = "23505"; // the SQL state for "this value already exists"
    private static final String COLUMNS = "name, price, reorder_level, quantity";

    private final Database database;

    public JdbcProductRepository(Database database) {
        this.database = database;
    }

    /**
     * Stores a new product.
     *
     * @throws InvalidInputException when a product with this name already exists (ignoring capitals)
     * @throws DatabaseException when the database fails for another reason
     */
    @Override
    public void add(Product product) {
        if (findByName(product.getName()).isPresent()) {
            throw new InvalidInputException("That product already exists: " + product.getName());
        }
        String sql = "INSERT INTO product (" + COLUMNS + ") VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = database.connection().prepareStatement(sql)) {
            statement.setString(1, product.getName());          // the first ? gets the name, and so on
            statement.setBigDecimal(2, product.getPrice());
            statement.setInt(3, product.getReorderLevel());
            statement.setInt(4, product.getQuantity());
            statement.executeUpdate();                          // executeUpdate for INSERT/UPDATE/DELETE
        } catch (SQLException e) {
            if (UNIQUE_VIOLATION.equals(e.getSQLState())) {     // two people added the same name at the same moment
                throw new InvalidInputException("That product already exists: " + product.getName());
            }
            throw new DatabaseException("Cannot save " + product.getName(), e);
        }
    }

    /** The product with this name (ignoring capitals and extra spaces), or empty when there is none. */
    @Override
    public Optional<Product> findByName(String name) {
        String sql = "SELECT " + COLUMNS + " FROM product WHERE LOWER(name) = ?";
        try (PreparedStatement statement = database.connection().prepareStatement(sql)) {
            statement.setString(1, TextUtil.clean(name).toLowerCase());
            try (ResultSet rows = statement.executeQuery()) {   // executeQuery for SELECT; the ResultSet is closed too
                return rows.next() ? Optional.of(toProduct(rows)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Cannot look up " + name, e);
        }
    }

    /** Every product, in the order they were added. */
    @Override
    public List<Product> findAll() {
        String sql = "SELECT " + COLUMNS + " FROM product ORDER BY id";
        try (PreparedStatement statement = database.connection().prepareStatement(sql);
             ResultSet rows = statement.executeQuery()) {
            List<Product> products = new ArrayList<>();
            while (rows.next()) {                               // next() moves to the next row; false when there are no more
                products.add(toProduct(rows));
            }
            return products;
        } catch (SQLException e) {
            throw new DatabaseException("Cannot list the products", e);
        }
    }

    /**
     * Saves the product's price, reorder level and quantity (the name says which row).
     *
     * @throws UnknownProductException when no product has this name
     */
    @Override
    public void update(Product product) {
        String sql = "UPDATE product SET price = ?, reorder_level = ?, quantity = ? WHERE name = ?";
        try (PreparedStatement statement = database.connection().prepareStatement(sql)) {
            statement.setBigDecimal(1, product.getPrice());
            statement.setInt(2, product.getReorderLevel());
            statement.setInt(3, product.getQuantity());
            statement.setString(4, product.getName());
            if (statement.executeUpdate() == 0) {               // executeUpdate returns how many rows changed
                throw new UnknownProductException(product.getName());
            }
        } catch (SQLException e) {
            throw new DatabaseException("Cannot update " + product.getName(), e);
        }
    }

    /** One row of the result as a Product. Columns are read by NAME, so adding a column to the table later breaks nothing. */
    private static Product toProduct(ResultSet row) throws SQLException {
        return new Product(row.getString("name"), row.getBigDecimal("price"), row.getInt("reorder_level"), row.getInt("quantity"));
    }
}
