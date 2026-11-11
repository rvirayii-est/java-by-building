// Run with:  java -cp <path to the H2 jar> lessons/32-migrations/examples/ShopUpgrade.java   (from the repository root)
// The real migration files of the shop (src/main/resources/db/migration), applied by hand in order to a FILE database
// that has live data in it: the data survives each step. Works in a temporary folder and cleans up after itself.
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Stream;

public class ShopUpgrade {

    public static void main(String[] args) throws Exception {
        Path migrations = Path.of("lessons/32-migrations/solution/src/main/resources/db/migration");
        Path folder = Files.createTempDirectory("shop-upgrade");
        String url = "jdbc:h2:file:" + folder.resolve("shop").toString().replace('\\', '/');
        try {
            try (Connection db = DriverManager.getConnection(url)) {
                System.out.println("== Version 1 is applied, and the shop trades for a while");
                apply(db, migrations.resolve("V1__create_shop_tables.sql"));
                run(db, "INSERT INTO product (name, price, reorder_level, quantity) VALUES ('Rice 25kg', 1250.50, 10, 17), ('Sugar 1kg', 75.00, 10, 4)");
                run(db, "INSERT INTO customer (name, phone) VALUES ('Ana Reyes', '09171234567')");
                show(db, "SELECT name, price, quantity FROM product ORDER BY id");
                show(db, "SELECT name, phone FROM customer");
            }
            try (Connection db = DriverManager.getConnection(url)) { // the program was closed and started again
                System.out.println("\n== Next morning: the new app brings migrations 2 and 3");
                apply(db, migrations.resolve("V2__add_customer_email.sql"));
                apply(db, migrations.resolve("V3__add_product_active_flag.sql"));
                show(db, "SELECT name, price, quantity, active FROM product ORDER BY id");
                show(db, "SELECT name, phone, email FROM customer");
                System.out.println("\nSame rows as before. The new columns are filled by their DEFAULT (active) or left empty (email).");
            }
        } finally {
            try (Stream<Path> files = Files.list(folder)) {
                for (Path f : (Iterable<Path>) files::iterator) {
                    Files.delete(f);
                }
            }
            Files.delete(folder);
        }
    }

    static void apply(Connection db, Path file) throws Exception {
        for (String statement : Files.readString(file).replaceAll("(?m)^--.*$", "").split(";")) {
            if (!statement.isBlank()) {
                run(db, statement);
            }
        }
        System.out.println("applied " + file.getFileName());
    }

    static void run(Connection db, String sql) throws SQLException {
        try (Statement statement = db.createStatement()) {
            statement.execute(sql);
        }
    }

    static void show(Connection db, String sql) throws SQLException {
        try (Statement statement = db.createStatement(); ResultSet rows = statement.executeQuery(sql)) {
            ResultSetMetaData meta = rows.getMetaData();
            StringBuilder header = new StringBuilder("  ");
            for (int c = 1; c <= meta.getColumnCount(); c++) {
                header.append(c > 1 ? " | " : "").append(meta.getColumnLabel(c).toLowerCase());
            }
            System.out.println(header);
            while (rows.next()) {
                StringBuilder line = new StringBuilder("  ");
                for (int c = 1; c <= meta.getColumnCount(); c++) {
                    line.append(c > 1 ? " | " : "").append(rows.getString(c) == null ? "NULL" : rows.getString(c));
                }
                System.out.println(line);
            }
        }
    }
}
