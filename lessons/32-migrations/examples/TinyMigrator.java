// Run with:  java -cp <path to the H2 jar> lessons/32-migrations/examples/TinyMigrator.java   (from the repository root)
// A database migration runner in 60 lines: remember which numbered scripts were applied, run only the new ones, and
// refuse to start if an applied script was edited. The shop's real runner (Migrator.java) does the same with more care.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

public class TinyMigrator {

    record Migration(int version, String description, String sql) {
        long checksum() {
            CRC32 crc = new CRC32();
            crc.update(sql.getBytes());
            return crc.getValue();
        }
    }

    public static void main(String[] args) throws SQLException {
        List<Migration> v1v2 = List.of(
                new Migration(1, "create product", "CREATE TABLE product (id INT PRIMARY KEY, name VARCHAR(40) NOT NULL)"),
                new Migration(2, "add price", "ALTER TABLE product ADD COLUMN price DECIMAL(10,2) NOT NULL DEFAULT 0.00"));
        List<Migration> v1v2v3 = List.of(v1v2.get(0), v1v2.get(1),
                new Migration(3, "add reorder level", "ALTER TABLE product ADD COLUMN reorder_level INT NOT NULL DEFAULT 10"));
        List<Migration> v1Edited = List.of(
                new Migration(1, "create product", "CREATE TABLE product (id INT PRIMARY KEY, name VARCHAR(80) NOT NULL)"), v1v2.get(1));

        try (Connection db = DriverManager.getConnection("jdbc:h2:mem:tiny-migrator")) {
            System.out.println("Day 1: a new laptop, the app knows migrations 1 and 2");
            migrate(db, v1v2);
            run(db, "INSERT INTO product (id, name, price) VALUES (1, 'Rice 25kg', 1250.50)");

            System.out.println("\nDay 2: the same database, the app is upgraded and now knows 1, 2 and 3");
            migrate(db, v1v2v3);
            System.out.println("Row 1 after the upgrade: " + one(db, "SELECT name || ', ' || price || ', reorder ' || reorder_level FROM product WHERE id = 1"));

            System.out.println("\nDay 3: nothing new");
            migrate(db, v1v2v3);

            System.out.println("\nDay 4: someone edits migration 1 after it was applied");
            try {
                migrate(db, v1Edited);
            } catch (IllegalStateException e) {
                System.out.println("REFUSED: " + e.getMessage());
            }
        }
    }

    static void migrate(Connection db, List<Migration> known) throws SQLException {
        run(db, "CREATE TABLE IF NOT EXISTS schema_version (version INT PRIMARY KEY, description VARCHAR(100), checksum BIGINT)");
        Map<Integer, Long> applied = new LinkedHashMap<>();
        try (Statement s = db.createStatement(); ResultSet rows = s.executeQuery("SELECT version, checksum FROM schema_version ORDER BY version")) {
            while (rows.next()) {
                applied.put(rows.getInt(1), rows.getLong(2));
            }
        }
        for (Migration m : known) { // 1. history must match the files
            if (applied.containsKey(m.version()) && applied.get(m.version()) != m.checksum()) {
                throw new IllegalStateException("migration " + m.version() + " (" + m.description() + ") was changed after it was applied");
            }
        }
        int count = 0;
        for (Migration m : known) { // 2. apply what is new
            if (!applied.containsKey(m.version())) {
                run(db, m.sql());
                try (PreparedStatement s = db.prepareStatement("INSERT INTO schema_version VALUES (?, ?, ?)")) {
                    s.setInt(1, m.version());
                    s.setString(2, m.description());
                    s.setLong(3, m.checksum());
                    s.executeUpdate();
                }
                System.out.println("  applied V" + m.version() + ": " + m.description());
                count++;
            }
        }
        System.out.println("  " + (count == 0 ? "up to date" : count + " applied") + ", database is at version " + one(db, "SELECT MAX(version) FROM schema_version"));
    }

    static void run(Connection db, String sql) throws SQLException {
        try (Statement statement = db.createStatement()) {
            statement.execute(sql);
        }
    }

    static String one(Connection db, String sql) throws SQLException {
        try (Statement statement = db.createStatement(); ResultSet rows = statement.executeQuery(sql)) {
            rows.next();
            return rows.getString(1);
        }
    }
}
