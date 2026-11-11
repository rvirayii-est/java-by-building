package dev.javabybuilding.inventory;

import java.util.UUID;

/** Test helper: a brand-new in-memory database with the shop's tables. Unique name, nothing written to disk, gone when closed. */
final class TestDb {

    private TestDb() {}

    static Database fresh() {
        Database database = Database.openMemory("test-" + UUID.randomUUID());
        database.createSchemaIfMissing();
        return database;
    }
}
