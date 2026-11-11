import java.lang.reflect.RecordComponent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * What an ORM (object-relational mapper) such as Hibernate does, in about 50 lines. JDBC by hand writes SQL and copies column
 * by column into objects. An ORM does the same copying for you, by READING THE CLASS (reflection): the class name gives the
 * table, the fields give the columns. The price is that you no longer see the SQL, which is why this example prints it.
 *
 * Needs the H2 jar on the class path: bash scripts/run-examples.sh does that. Run:  java -cp h2.jar MiniOrm.java
 */
public class MiniOrm {

    /** The thing to store. A record: its components are the columns. */
    record Product(String name, BigDecimal price, int reorderLevel) {}

    // ---------- the "ORM" ----------

    /** reorderLevel becomes reorder_level; Product becomes product. */
    static String snake(String camel) {
        return camel.replaceAll("([A-Z])", "_$1").toLowerCase().replaceFirst("^_", "");
    }

    static void save(Connection connection, Object row) throws Exception {
        RecordComponent[] parts = row.getClass().getRecordComponents();
        String columns = String.join(", ", java.util.Arrays.stream(parts).map(p -> snake(p.getName())).toList());
        String marks = String.join(", ", java.util.Collections.nCopies(parts.length, "?"));
        String sql = "INSERT INTO " + snake(row.getClass().getSimpleName()) + " (" + columns + ") VALUES (" + marks + ")";
        System.out.println("  ORM runs: " + sql);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parts.length; i++) {
                statement.setObject(i + 1, parts[i].getAccessor().invoke(row)); // read the value out of the record
            }
            statement.executeUpdate();
        }
    }

    static <T extends Record> List<T> findAll(Connection connection, Class<T> type) throws Exception {
        RecordComponent[] parts = type.getRecordComponents();
        String sql = "SELECT " + java.util.Arrays.stream(parts).map(p -> snake(p.getName())).collect(Collectors.joining(", "))
                + " FROM " + snake(type.getSimpleName()) + " ORDER BY name";
        System.out.println("  ORM runs: " + sql);
        Class<?>[] types = java.util.Arrays.stream(parts).map(RecordComponent::getType).toArray(Class<?>[]::new);
        List<T> result = new ArrayList<>();
        try (Statement statement = connection.createStatement(); ResultSet rows = statement.executeQuery(sql)) {
            while (rows.next()) {
                Object[] values = new Object[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    Class<?> wanted = types[i] == int.class ? Integer.class : types[i]; // getObject wants a class, not a primitive
                    values[i] = rows.getObject(i + 1, wanted);       // JDBC converts to the field type
                }
                result.add(type.getDeclaredConstructor(types).newInstance(values));
            }
        }
        return result;
    }

    // ---------- the same job by hand with JDBC, for comparison ----------

    static List<Product> findAllByHand(Connection connection) throws Exception {
        List<Product> products = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet rows = statement.executeQuery("SELECT name, price, reorder_level FROM product ORDER BY name")) {
            while (rows.next()) {
                products.add(new Product(rows.getString("name"), rows.getBigDecimal("price"), rows.getInt("reorder_level")));
            }
        }
        return products;
    }

    public static void main(String[] args) throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:orm-demo")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE product (name VARCHAR(60), price DECIMAL(10,2), reorder_level INT)");
            }

            System.out.println("Saving two objects:");
            save(connection, new Product("Sugar 1kg", new BigDecimal("75.00"), 10));
            save(connection, new Product("Rice 25kg", new BigDecimal("1250.50"), 10));

            System.out.println("Reading them back with the ORM:");
            List<Product> viaOrm = findAll(connection, Product.class);
            viaOrm.forEach(p -> System.out.println("  " + p));

            System.out.println("Reading them back by hand with JDBC:");
            List<Product> byHand = findAllByHand(connection);
            byHand.forEach(p -> System.out.println("  " + p));

            System.out.println("Same result? " + viaOrm.equals(byHand));
        }
    }
}
