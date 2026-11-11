// Run with:  java -cp <path to the H2 jar> lessons/27-joins-and-reports/examples/DailyReport.java   (from the repository root)
// SQL finds and adds up the rows; Java only lays out the page. A printed shop report for 1-3 March 2026, receipt by receipt.
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DailyReport {

    public static void main(String[] args) throws Exception {
        Path sql = Path.of("lessons/27-joins-and-reports/solution/src/main/resources/sql");
        try (Connection db = DriverManager.getConnection("jdbc:h2:mem:daily-report")) {
            for (String file : List.of("schema.sql", "seed.sql")) {
                for (String statement : Files.readString(sql.resolve(file)).replaceAll("(?m)^--.*$", "").split(";")) {
                    if (!statement.isBlank()) {
                        try (Statement s = db.createStatement()) {
                            s.execute(statement);
                        }
                    }
                }
            }
            System.out.println("=== SHOP SALES REPORT, 1-3 March 2026 ===");
            try (Statement days = db.createStatement();
                 ResultSet day = days.executeQuery("SELECT CAST(sold_at AS DATE) AS d FROM sale GROUP BY CAST(sold_at AS DATE) ORDER BY d")) {
                while (day.next()) {
                    System.out.println("\n" + day.getString("d"));
                    printSales(db, day.getString("d"));
                }
            }
            System.out.println("\n--- Revenue by product ---");
            try (Statement s = db.createStatement();
                 ResultSet rs = s.executeQuery("SELECT p.name, SUM(l.quantity) AS units, SUM(l.unit_price * l.quantity) AS revenue "
                         + "FROM sale_line l JOIN product p ON p.id = l.product_id GROUP BY p.name ORDER BY revenue DESC")) {
                while (rs.next()) {
                    System.out.printf("%-16s %3d units %12s%n", rs.getString("name"), rs.getInt("units"), rs.getBigDecimal("revenue").toPlainString());
                }
            }
            try (Statement s = db.createStatement();
                 ResultSet rs = s.executeQuery("SELECT SUM(unit_price * quantity) AS total FROM sale_line")) {
                rs.next();
                System.out.printf("%-16s %-9s %12s%n", "TOTAL (before discounts)", "", rs.getBigDecimal("total").toPlainString());
            }
        }
    }

    static void printSales(Connection db, String day) throws SQLException {
        String sales = "SELECT s.id, COALESCE(c.name, 'Walk-in') AS customer, s.payment FROM sale s "
                + "LEFT JOIN customer c ON c.id = s.customer_id WHERE CAST(s.sold_at AS DATE) = DATE '" + day + "' ORDER BY s.id";
        try (Statement st = db.createStatement(); ResultSet sale = st.executeQuery(sales)) {
            while (sale.next()) {
                System.out.println("  Sale #" + sale.getInt("id") + " - " + sale.getString("customer") + " (" + sale.getString("payment") + ")");
                try (Statement lines = db.createStatement();
                     ResultSet line = lines.executeQuery("SELECT p.name, l.quantity, l.unit_price FROM sale_line l JOIN product p ON p.id = l.product_id "
                             + "WHERE l.sale_id = " + sale.getInt("id") + " ORDER BY l.id")) {
                    while (line.next()) {
                        System.out.printf("    %-16s %3d x %9s%n", line.getString("name"), line.getInt("quantity"), line.getBigDecimal("unit_price").toPlainString());
                    }
                }
            }
        }
    }
}
