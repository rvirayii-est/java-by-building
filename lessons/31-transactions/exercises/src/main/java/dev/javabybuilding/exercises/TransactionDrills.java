package dev.javabybuilding.exercises;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Lesson 31 drills: transactions in plain JDBC, on the table product (id, name, price, reorder_level, quantity).
 * A transaction in JDBC: setAutoCommit(false), do the work, commit(); if anything fails, rollback(); and always put
 * auto-commit back to true.
 */
public class TransactionDrills {

    /** Work that needs a Connection and may throw SQLException (an ordinary lambda cannot, so we need this interface). */
    @FunctionalInterface
    public interface SqlWork {
        void run(Connection connection) throws SQLException;
    }

    /**
     * Runs the work as ONE transaction. When the work finishes, commit. When it throws (SQLException or anything
     * else), roll back everything it did and throw the same exception again. Either way auto-commit is true again
     * when this method ends.
     */
    public static void inTransaction(Connection connection, SqlWork work) throws SQLException {
        // TODO 1: connection.setAutoCommit(false); try { work.run(connection); connection.commit(); }
        // catch (SQLException | RuntimeException e) { connection.rollback(); throw e; } finally { connection.setAutoCommit(true); }
        work.run(connection); // as it is, this is NOT a transaction: every statement is saved at once
    }

    /**
     * Repacks stock: takes amount units off the product called from and puts them on the product called to, ALL OR
     * NOTHING. Uses {@link #inTransaction}. Rules:
     * - if from has fewer than amount units, throw IllegalStateException("Not enough stock") and change nothing;
     * - if either product does not exist, throw IllegalStateException("Unknown product") and change nothing
     *   (even if the first UPDATE already happened: that is what the rollback is for).
     * Hint: UPDATE ... SET quantity = quantity - ? WHERE name = ? AND quantity >= ? changes 0 rows when the stock is too low.
     */
    public static void moveStock(Connection connection, String from, String to, int amount) throws SQLException {
        // TODO 2: inTransaction(connection, c -> { ... }) with two UPDATEs (see the hint above).
        // For the exception text, the private helper exists(c, name) tells "unknown" from "not enough stock".
    }

    private static boolean exists(Connection connection, String name) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM product WHERE name = ?")) {
            statement.setString(1, name);
            return statement.executeQuery().next();
        }
    }
}
