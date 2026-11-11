// Run with:  java -cp <path to the H2 jar> lessons/25-relational-thinking/examples/FlatVsRelational.java   (from the repository root)
// The same shop data stored twice: as one flat sheet (like a spreadsheet), then as related tables.
// Watch what goes wrong with the sheet when ONE fact (Ana's phone number) must change.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FlatVsRelational {

    public static void main(String[] args) throws SQLException {
        try (Connection db = DriverManager.getConnection("jdbc:h2:mem:flat-vs-relational")) {
            System.out.println("== 1. One flat sheet: every row repeats the customer and the product details");
            run(db, "CREATE TABLE sheet (sale_no INT, customer VARCHAR(40), phone VARCHAR(20), product VARCHAR(40), price DECIMAL(10,2), qty INT)");
            run(db, "INSERT INTO sheet VALUES (1, 'Ana Reyes', '0917 123 4567', 'Rice 25kg', 1250.50, 2),"
                    + " (4, 'Ana Reyes', '0917 123 4567', 'Rice 25kg', 1250.50, 1),"
                    + " (4, 'Ana Reyes', '0917 123 4567', 'Sardines 155g', 24.00, 4)");
            show(db, "SELECT * FROM sheet ORDER BY sale_no, product");

            System.out.println("\n== 2. Ana gets a new phone. Somebody fixes only the first row ...");
            run(db, "UPDATE sheet SET phone = '0999 000 1111' WHERE sale_no = 1");
            show(db, "SELECT DISTINCT customer, phone FROM sheet ORDER BY phone");
            System.out.println("Two phone numbers for one person. The sheet now disagrees with itself (an update anomaly).");

            System.out.println("\n== 3. Related tables: each fact is stored ONCE, rows point at each other by id");
            run(db, "CREATE TABLE customer (id INT PRIMARY KEY, name VARCHAR(40), phone VARCHAR(20))");
            run(db, "CREATE TABLE product (id INT PRIMARY KEY, name VARCHAR(40), price DECIMAL(10,2))");
            run(db, "CREATE TABLE sale (no INT PRIMARY KEY, customer_id INT REFERENCES customer (id))");
            run(db, "CREATE TABLE sale_line (sale_no INT REFERENCES sale (no), product_id INT REFERENCES product (id), qty INT)");
            run(db, "INSERT INTO customer VALUES (1, 'Ana Reyes', '0917 123 4567')");
            run(db, "INSERT INTO product VALUES (1, 'Rice 25kg', 1250.50), (2, 'Sardines 155g', 24.00)");
            run(db, "INSERT INTO sale VALUES (1, 1), (4, 1)");
            run(db, "INSERT INTO sale_line VALUES (1, 1, 2), (4, 1, 1), (4, 2, 4)");

            System.out.println("Ana changes her phone: ONE row to update.");
            run(db, "UPDATE customer SET phone = '0999 000 1111' WHERE id = 1");
            show(db, "SELECT * FROM customer");
            System.out.println("\nThe receipt is put back together by following the ids (a JOIN, lesson 27):");
            show(db, "SELECT s.no AS sale, c.name AS customer, c.phone, p.name AS product, l.qty "
                    + "FROM sale s JOIN customer c ON c.id = s.customer_id "
                    + "JOIN sale_line l ON l.sale_no = s.no JOIN product p ON p.id = l.product_id "
                    + "ORDER BY s.no, p.name");

            System.out.println("\n== 4. The database refuses a row that points at nothing");
            try {
                run(db, "INSERT INTO sale VALUES (9, 77)");
            } catch (SQLException e) {
                System.out.println("Refused: no customer 77 exists (SQL state " + e.getSQLState() + ")");
            }
        }
    }

    // ---- tiny helpers: run a statement, or run a query and print it as a table
    static void run(Connection db, String sql) throws SQLException {
        try (Statement statement = db.createStatement()) {
            statement.execute(sql);
        }
    }

    static void show(Connection db, String sql) throws SQLException {
        try (Statement statement = db.createStatement(); ResultSet rows = statement.executeQuery(sql)) {
            ResultSetMetaData meta = rows.getMetaData();
            List<String[]> data = new ArrayList<>();
            String[] header = new String[meta.getColumnCount()];
            for (int c = 0; c < header.length; c++) {
                header[c] = meta.getColumnLabel(c + 1).toLowerCase();
            }
            data.add(header);
            while (rows.next()) {
                String[] row = new String[header.length];
                for (int c = 0; c < row.length; c++) {
                    row[c] = String.valueOf(rows.getString(c + 1)).replace("null", "NULL");
                }
                data.add(row);
            }
            int[] width = new int[header.length];
            for (String[] row : data) {
                for (int c = 0; c < width.length; c++) {
                    width[c] = Math.max(width[c], row[c].length());
                }
            }
            for (int r = 0; r < data.size(); r++) {
                StringBuilder line = new StringBuilder();
                for (int c = 0; c < width.length; c++) {
                    line.append(c == 0 ? "" : " | ").append(c < width.length - 1 ? String.format("%-" + width[c] + "s", data.get(r)[c]) : data.get(r)[c]);
                }
                System.out.println(line);
                if (r == 0) {
                    StringBuilder rule = new StringBuilder();
                    for (int c = 0; c < width.length; c++) {
                        rule.append(c == 0 ? "" : "-+-").append("-".repeat(width[c]));
                    }
                    System.out.println(rule);
                }
            }
            System.out.println("(" + (data.size() - 1) + (data.size() == 2 ? " row)" : " rows)"));
        }
    }
}
