package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class Exercises32Test {

    @Test
    void versionOfReadsTheNumber() {
        assertEquals(1, MigrationDrills.versionOf("V1__create_shop_tables.sql"));
        assertEquals(12, MigrationDrills.versionOf("V12__add_customer_email.sql"));
    }

    @Test
    void versionOfRejectsOtherNames() {
        assertThrows(IllegalArgumentException.class, () -> MigrationDrills.versionOf("create_tables.sql"));
        assertThrows(IllegalArgumentException.class, () -> MigrationDrills.versionOf("V2_one_underscore.sql"));
        assertThrows(IllegalArgumentException.class, () -> MigrationDrills.versionOf("V2__no_extension"));
        assertThrows(IllegalArgumentException.class, () -> MigrationDrills.versionOf("Vx__letters.sql"));
    }

    @Test
    void descriptionOfTurnsUnderscoresIntoSpaces() {
        assertEquals("add customer email", MigrationDrills.descriptionOf("V2__add_customer_email.sql"));
        assertEquals("x", MigrationDrills.descriptionOf("V10__x.sql"));
    }

    @Test
    void pendingKeepsOnlyNewerFilesOldestFirst() {
        List<String> files = List.of("V3__c.sql", "V1__a.sql", "V2__b.sql");
        assertEquals(List.of("V2__b.sql", "V3__c.sql"), MigrationDrills.pending(files, 1));
        assertEquals(List.of(), MigrationDrills.pending(files, 3));
        assertEquals(List.of("V1__a.sql", "V2__b.sql", "V3__c.sql"), MigrationDrills.pending(files, 0));
    }

    @Test
    void pendingSortsByNumberNotByText() {
        List<String> files = List.of("V10__j.sql", "V9__i.sql", "V2__b.sql", "V1__a.sql", "V3__c.sql", "V4__d.sql", "V5__e.sql",
                "V6__f.sql", "V7__g.sql", "V8__h.sql");
        assertEquals(List.of("V9__i.sql", "V10__j.sql"), MigrationDrills.pending(files, 8));
    }

    @Test
    void hasGapNoticesAMissingVersionADuplicateOrAWrongStart() {
        assertFalse(MigrationDrills.hasGap(List.of("V2__b.sql", "V1__a.sql", "V3__c.sql")));
        assertTrue(MigrationDrills.hasGap(List.of("V1__a.sql", "V3__c.sql")));
        assertTrue(MigrationDrills.hasGap(List.of("V1__a.sql", "V2__b.sql", "V2__again.sql")));
        assertTrue(MigrationDrills.hasGap(List.of("V2__b.sql", "V3__c.sql")));
        assertFalse(MigrationDrills.hasGap(List.of()));
    }

    @Test
    void checksumChangesWithTheTextButNotWithTheLineEndings() {
        assertNotEquals(MigrationDrills.checksum("CREATE TABLE a (x INT);"), MigrationDrills.checksum("CREATE TABLE a (y INT);"));
        assertEquals(MigrationDrills.checksum("A;\nB;\n"), MigrationDrills.checksum("A;\r\nB;\r\n"));
        assertEquals(MigrationDrills.checksum("same"), MigrationDrills.checksum("same"));
    }

    @Test
    void createVersionTableBuildsATableThatKeepsVersionsUnique() throws SQLException {
        try (Connection db = DriverManager.getConnection("jdbc:h2:mem:l32-" + UUID.randomUUID()); Statement statement = db.createStatement()) {
            statement.execute(MigrationDrills.createVersionTable());
            statement.execute("INSERT INTO schema_version (version, description, applied_at) VALUES (1, 'create tables', TIMESTAMP '2026-03-01 09:00:00')");
            assertThrows(SQLException.class, () -> statement.execute(
                    "INSERT INTO schema_version (version, description, applied_at) VALUES (1, 'again', TIMESTAMP '2026-03-01 09:00:00')"));
            assertThrows(SQLException.class, () -> statement.execute("INSERT INTO schema_version (version, applied_at) VALUES (2, TIMESTAMP '2026-03-01 09:00:00')"));
        }
    }
}
