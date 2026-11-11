package dev.javabybuilding.inventory;

import dev.javabybuilding.sql.ShopSchema;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * The shop's connection to its H2 database. JDBC (Java Database Connectivity) is the standard Java way to talk to any
 * relational database: you get a {@link Connection}, send SQL through it, and read the answers. Only the URL changes
 * when you move to PostgreSQL or MySQL.
 *
 * <p>A Connection is a resource, like a file: it must be closed. Database is AutoCloseable, so
 * {@code try (Database db = Database.openMemory("x")) { ... }} closes it for you.
 *
 * <p>shortcut: one Connection for the whole app is fine for one console and one cashier. A web server with many users
 * needs a pool of connections (lesson 36 onward); the repositories would not change.
 */
public final class Database implements AutoCloseable, Transactor {

    private final Connection connection;
    private boolean inTransaction; // true while inTransaction(...) is running; nested calls join it

    private Database(Connection connection) {
        this.connection = connection;
    }

    /**
     * A database that lives in memory and disappears when closed. The name makes it separate from other in-memory
     * databases, so tests use a unique name each. URL: jdbc:h2:mem:name
     */
    public static Database openMemory(String name) {
        return connect("jdbc:h2:mem:" + name);
    }

    /**
     * A database stored in files next to the given path: openFile(Path.of("shop-data", "shop")) creates
     * shop-data/shop.mv.db. The data is still there next time. URL: jdbc:h2:file:/full/path/shop
     */
    public static Database openFile(Path file) {
        Path absolute = file.toAbsolutePath();
        try {
            Files.createDirectories(absolute.getParent());
        } catch (IOException e) {
            throw new DataFileException("Cannot create the folder " + absolute.getParent() + ": " + e.getMessage());
        }
        return connect("jdbc:h2:file:" + absolute.toString().replace('\\', '/'));
    }

    private static Database connect(String url) {
        try {
            return new Database(DriverManager.getConnection(url));
        } catch (SQLException e) {
            throw new DatabaseException("Cannot open the database " + url, e);
        }
    }

    /** The JDBC connection. Repositories use it to prepare statements. */
    public Connection connection() {
        return connection;
    }

    /**
     * Creates the shop's tables (db/schema.sql, lesson 28) when the database is new, and does nothing when they exist.
     * Lesson 32 replaces this with versioned migrations.
     */
    public void createSchemaIfMissing() {
        try {
            try (ResultSet tables = connection.getMetaData().getTables(null, "PUBLIC", "PRODUCT", null)) {
                if (tables.next()) {
                    return;
                }
            }
            ShopSchema.create(connection);
        } catch (SQLException e) {
            throw new DatabaseException("Cannot create the shop's tables", e);
        }
    }

    /**
     * Runs the work as one transaction on this connection. By default JDBC is in "auto-commit" mode: every statement
     * is saved the moment it runs, so a failure half way leaves half the job done. Here we switch auto-commit off, run
     * the work, then {@code commit()} (save everything at once). If the work throws, {@code rollback()} undoes every
     * statement since the start, and the exception goes on to the caller unchanged. Auto-commit is switched on again
     * at the end, whatever happened.
     *
     * <p>If a transaction is already running the work joins it, so a method that is itself called inside a
     * transaction can safely start its own.
     *
     * <p>Not for several threads: one Connection is one conversation. Real servers use one connection per request.
     */
    @Override
    public <T> T inTransaction(Supplier<T> work) {
        if (inTransaction) {
            return work.get(); // join the transaction that is already running
        }
        inTransaction = true;
        try {
            connection.setAutoCommit(false);
            try {
                T result = work.get();
                connection.commit();
                return result;
            } catch (RuntimeException | Error failure) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackProblem) {
                    failure.addSuppressed(rollbackProblem); // keep the original failure as the main one
                }
                throw failure;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseException("The transaction could not be completed", e);
        } finally {
            inTransaction = false;
        }
    }

    /** Closes the connection. For a file database this also flushes everything to disk. */
    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new DatabaseException("Cannot close the database", e);
        }
    }
}
