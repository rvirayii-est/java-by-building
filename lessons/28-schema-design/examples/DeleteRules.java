// Run with:  java -cp <path to the H2 jar> lessons/28-schema-design/examples/DeleteRules.java   (from the repository root)
// What should happen to rows that point at a row you delete? You decide, per foreign key, with ON DELETE:
//   RESTRICT (the default)  refuse the delete          SET NULL  keep the child, clear its link          CASCADE  delete the children too
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DeleteRules {

    public static void main(String[] args) throws Exception {
        Path sql = Path.of("lessons/28-schema-design/solution/src/main/resources");
        try (Connection db = DriverManager.getConnection("jdbc:h2:mem:delete-rules")) {
            for (String file : new String[] {"db/schema.sql", "sql/seed.sql"}) {
                for (String statement : Files.readString(sql.resolve(file)).replaceAll("(?m)^--.*$", "").split(";")) {
                    if (!statement.isBlank()) {
                        run(db, statement);
                    }
                }
            }
            System.out.println("Start:  " + counts(db));

            System.out.println("\n1. RESTRICT: delete product 'Rice 25kg' (sales still use it)");
            try {
                run(db, "DELETE FROM product WHERE name = 'Rice 25kg'");
                System.out.println("   deleted");
            } catch (SQLException e) {
                System.out.println("   refused (SQL state " + e.getSQLState() + "): history must not point at nothing");
            }
            System.out.println("   Now:  " + counts(db));

            System.out.println("\n2. SET NULL: delete customer 'Ana Reyes' (she made sales 1 and 4)");
            run(db, "DELETE FROM customer WHERE name = 'Ana Reyes'");
            System.out.println("   Now:  " + counts(db));
            System.out.println("   Sales without a customer: " + one(db, "SELECT COUNT(*) FROM sale WHERE customer_id IS NULL") + " (was 2): her sales became walk-ins");

            System.out.println("\n3. CASCADE: delete sale 3 (it has two lines)");
            run(db, "DELETE FROM sale WHERE id = 3");
            System.out.println("   Now:  " + counts(db));
        }
    }

    static String counts(Connection db) throws SQLException {
        return one(db, "SELECT COUNT(*) FROM product") + " products, " + one(db, "SELECT COUNT(*) FROM customer") + " customers, "
                + one(db, "SELECT COUNT(*) FROM sale") + " sales, " + one(db, "SELECT COUNT(*) FROM sale_line") + " sale lines";
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
