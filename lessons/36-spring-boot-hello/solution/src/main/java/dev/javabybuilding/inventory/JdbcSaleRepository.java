package dev.javabybuilding.inventory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Sales in the sale, sale_line and customer tables. One Sale object becomes one sale row, one row per line, and
 * (unless it is a walk-in) a customer row that is found or created. The customer's points are not stored:
 * they are worked out again from the sales when they are read (the same choice DataStore made in lesson 19).
 */
public class JdbcSaleRepository implements SaleRepository {

    private final Database database;

    public JdbcSaleRepository(Database database) {
        this.database = database;
    }

    @Override
    public Sale add(Sale draft) {
        Connection connection = database.connection();
        try {
            Integer customerId = draft.getCustomer().isWalkIn() ? null : findOrCreateCustomer(connection, draft.getCustomer());
            int number;
            // RETURN_GENERATED_KEYS asks the database for the id it just made up for the new row.
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO sale (customer_id, sold_at, discount, payment) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                if (customerId == null) {
                    statement.setNull(1, Types.INTEGER); // a walk-in: the customer column stays empty (NULL)
                } else {
                    statement.setInt(1, customerId);
                }
                statement.setObject(2, draft.getDateTime());
                statement.setString(3, DiscountCodes.encode(draft.getDiscount()));
                statement.setString(4, draft.getPayment().name());
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    keys.next();
                    number = keys.getInt(1);
                }
            }
            // The product is found by name inside the INSERT itself; an unknown name leaves product_id NULL and the database refuses.
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO sale_line (sale_id, product_id, unit_price, quantity) VALUES (?, (SELECT id FROM product WHERE name = ?), ?, ?)")) {
                for (SaleLine line : draft.getLines()) {
                    statement.setInt(1, number);
                    statement.setString(2, line.productName());
                    statement.setBigDecimal(3, line.unitPrice());
                    statement.setInt(4, line.quantity());
                    statement.executeUpdate();
                }
            }
            return draft.withNumber(number);
        } catch (SQLException e) {
            throw new DatabaseException("Cannot save the sale", e);
        }
    }

    private static int findOrCreateCustomer(Connection connection, Customer customer) throws SQLException {
        String phone = customer.getPhone() == null ? "" : customer.getPhone();
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM customer WHERE name = ? AND phone = ?")) {
            statement.setString(1, customer.getName());
            statement.setString(2, phone);
            try (ResultSet rows = statement.executeQuery()) {
                if (rows.next()) {
                    return rows.getInt(1);
                }
            }
        }
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO customer (name, phone) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, customer.getName());
            statement.setString(2, phone);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    @Override
    public List<Sale> findAll() {
        Connection connection = database.connection();
        Map<Integer, Sale> sales = new LinkedHashMap<>();   // keeps the order of the ids
        Map<String, Customer> customers = new LinkedHashMap<>(); // one Customer object per person
        String salesSql = "SELECT s.id, s.sold_at, s.discount, s.payment, c.name AS customer_name, c.phone AS customer_phone "
                + "FROM sale s LEFT JOIN customer c ON c.id = s.customer_id ORDER BY s.id";
        String linesSql = "SELECT l.sale_id, p.name, l.unit_price, l.quantity "
                + "FROM sale_line l JOIN product p ON p.id = l.product_id ORDER BY l.id";
        try (PreparedStatement statement = connection.prepareStatement(salesSql); ResultSet rows = statement.executeQuery()) {
            while (rows.next()) {
                String name = rows.getString("customer_name");
                Customer customer = name == null ? Customer.WALK_IN
                        : customers.computeIfAbsent(name + "|" + rows.getString("customer_phone"), k -> newCustomer(rows));
                Sale sale = new Sale(rows.getInt("id"), customer, rows.getObject("sold_at", LocalDateTime.class));
                sale.setDiscount(DiscountCodes.decode(rows.getString("discount")));
                sale.setPayment(PaymentMethod.valueOf(rows.getString("payment")));
                sales.put(sale.getNumber(), sale);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Cannot read the sales", e);
        }
        try (PreparedStatement statement = connection.prepareStatement(linesSql); ResultSet rows = statement.executeQuery()) {
            while (rows.next()) {
                BigDecimal price = rows.getBigDecimal("unit_price");
                sales.get(rows.getInt("sale_id")).addLine(new SaleLine(rows.getString("name"), price, rows.getInt("quantity")));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Cannot read the sale lines", e);
        }
        for (Sale sale : sales.values()) {
            sale.getCustomer().addPurchase(sale.total()); // points are replayed, in order; the walk-in ignores this
        }
        return new ArrayList<>(sales.values());
    }

    private static Customer newCustomer(ResultSet row) {
        try {
            return new Customer(row.getString("customer_name"), row.getString("customer_phone"));
        } catch (SQLException e) {
            throw new DatabaseException("Cannot read a customer", e);
        }
    }
}
