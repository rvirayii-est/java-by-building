// Run with:  java -cp <path to the H2 jar> lessons/28-schema-design/examples/SchemaRules.java   (from the repository root)
// Builds the shop's designed schema (db/schema.sql) and then tries to break it. Every bad row is refused, and the error
// names the rule. The database is the last line of defence: it protects the data from bugs in ANY program, not only ours.
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SchemaRules {

    public static void main(String[] args) throws Exception {
        Path schema = Path.of("lessons/28-schema-design/solution/src/main/resources/db/schema.sql");
        try (Connection db = DriverManager.getConnection("jdbc:h2:mem:schema-rules")) {
            for (String statement : Files.readString(schema).replaceAll("(?m)^--.*$", "").split(";")) {
                if (!statement.isBlank()) {
                    run(db, statement);
                }
            }
            run(db, "INSERT INTO product (name, price, reorder_level, quantity) VALUES ('Rice 25kg', 1250.50, 10, 17)");
            run(db, "INSERT INTO customer (name, phone) VALUES ('Ana Reyes', '09171234567')");
            run(db, "INSERT INTO sale (customer_id, sold_at, payment) VALUES (1, TIMESTAMP '2026-03-01 09:30:00', 'CASH')");
            System.out.println("Schema built. One product, one customer, one sale. Now let us misbehave.\n");

            try_(db, "A second product with the same name", "INSERT INTO product (name, price) VALUES ('Rice 25kg', 10.00)");
            try_(db, "A product with a blank name", "INSERT INTO product (name, price) VALUES ('  ', 10.00)");
            try_(db, "A price of 0", "INSERT INTO product (name, price) VALUES ('Free', 0.00)");
            try_(db, "A price above PHP 10,000", "INSERT INTO product (name, price) VALUES ('Gold', 10000.01)");
            try_(db, "A negative quantity", "INSERT INTO product (name, price, quantity) VALUES ('Salt', 5.00, -1)");
            try_(db, "A product without a price", "INSERT INTO product (name) VALUES ('Pepper')");
            try_(db, "The same customer twice", "INSERT INTO customer (name, phone) VALUES ('Ana Reyes', '09171234567')");
            try_(db, "Payment by cheque", "INSERT INTO sale (sold_at, payment) VALUES (TIMESTAMP '2026-03-01 10:00:00', 'CHEQUE')");
            try_(db, "A sale line for 0 units", "INSERT INTO sale_line (sale_id, product_id, unit_price, quantity) VALUES (1, 1, 10.00, 0)");
            try_(db, "A sale line for product 99 (does not exist)", "INSERT INTO sale_line (sale_id, product_id, unit_price, quantity) VALUES (1, 99, 10.00, 1)");
            try_(db, "A stock movement of type LOST", "INSERT INTO stock_movement (product_id, type, quantity, moved_at) VALUES (1, 'LOST', 1, TIMESTAMP '2026-03-01 10:00:00')");

            System.out.println("\nAnd the rows that follow the rules go in without a fuss:");
            try_(db, "A product at the lowest legal price", "INSERT INTO product (name, price) VALUES ('Candy', 0.01)");
            try_(db, "A sale line for 3 units", "INSERT INTO sale_line (sale_id, product_id, unit_price, quantity) VALUES (1, 1, 1250.50, 3)");
        }
    }

    static void run(Connection db, String sql) throws SQLException {
        try (Statement statement = db.createStatement()) {
            statement.execute(sql);
        }
    }

    static void try_(Connection db, String what, String sql) {
        try {
            run(db, sql);
            System.out.println("ACCEPTED  " + what);
        } catch (SQLException e) {
            Matcher rule = Pattern.compile("\\b(UQ|CK|FK)_[A-Z_]+").matcher(e.getMessage());
            String name = rule.find() ? rule.group().replaceAll("_INDEX_\\d+$", "") : "a NOT NULL column";
            System.out.printf("REFUSED   %-45s rule: %-22s (SQL state %s)%n", what, name, e.getSQLState());
        }
    }
}
