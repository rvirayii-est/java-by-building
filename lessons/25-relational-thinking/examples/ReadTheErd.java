// Run with:  java -cp <path to the H2 jar> lessons/25-relational-thinking/examples/ReadTheErd.java   (from the repository root)
// Builds the shop's tables from the schema file, then ASKS the database how its tables are connected.
// This is how a tool draws an ERD for you: the answers live in the database itself.
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReadTheErd {

    public static void main(String[] args) throws Exception {
        Path schema = Path.of("lessons/25-relational-thinking/solution/src/main/resources/sql/schema.sql");
        try (Connection db = DriverManager.getConnection("jdbc:h2:mem:read-the-erd");
             Statement statement = db.createStatement()) {
            for (String create : Files.readString(schema).replaceAll("(?m)^--.*$", "").split(";")) {
                if (!create.isBlank()) {
                    statement.execute(create);
                }
            }
            DatabaseMetaData meta = db.getMetaData();
            List<String> tables = new ArrayList<>();
            try (ResultSet rs = meta.getTables(null, "PUBLIC", "%", new String[] {"BASE TABLE"})) {
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME"));
                }
            }
            tables.sort(null);
            System.out.println("Tables: " + tables.size());
            for (String table : tables) {
                System.out.println("\n" + table.toLowerCase());
                try (ResultSet keys = meta.getPrimaryKeys(null, "PUBLIC", table)) {
                    while (keys.next()) {
                        System.out.println("  primary key : " + keys.getString("COLUMN_NAME").toLowerCase());
                    }
                }
                try (ResultSet fks = meta.getImportedKeys(null, "PUBLIC", table)) {
                    while (fks.next()) {
                        System.out.println("  foreign key : " + fks.getString("FKCOLUMN_NAME").toLowerCase() + " -> "
                                + fks.getString("PKTABLE_NAME").toLowerCase() + "." + fks.getString("PKCOLUMN_NAME").toLowerCase());
                    }
                }
            }
        }
    }
}
