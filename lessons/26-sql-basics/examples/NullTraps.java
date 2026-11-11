// Run with:  java -cp <path to the H2 jar> lessons/26-sql-basics/examples/NullTraps.java   (from the repository root)
// NULL is the number-one surprise for people new to SQL. Each line below asks a question and prints the answer.
// Rule of thumb: NULL is "unknown", and any comparison with an unknown is neither true nor false: it is unknown.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class NullTraps {

    public static void main(String[] args) throws SQLException {
        try (Connection db = DriverManager.getConnection("jdbc:h2:mem:null-traps")) {
            update(db, "CREATE TABLE sale (id INT PRIMARY KEY, customer_id INT, total DECIMAL(10,2))");
            update(db, "INSERT INTO sale VALUES (1, 1, 2501.00), (2, NULL, 444.00), (3, 2, 300.00), (4, NULL, 350.00)");
            System.out.println("Table sale: ids 1-4; sales 2 and 4 have customer_id NULL (walk-ins).\n");

            ask(db, "How many sales have customer_id = NULL?  (the trap)", "SELECT COUNT(*) FROM sale WHERE customer_id = NULL");
            ask(db, "How many sales have customer_id IS NULL?  (the right way)", "SELECT COUNT(*) FROM sale WHERE customer_id IS NULL");
            ask(db, "COUNT(*) counts rows", "SELECT COUNT(*) FROM sale");
            ask(db, "COUNT(customer_id) counts only the rows that HAVE a value", "SELECT COUNT(customer_id) FROM sale");
            ask(db, "customer_id <> 1 skips the NULL rows too (unknown is not 'different')", "SELECT COUNT(*) FROM sale WHERE customer_id <> 1");
            ask(db, "NULL + 1 is NULL", "SELECT customer_id + 1 FROM sale WHERE id = 2");
            ask(db, "COALESCE(x, y): x, or y when x is NULL", "SELECT COALESCE(customer_id, 0) FROM sale WHERE id = 2");
            ask(db, "SUM ignores NULLs; the SUM of nothing is NULL", "SELECT SUM(total), SUM(total) FILTER (WHERE id > 99) FROM sale");
        }
    }

    static void update(Connection db, String sql) throws SQLException {
        try (Statement statement = db.createStatement()) {
            statement.execute(sql);
        }
    }

    static void ask(Connection db, String question, String sql) throws SQLException {
        System.out.println(question);
        System.out.println("  " + sql);
        try (Statement statement = db.createStatement(); ResultSet rows = statement.executeQuery(sql)) {
            rows.next();
            StringBuilder answer = new StringBuilder("  -> ");
            for (int c = 1; c <= rows.getMetaData().getColumnCount(); c++) {
                String value = rows.getString(c);
                answer.append(c > 1 ? ", " : "").append(value == null ? "NULL" : value);
            }
            System.out.println(answer + "\n");
        }
    }
}
