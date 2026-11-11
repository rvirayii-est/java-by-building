package dev.javabybuilding.inventory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Reads and writes Products in the product table. DAO = Data Access Object: a class whose only job is the SQL for one
 * kind of thing, so the rest of the app never sees a Connection or a ResultSet. (Lesson 30 turns this idea into the
 * repository pattern.)
 *
 * <p>Every statement is a {@link PreparedStatement}: the SQL has ? holes and the values are handed over separately, so
 * they can never be mistaken for SQL. Building SQL by gluing text together ("... WHERE name = '" + name + "'")
 * lets a name such as {@code x'; DROP TABLE product; --} run as a command. That attack is called SQL injection.
 */
public class ProductDao {

    private static final String UNIQUE_VIOLATION = "23505"; // the SQL state for "this value already exists"
    private static final String COLUMNS = "name, price, reorder_level, quantity";

    private final Database database;

    public ProductDao(Database database) {
        this.database = database;
    }

    /**
     * Stores a new product.
     *
     * @throws InvalidInputException when a product with this name already exists (ignoring capitals)
     * @throws DatabaseException when the database fails for another reason
     */
    public void insert(Product product) {
        // TODO 1: (a) if findByName(product.getName()) is present, throw new InvalidInputException("That product already exists: " + name).
        // (b) sql = "INSERT INTO product (" + COLUMNS + ") VALUES (?, ?, ?, ?)"; open a PreparedStatement in try-with-resources
        //     (database.connection().prepareStatement(sql)); setString(1, name), setBigDecimal(2, price), setInt(3, reorder), setInt(4, quantity); executeUpdate().
        // (c) catch SQLException e: if e.getSQLState() equals UNIQUE_VIOLATION throw the same InvalidInputException; otherwise throw new DatabaseException("Cannot save ...", e).
        throw new UnsupportedOperationException("TODO 1");
    }

    /** The product with this name (ignoring capitals and extra spaces), or empty when there is none. */
    public Optional<Product> findByName(String name) {
        // TODO 2: SELECT COLUMNS FROM product WHERE LOWER(name) = ?  with the parameter TextUtil.clean(name).toLowerCase().
        // executeQuery() gives a ResultSet: if rows.next() return Optional.of(toProduct(rows)), else Optional.empty(). Wrap SQLException in DatabaseException.
        throw new UnsupportedOperationException("TODO 2");
    }

    /** Every product, in the order they were added. */
    public List<Product> findAll() {
        // TODO 3: SELECT COLUMNS FROM product ORDER BY id; loop "while (rows.next())" and add toProduct(rows) to a list.
        throw new UnsupportedOperationException("TODO 3");
    }

    /**
     * Saves the product's price, reorder level and quantity (the name says which row).
     *
     * @throws UnknownProductException when no product has this name
     */
    public void update(Product product) {
        // TODO 4: UPDATE product SET price = ?, reorder_level = ?, quantity = ? WHERE name = ?  (four parameters, in that order).
        // executeUpdate() returns how many rows changed: 0 means there is no such product, so throw new UnknownProductException(name).
        throw new UnsupportedOperationException("TODO 4");
    }

    /** How many products there are. */
    public int count() {
        try (PreparedStatement statement = database.connection().prepareStatement("SELECT COUNT(*) FROM product");
             ResultSet rows = statement.executeQuery()) {
            rows.next();
            return rows.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException("Cannot count the products", e);
        }
    }

    /** One row of the result as a Product. Columns are read by NAME, so adding a column to the table later breaks nothing. */
    private static Product toProduct(ResultSet row) throws SQLException {
        return new Product(row.getString("name"), row.getBigDecimal("price"), row.getInt("reorder_level"), row.getInt("quantity"));
    }
}
