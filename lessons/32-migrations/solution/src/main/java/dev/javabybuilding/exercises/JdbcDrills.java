package dev.javabybuilding.exercises;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Lesson 29 drills: plain JDBC against the table product (id, name, price, reorder_level, quantity).
 * Rules: use try-with-resources so every Statement and ResultSet is closed, and use a PreparedStatement with ? holes
 * whenever a value comes from outside (a name typed by a person). Never glue values into the SQL text.
 * The methods declare "throws SQLException": the caller decides what to do about a database failure.
 */
public class JdbcDrills {

    /** How many rows the product table has. (No ? needed here: there are no values to pass.) */
    public static int countProducts(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet rows = statement.executeQuery("SELECT COUNT(*) FROM product")) {
            rows.next();
            return rows.getInt(1);
        }
    }

    /** The price of the product with exactly this name, or empty when there is none. */
    public static Optional<BigDecimal> priceOf(Connection connection, String name) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT price FROM product WHERE name = ?")) {
            statement.setString(1, name);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next() ? Optional.of(rows.getBigDecimal("price")) : Optional.empty();
            }
        }
    }

    /** Inserts a product with this name and price, reorder level 0 and quantity 0. Names such as Nestle's Milo must work. */
    public static void addProduct(Connection connection, String name, BigDecimal price) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO product (name, price, reorder_level, quantity) VALUES (?, ?, 0, 0)")) {
            statement.setString(1, name);
            statement.setBigDecimal(2, price);
            statement.executeUpdate();
        }
    }

    /** The names of the products that cost less than max, A to Z. */
    public static List<String> namesCheaperThan(Connection connection, BigDecimal max) throws SQLException {
        List<String> names = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT name FROM product WHERE price < ? ORDER BY name")) {
            statement.setBigDecimal(1, max);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    names.add(rows.getString("name"));
                }
            }
        }
        return names;
    }

    /** Adds amount to the quantity of the product with this name. Returns how many rows changed: 1, or 0 when there is no such product. */
    public static int receive(Connection connection, String name, int amount) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE product SET quantity = quantity + ? WHERE name = ?")) {
            statement.setInt(1, amount);
            statement.setString(2, name);
            return statement.executeUpdate();
        }
    }

    /** Name to quantity for every product, in the order they were added (by id). */
    public static Map<String, Integer> stockByName(Connection connection) throws SQLException {
        Map<String, Integer> stock = new LinkedHashMap<>();
        try (Statement statement = connection.createStatement();
             ResultSet rows = statement.executeQuery("SELECT name, quantity FROM product ORDER BY id")) {
            while (rows.next()) {
                stock.put(rows.getString("name"), rows.getInt("quantity"));
            }
        }
        return stock;
    }
}
