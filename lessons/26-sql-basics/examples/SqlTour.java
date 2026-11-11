// Run with:  java -cp <path to the H2 jar> lessons/26-sql-basics/examples/SqlTour.java   (from the repository root)
// A guided tour of the SQL you need every day. It builds the practice shop in memory, then runs one statement at a time
// and prints the statement and its answer. Change a statement and run it again: nothing is saved anywhere.
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

public class SqlTour {

    public static void main(String[] args) throws Exception {
        Path sql = Path.of("lessons/26-sql-basics/solution/src/main/resources/sql");
        try (Connection db = DriverManager.getConnection("jdbc:h2:mem:sql-tour")) {
            for (String file : List.of("schema.sql", "seed.sql")) {
                for (String statement : Files.readString(sql.resolve(file)).replaceAll("(?m)^--.*$", "").split(";")) {
                    if (!statement.isBlank()) {
                        run(db, statement);
                    }
                }
            }
            step(db, "Pick columns and rows", "SELECT name, price FROM product WHERE price < 100 ORDER BY price DESC");
            step(db, "Several conditions: AND, OR, NOT, parentheses", "SELECT name, quantity FROM product WHERE quantity < 10 AND NOT (price > 1000)");
            step(db, "Text search: LIKE (% means any characters), case-insensitive with LOWER", "SELECT name FROM product WHERE LOWER(name) LIKE '%oil%' OR name LIKE 'S%'");
            step(db, "A list of values: IN, and a range: BETWEEN", "SELECT name, price FROM product WHERE id IN (1, 3) OR price BETWEEN 20 AND 40 ORDER BY id");
            step(db, "Work out a new column with an expression and give it a name (alias)", "SELECT name, price * quantity AS stock_value FROM product ORDER BY stock_value DESC LIMIT 3");
            step(db, "CASE: the status rule of StockChecker, in SQL", "SELECT name, quantity, CASE WHEN quantity <= 0 THEN 'OUT' WHEN quantity <= reorder_level THEN 'LOW' ELSE 'OK' END AS status FROM product ORDER BY id");
            step(db, "Many rows in, one row out: COUNT, SUM, AVG, MIN, MAX", "SELECT COUNT(*) AS products, SUM(quantity) AS units, MIN(price) AS cheapest, MAX(price) AS dearest FROM product");
            step(db, "GROUP BY: one answer per group", "SELECT payment, COUNT(*) AS sales FROM sale GROUP BY payment ORDER BY payment");
            step(db, "NULL means 'no value': ask with IS NULL, never = NULL", "SELECT id, sold_at FROM sale WHERE customer_id IS NULL");
            step(db, "INSERT adds a row (the database numbers the id)", "INSERT INTO product (name, price, reorder_level, quantity) VALUES ('Coffee Sachet', 8.50, 50, 0)");
            step(db, "UPDATE changes the rows its WHERE picks (no WHERE = every row!)", "UPDATE product SET quantity = quantity + 10 WHERE name = 'Soy Sauce 500ml'");
            step(db, "DELETE removes the rows its WHERE picks", "DELETE FROM product WHERE name = 'Coffee Sachet'");
            step(db, "Check the result", "SELECT name, quantity FROM product WHERE name LIKE 'Soy%' OR name LIKE 'Coffee%'");
        }
    }

    static int stepNumber = 0;

    static void step(Connection db, String title, String sql) throws SQLException {
        System.out.println("-- " + (++stepNumber) + ". " + title);
        System.out.println(sql + ";");
        try (Statement statement = db.createStatement()) {
            if (statement.execute(sql)) {
                try (ResultSet rows = statement.getResultSet()) {
                    print(rows);
                }
            } else {
                int n = statement.getUpdateCount();
                System.out.println("(" + n + (n == 1 ? " row" : " rows") + " updated)");
            }
        }
        System.out.println();
    }

    static void run(Connection db, String sql) throws SQLException {
        try (Statement statement = db.createStatement()) {
            statement.execute(sql);
        }
    }

    static void print(ResultSet rows) throws SQLException {
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
        System.out.println("(" + (data.size() - 1) + (data.size() == 2 ? " row)" : " rows)"));
    }
}
