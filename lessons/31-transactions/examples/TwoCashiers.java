// Run with:  java -cp <path to the H2 jar> lessons/31-transactions/examples/TwoCashiers.java   (from the repository root)
// Two cashiers (two connections) sell the LAST 3 sacks of rice at the same moment. Isolation keeps their private work
// apart until commit; a guarded UPDATE keeps the shop from selling what it does not have.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TwoCashiers {

    static final String URL = "jdbc:h2:mem:two-cashiers";

    public static void main(String[] args) throws SQLException {
        try (Connection setup = DriverManager.getConnection(URL);
             Connection ana = DriverManager.getConnection(URL);
             Connection ben = DriverManager.getConnection(URL)) {
            run(setup, "CREATE TABLE product (name VARCHAR(40) PRIMARY KEY, quantity INT NOT NULL CHECK (quantity >= 0))");
            run(setup, "INSERT INTO product VALUES ('Rice 25kg', 3)");

            System.out.println("== 1. Isolation: Ana's unfinished sale is invisible to Ben");
            ana.setAutoCommit(false);
            run(ana, "UPDATE product SET quantity = quantity - 3 WHERE name = 'Rice 25kg'");
            System.out.println("Ana (inside her transaction) sees: " + one(ana, "SELECT quantity FROM product"));
            System.out.println("Ben (a different connection) sees: " + one(ben, "SELECT quantity FROM product") + "   <- the committed value");
            ana.commit();
            System.out.println("After Ana's commit, Ben sees:      " + one(ben, "SELECT quantity FROM product"));
            ana.setAutoCommit(true);

            run(setup, "UPDATE product SET quantity = 3 WHERE name = 'Rice 25kg'");

            System.out.println("\n== 2. The LOST UPDATE: both read 3, both think they may sell 3");
            int anaSaw = Integer.parseInt(one(ana, "SELECT quantity FROM product"));
            int benSaw = Integer.parseInt(one(ben, "SELECT quantity FROM product"));
            System.out.println("Ana read " + anaSaw + ", Ben read " + benSaw);
            // each writes 'what I read minus what I sell' - the second write silently overwrites the first
            run(ana, "UPDATE product SET quantity = " + (anaSaw - 3) + " WHERE name = 'Rice 25kg'");
            run(ben, "UPDATE product SET quantity = " + (benSaw - 3) + " WHERE name = 'Rice 25kg'");
            System.out.println("Both sold 3 sacks (6 sold), the shelf says " + one(setup, "SELECT quantity FROM product") + ". We sold rice we never had.");

            run(setup, "UPDATE product SET quantity = 3 WHERE name = 'Rice 25kg'");

            System.out.println("\n== 3. The cure: let the database check AND change in one statement (UPDATE ... WHERE quantity >= ?)");
            System.out.println("Ana sells 3: " + (guardedSell(ana, 3) ? "done" : "refused, not enough stock"));
            System.out.println("Ben sells 3: " + (guardedSell(ben, 3) ? "done" : "refused, not enough stock"));
            System.out.println("Shelf: " + one(setup, "SELECT quantity FROM product"));
        }
    }

    /** One statement does the check and the change together, so nobody can slip in between. Returns false when the stock was too low. */
    static boolean guardedSell(Connection db, int amount) throws SQLException {
        try (PreparedStatement statement = db.prepareStatement("UPDATE product SET quantity = quantity - ? WHERE name = 'Rice 25kg' AND quantity >= ?")) {
            statement.setInt(1, amount);
            statement.setInt(2, amount);
            return statement.executeUpdate() == 1; // 0 rows changed: the guard said no
        }
    }

    static String one(Connection db, String sql) throws SQLException {
        try (Statement statement = db.createStatement(); ResultSet rows = statement.executeQuery(sql)) {
            rows.next();
            return rows.getString(1);
        }
    }

    static void run(Connection db, String sql) throws SQLException {
        try (Statement statement = db.createStatement()) {
            statement.execute(sql);
        }
    }
}
