package dev.javabybuilding.exercises;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Lesson 29 drills: plain JDBC against the table product (id, name, price, reorder_level, quantity).
 * Rules: use try-with-resources so every Statement and ResultSet is closed, and use a PreparedStatement with ? holes
 * whenever a value comes from outside (a name typed by a person). Never glue values into the SQL text.
 * The methods declare "throws SQLException": the caller decides what to do about a database failure.
 * Pattern to follow: lessons/29-jdbc/solution/.../ProductDao.java and examples/JdbcTour.java.
 */
public class JdbcDrills {

    /** TODO 1: how many rows the product table has. (SELECT COUNT(*) ...; no ? needed: there are no values to pass.) */
    public static int countProducts(Connection connection) throws SQLException {
        return -1;
    }

    /** TODO 2: the price of the product with exactly this name, or empty when there is none. (prepareStatement, setString, executeQuery) */
    public static Optional<BigDecimal> priceOf(Connection connection, String name) throws SQLException {
        return Optional.empty();
    }

    /** TODO 3: insert a product with this name and price, reorder level 0 and quantity 0. Names such as Nestle's Milo must work. (executeUpdate) */
    public static void addProduct(Connection connection, String name, BigDecimal price) throws SQLException {
    }

    /** TODO 4: the names of the products that cost less than max, A to Z. */
    public static List<String> namesCheaperThan(Connection connection, BigDecimal max) throws SQLException {
        return List.of();
    }

    /** TODO 5: add amount to the quantity of the product with this name. Return how many rows changed: 1, or 0 when there is no such product. */
    public static int receive(Connection connection, String name, int amount) throws SQLException {
        return -1;
    }

    /** TODO 6: name to quantity for every product, in the order they were added (by id). A LinkedHashMap keeps that order. */
    public static Map<String, Integer> stockByName(Connection connection) throws SQLException {
        return Map.of();
    }
}
