// Run with:  java -cp <path to the H2 jar> lessons/29-jdbc/examples/FileDatabase.java   (from the repository root)
// An in-memory database forgets everything when it closes. A FILE database remembers: this is what the shop app uses.
// The example works in a temporary folder and removes it at the end, so it leaves nothing behind.
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Stream;

public class FileDatabase {

    public static void main(String[] args) throws Exception {
        Path folder = Files.createTempDirectory("shop-db-demo");
        String url = "jdbc:h2:file:" + folder.resolve("shop").toString().replace('\\', '/'); // H2 adds .mv.db to the name
        try {
            System.out.println("First run: create the table and save two rows");
            try (Connection db = DriverManager.getConnection(url); Statement statement = db.createStatement()) {
                statement.execute("CREATE TABLE product (name VARCHAR(60) PRIMARY KEY, quantity INT NOT NULL)");
                statement.execute("INSERT INTO product VALUES ('Rice 25kg', 17), ('Sugar 1kg', 4)");
            }
            System.out.println("  The program has ended its connection. Files in the folder:");
            try (Stream<Path> files = Files.list(folder)) {
                files.map(p -> "    " + p.getFileName()).sorted().forEach(System.out::println);
            }

            System.out.println("Second run: a new connection finds the same data");
            try (Connection db = DriverManager.getConnection(url);
                 Statement statement = db.createStatement();
                 ResultSet rows = statement.executeQuery("SELECT name, quantity FROM product ORDER BY name")) {
                while (rows.next()) {
                    System.out.println("    " + rows.getString("name") + ": " + rows.getInt("quantity"));
                }
            }
        } finally {
            deleteAll(folder);
        }
        System.out.println("Temporary folder removed: " + !Files.exists(folder));
    }

    static void deleteAll(Path folder) throws IOException {
        try (Stream<Path> files = Files.list(folder)) {
            for (Path file : (Iterable<Path>) files::iterator) {
                Files.delete(file);
            }
        }
        Files.delete(folder);
    }
}
