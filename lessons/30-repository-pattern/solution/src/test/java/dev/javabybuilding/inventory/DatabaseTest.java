package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DatabaseTest {

    @Test
    void aNewMemoryDatabaseHasTheShopTables() throws SQLException {
        try (Database database = TestDb.fresh()) {
            assertTrue(database.connection().getMetaData().getTables(null, "PUBLIC", "SALE_LINE", null).next());
        }
    }

    @Test
    void createSchemaIfMissingCanBeCalledTwice() {
        try (Database database = TestDb.fresh()) {
            database.createSchemaIfMissing(); // no "table already exists" error
            assertEquals(0, new JdbcProductRepository(database).findAll().size());
        }
    }

    @Test
    void aMemoryDatabaseDisappearsWhenItIsClosed() {
        String name = "gone-" + UUID.randomUUID();
        try (Database first = Database.openMemory(name)) {
            first.createSchemaIfMissing();
            new JdbcProductRepository(first).add(new Product("Rice 25kg", Money.of("1250.50"), 10));
        }
        try (Database second = Database.openMemory(name)) {
            second.createSchemaIfMissing(); // a brand new, empty database
            assertEquals(0, new JdbcProductRepository(second).findAll().size());
        }
    }

    @Test
    void aFileDatabaseKeepsItsDataBetweenRuns(@TempDir Path folder) {
        Path file = folder.resolve("shop-db").resolve("shop");
        try (Database first = Database.openFile(file)) {
            first.createSchemaIfMissing();
            new JdbcProductRepository(first).add(new Product("Rice 25kg", Money.of("1250.50"), 10, 17));
        }
        assertTrue(Files.exists(folder.resolve("shop-db").resolve("shop.mv.db")));
        try (Database second = Database.openFile(file)) {
            second.createSchemaIfMissing(); // the tables exist already, so nothing happens
            assertEquals(17, new JdbcProductRepository(second).findByName("rice 25kg").orElseThrow().getQuantity());
        }
    }

    @Test
    void aClosedDatabaseCannotBeUsed() throws SQLException {
        Database database = TestDb.fresh();
        database.close();
        assertTrue(database.connection().isClosed());
        assertThrows(DatabaseException.class, () -> new JdbcProductRepository(database).findAll().size());
    }

    @Test
    void aDatabaseExceptionKeepsTheOriginalCause() throws SQLException {
        Database database = TestDb.fresh();
        database.close();
        DatabaseException e = assertThrows(DatabaseException.class, () -> new JdbcProductRepository(database).findAll().size());
        assertTrue(e.getCause() instanceof SQLException);
        assertFalse(e.getMessage().isBlank());
    }
}
