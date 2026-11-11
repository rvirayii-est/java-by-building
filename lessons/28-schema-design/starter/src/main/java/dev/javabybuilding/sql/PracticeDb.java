package dev.javabybuilding.sql;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

/**
 * A throw-away practice database for lessons 25-28: H2 running in memory, filled with the sample shop.
 * Every call makes a NEW database with its own unique name, so tests never see each other's changes and nothing is
 * written to disk. When you close the Connection, the database disappears.
 *
 * <p>The tables come from the file sql/schema.sql and the rows from sql/seed.sql (both in src/main/resources).
 */
public final class PracticeDb {

    private PracticeDb() {}

    /** A fresh database with the shop's tables and the sample rows. */
    public static Connection open() throws SQLException {
        Connection connection = openEmpty();
        SqlRunner.execute(connection, resource("/sql/schema.sql"));
        SqlRunner.execute(connection, resource("/sql/seed.sql"));
        return connection;
    }

    /**
     * A fresh database built from the designed schema of lesson 28 (db/schema.sql: with CHECK, UNIQUE, named foreign
     * keys and indexes) and filled with the same sample rows. Use it to see the database refuse bad data.
     */
    public static Connection openWithShopSchema() throws SQLException {
        Connection connection = openEmpty();
        ShopSchema.create(connection);
        SqlRunner.execute(connection, resource("/sql/seed.sql"));
        return connection;
    }

    /** A fresh database with nothing in it. */
    public static Connection openEmpty() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:mem:practice-" + UUID.randomUUID());
    }

    /** The text of a file inside src/main/resources, for example "/sql/schema.sql". */
    public static String resource(String path) {
        try (InputStream in = PracticeDb.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalArgumentException("Resource not found: " + path);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + path, e);
        }
    }
}
