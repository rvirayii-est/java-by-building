package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.javabybuilding.sql.Erd;
import dev.javabybuilding.sql.PracticeDb;
import dev.javabybuilding.sql.QueryResult;
import dev.javabybuilding.sql.ShopSchema;
import dev.javabybuilding.sql.SqlRunner;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MigrationTest {

    private Database database;

    @BeforeEach
    void open() {
        database = Database.openMemory("migrations-" + UUID.randomUUID());
    }

    @AfterEach
    void close() {
        database.close();
    }

    private QueryResult query(String sql) throws SQLException {
        return SqlRunner.query(database.connection(), sql);
    }

    private static Migration m(int version, String sql) {
        return new Migration(version, "step " + version, sql);
    }

    // ---- the migration files

    @Test
    void fileNamesGiveTheVersionAndTheDescription() {
        Migration migration = Migration.fromFile("V12__add_customer_email.sql", "SELECT 1");
        assertEquals(12, migration.version());
        assertEquals("add customer email", migration.description());
    }

    @Test
    void badFileNamesAreRejected() {
        assertThrows(MigrationException.class, () -> Migration.fromFile("create_tables.sql", ""));
        assertThrows(MigrationException.class, () -> Migration.fromFile("V1_one_underscore.sql", ""));
        assertThrows(MigrationException.class, () -> Migration.fromFile("Vx__letters.sql", ""));
    }

    @Test
    void theChecksumChangesWhenTheScriptChanges() {
        assertNotEquals(m(1, "CREATE TABLE a (x INT)").checksum(), m(1, "CREATE TABLE a (y INT)").checksum());
        assertEquals(m(1, "CREATE TABLE a (x INT)").checksum(), m(1, "CREATE TABLE a (x INT)").checksum());
    }

    @Test
    void theChecksumIgnoresWindowsVersusLinuxLineEndings() {
        assertEquals(m(1, "A;\nB;\n").checksum(), m(1, "A;\r\nB;\r\n").checksum());
    }

    @Test
    void versionsMustBeOneTwoThreeWithNoGapAndNoDuplicate() {
        assertEquals(List.of(1, 2, 3), Migrations.validate(List.of(m(3, ""), m(1, ""), m(2, ""))).stream().map(Migration::version).toList());
        assertTrue(assertThrows(MigrationException.class, () -> Migrations.validate(List.of(m(1, ""), m(3, "")))).getMessage().contains("V2 is missing"));
        assertTrue(assertThrows(MigrationException.class, () -> Migrations.validate(List.of(m(1, ""), m(2, ""), m(2, "")))).getMessage().contains("Two migrations"));
        assertThrows(MigrationException.class, () -> Migrations.validate(List.of(m(2, ""))));
    }

    @Test
    void theAppsMigrationsLoadFromTheClasspathInOrder() {
        List<Migration> migrations = Migrations.fromClasspath();
        assertEquals(List.of(1, 2, 3), migrations.stream().map(Migration::version).toList());
        assertEquals("create shop tables", migrations.get(0).description());
        assertEquals(ShopSchema.script(), migrations.get(0).script());
    }

    @Test
    void indexTxtListsEveryMigrationFileInTheFolder() throws IOException {
        Path folder = Path.of("src", "main", "resources", "db", "migration"); // the module folder is the working directory
        List<String> listed = Files.readAllLines(folder.resolve("index.txt")).stream().filter(l -> !l.isBlank()).toList();
        try (Stream<Path> files = Files.list(folder)) {
            List<String> onDisk = files.map(p -> p.getFileName().toString()).filter(n -> n.endsWith(".sql")).sorted().toList();
            assertEquals(onDisk, listed.stream().sorted().toList(), "index.txt must list every V*.sql file (and only those)");
        }
    }

    // ---- the runner

    @Test
    void aNewDatabaseGetsEveryMigration() throws SQLException {
        assertEquals(3, Migrator.standard(database).migrate());
        assertEquals(3, Migrator.standard(database).currentVersion());
        assertEquals(List.of("1", "2", "3"), query("SELECT version FROM schema_version ORDER BY version").column("version"));
        assertEquals(List.of("create shop tables", "add customer email", "add product active flag"),
                query("SELECT description FROM schema_version ORDER BY version").column("description"));
        // V1 made the tables, V2 and V3 added a column each
        assertEquals(1, query("SELECT COUNT(*) AS n FROM information_schema.columns WHERE table_name = 'CUSTOMER' AND column_name = 'EMAIL'").rowCount());
        assertEquals("1", query("SELECT COUNT(*) AS n FROM information_schema.columns WHERE table_name = 'PRODUCT' AND column_name = 'ACTIVE'").cell(0, 0));
    }

    @Test
    void runningMigrateAgainDoesNothing() {
        assertEquals(3, Migrator.standard(database).migrate());
        assertEquals(0, Migrator.standard(database).migrate());
    }

    @Test
    void aDatabaseAtVersionTwoOnlyGetsVersionThree() throws SQLException {
        List<Migration> all = Migrations.fromClasspath();
        assertEquals(2, new Migrator(database, all.subList(0, 2), java.time.Clock.systemUTC()).migrate()); // the "old" app
        assertEquals(1, Migrator.standard(database).migrate());                                             // the new app
        assertEquals(3, Migrator.standard(database).currentVersion());
    }

    @Test
    void existingDataSurvivesAnUpgradeAndGetsTheDefaults() throws SQLException {
        List<Migration> all = Migrations.fromClasspath();
        new Migrator(database, all.subList(0, 1), java.time.Clock.systemUTC()).migrate(); // the shop runs on version 1 for a while ...
        new JdbcProductRepository(database).add(new Product("Rice 25kg", Money.of("1250.50"), 10, 17));
        SqlRunner.execute(database.connection(), "INSERT INTO customer (name, phone) VALUES ('Ana Reyes', '0917')");

        assertEquals(2, Migrator.standard(database).migrate());                           // ... then the app is upgraded
        QueryResult product = query("SELECT name, quantity, active FROM product");
        assertEquals(List.of("Rice 25kg", "17", "TRUE"), product.rows().get(0));          // old row, new column with its DEFAULT
        assertEquals(List.of("Ana Reyes", "NULL"), query("SELECT name, email FROM customer").rows().get(0)); // optional column: empty
    }

    @Test
    void theHistoryRemembersWhenEachMigrationWasApplied() throws SQLException {
        java.time.Clock fixed = java.time.Clock.fixed(java.time.Instant.parse("2026-03-01T01:30:00Z"), Inventory.SHOP_ZONE);
        new Migrator(database, Migrations.fromClasspath(), fixed).migrate();
        assertEquals("2026-03-01 09:30:00", query("SELECT applied_at FROM schema_version WHERE version = 1").cell(0, 0));
    }

    @Test
    void editingAnAppliedMigrationIsRefused() {
        new Migrator(database, List.of(m(1, "CREATE TABLE a (x INT)")), java.time.Clock.systemUTC()).migrate();
        Migrator edited = new Migrator(database, List.of(m(1, "CREATE TABLE a (x INT, y INT)")), java.time.Clock.systemUTC());
        MigrationException e = assertThrows(MigrationException.class, edited::migrate);
        assertTrue(e.getMessage().contains("V1"));
        assertTrue(e.getMessage().contains("changed after it was applied"));
    }

    @Test
    void aDatabaseNewerThanTheAppIsRefused() {
        new Migrator(database, List.of(m(1, "CREATE TABLE a (x INT)"), m(2, "CREATE TABLE b (x INT)")), java.time.Clock.systemUTC()).migrate();
        Migrator oldApp = new Migrator(database, List.of(m(1, "CREATE TABLE a (x INT)")), java.time.Clock.systemUTC());
        MigrationException e = assertThrows(MigrationException.class, oldApp::migrate);
        assertTrue(e.getMessage().contains("database is at version 2"));
    }

    @Test
    void aFailingMigrationStopsTheRunAndIsNotRecorded() throws SQLException {
        Migrator broken = new Migrator(database, List.of(
                m(1, "CREATE TABLE a (x INT)"),
                m(2, "ALTER TABLE a ADD COLUMN y INT"),
                m(3, "ALTER TABLE does_not_exist ADD COLUMN z INT"),
                m(4, "CREATE TABLE never_reached (x INT)")), java.time.Clock.systemUTC());
        MigrationException e = assertThrows(MigrationException.class, broken::migrate);
        assertTrue(e.getMessage().startsWith("Migration V3"));
        assertEquals(List.of("1", "2"), query("SELECT version FROM schema_version ORDER BY version").column("version"));
        assertEquals(0, query("SELECT * FROM information_schema.tables WHERE table_name = 'NEVER_REACHED'").rowCount());
    }

    @Test
    void afterTheFailureIsFixedTheRunContinuesFromWhereItStopped() {
        List<Migration> broken = List.of(m(1, "CREATE TABLE a (x INT)"), m(2, "ALTER TABLE nope ADD COLUMN z INT"));
        assertThrows(MigrationException.class, () -> new Migrator(database, broken, java.time.Clock.systemUTC()).migrate());
        List<Migration> fixed = List.of(m(1, "CREATE TABLE a (x INT)"), m(2, "ALTER TABLE a ADD COLUMN z INT"));
        assertEquals(1, new Migrator(database, fixed, java.time.Clock.systemUTC()).migrate());
    }

    @Test
    void theMigratedSchemaHasTheShapeOfTheDesignedOne() throws SQLException {
        Migrator.standard(database).migrate();
        try (Connection practice = PracticeDb.open()) {
            // V1 is the designed schema of lesson 28; V2 and V3 only add the columns email and active
            String expected = Erd.mermaid(practice)
                    .replace("        string phone\n    }", "        string phone\n        string email\n    }")
                    .replace("        int quantity\n    }\n    sale {", "        int quantity\n        boolean active\n    }\n    sale {");
            String migrated = Erd.mermaid(database.connection()).replace("    schema_version {\n        int version PK\n        string description\n        bigint checksum\n        timestamp applied_at\n    }\n", "");
            assertEquals(expected, migrated);
        }
    }
}
