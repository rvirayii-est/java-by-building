// Run with:  java -cp <path to the H2 jar> lessons/27-joins-and-reports/examples/JoinsDemo.java   (from the repository root)
// Four ways to combine sale, customer and sale_line. Read each answer, then ask: which rows are missing, which are doubled?
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JoinsDemo {

    public static void main(String[] args) throws Exception {
        Path sql = Path.of("lessons/27-joins-and-reports/solution/src/main/resources/sql");
        try (Connection db = DriverManager.getConnection("jdbc:h2:mem:joins-demo")) {
            for (String file : List.of("schema.sql", "seed.sql")) {
                for (String statement : Files.readString(sql.resolve(file)).replaceAll("(?m)^--.*$", "").split(";")) {
                    if (!statement.isBlank()) {
                        run(db, statement);
                    }
                }
            }
            ask(db, "1. INNER JOIN: only sales that HAVE a customer (walk-in sales 2 and 5 disappear)",
                    "SELECT s.id AS sale, c.name AS customer FROM sale s JOIN customer c ON c.id = s.customer_id ORDER BY s.id");
            ask(db, "2. LEFT JOIN: every sale, with NULL where there is no customer",
                    "SELECT s.id AS sale, c.name AS customer FROM sale s LEFT JOIN customer c ON c.id = s.customer_id ORDER BY s.id");
            ask(db, "3. LEFT JOIN + IS NULL: customers who bought nothing (rows with NO match)",
                    "SELECT c.name AS customer FROM customer c LEFT JOIN sale s ON s.customer_id = c.id WHERE s.id IS NULL");
            ask(db, "4. Three tables: one receipt line per row (sale 4)",
                    "SELECT s.id AS sale, COALESCE(c.name, 'Walk-in') AS customer, p.name AS product, l.quantity, l.unit_price * l.quantity AS line_total "
                            + "FROM sale s LEFT JOIN customer c ON c.id = s.customer_id JOIN sale_line l ON l.sale_id = s.id "
                            + "JOIN product p ON p.id = l.product_id WHERE s.id = 4 ORDER BY l.id");
            ask(db, "5. The classic mistake: a join with no ON gives EVERY pairing (6 products x 3 customers)",
                    "SELECT COUNT(*) AS rows_produced FROM product, customer");
            ask(db, "6. Another trap: after joining sale_line there is one row per LINE, so COUNT(*) counts lines, not sales",
                    "SELECT COUNT(*) AS lines, COUNT(DISTINCT s.id) AS sales FROM sale s JOIN sale_line l ON l.sale_id = s.id");
            ask(db, "7. A report: revenue per day (GROUP BY a calendar day)",
                    "SELECT CAST(s.sold_at AS DATE) AS sale_day, COUNT(DISTINCT s.id) AS sales, SUM(l.unit_price * l.quantity) AS revenue "
                            + "FROM sale s JOIN sale_line l ON l.sale_id = s.id GROUP BY CAST(s.sold_at AS DATE) ORDER BY sale_day");
        }
    }

    static void run(Connection db, String sql) throws SQLException {
        try (Statement statement = db.createStatement()) {
            statement.execute(sql);
        }
    }

    static void ask(Connection db, String title, String sql) throws SQLException {
        System.out.println(title);
        try (Statement statement = db.createStatement(); ResultSet rows = statement.executeQuery(sql)) {
            ResultSetMetaData meta = rows.getMetaData();
            int columns = meta.getColumnCount();
            List<String[]> data = new ArrayList<>();
            String[] header = new String[columns];
            for (int c = 0; c < columns; c++) {
                header[c] = meta.getColumnLabel(c + 1).toLowerCase();
            }
            data.add(header);
            while (rows.next()) {
                String[] row = new String[columns];
                for (int c = 0; c < columns; c++) {
                    String value = rows.getString(c + 1);
                    row[c] = value == null ? "NULL" : value;
                }
                data.add(row);
            }
            int[] width = new int[columns];
            for (String[] row : data) {
                for (int c = 0; c < columns; c++) {
                    width[c] = Math.max(width[c], row[c].length());
                }
            }
            for (int r = 0; r < data.size(); r++) {
                StringBuilder line = new StringBuilder();
                for (int c = 0; c < columns; c++) {
                    line.append(c == 0 ? "" : " | ").append(c < columns - 1 ? String.format("%-" + width[c] + "s", data.get(r)[c]) : data.get(r)[c]);
                }
                System.out.println(line);
                if (r == 0) {
                    StringBuilder rule = new StringBuilder();
                    for (int c = 0; c < columns; c++) {
                        rule.append(c == 0 ? "" : "-+-").append("-".repeat(width[c]));
                    }
                    System.out.println(rule);
                }
            }
            System.out.println("(" + (data.size() - 1) + (data.size() == 2 ? " row)" : " rows)") + "\n");
        }
    }
}
