package dev.javabybuilding.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.javabybuilding.inventory.Product;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** The designed schema (db/schema.sql) must refuse bad data, each time naming the rule that was broken. */
class SchemaRulesTest {

    private Connection db;

    @BeforeEach
    void open() throws SQLException {
        db = PracticeDb.openWithShopSchema();
    }

    @AfterEach
    void close() throws SQLException {
        db.close();
    }

    private String count(String table) throws SQLException {
        return SqlRunner.query(db, "SELECT COUNT(*) FROM " + table).cell(0, 0);
    }

    /** Runs the SQL, expects the database to refuse it, and returns the error message. */
    private String refused(String sql) {
        return assertThrows(SQLException.class, () -> SqlRunner.execute(db, sql)).getMessage();
    }

    private static String newProduct(String name, String price, int reorder, int quantity) {
        return "INSERT INTO product (name, price, reorder_level, quantity) VALUES ('" + name + "', " + price + ", " + reorder + ", " + quantity + ")";
    }

    @Test
    void theSampleShopLoadsIntoTheDesignedSchema() throws SQLException {
        assertEquals("6", count("product"));
        assertEquals("8", count("sale_line"));
    }

    @Test
    void aProductNameMustBeUnique() {
        assertTrue(refused(newProduct("Rice 25kg", "1.00", 0, 0)).contains("UQ_PRODUCT_NAME"));
    }

    @Test
    void aProductNameMustNotBeBlank() {
        assertTrue(refused(newProduct("   ", "1.00", 0, 0)).contains("CK_PRODUCT_NAME"));
    }

    @Test
    void aProductNeedsAPriceAndAName() {
        refused("INSERT INTO product (name, reorder_level, quantity) VALUES ('Salt', 0, 0)");
        refused("INSERT INTO product (price) VALUES (5.00)");
    }

    @Test
    void thePriceRuleIsZeroPointZeroOneToTenThousand() throws SQLException {
        assertTrue(refused(newProduct("Free", "0.00", 0, 0)).contains("CK_PRODUCT_PRICE"));
        assertTrue(refused(newProduct("Gold", "10000.01", 0, 0)).contains("CK_PRODUCT_PRICE"));
        SqlRunner.execute(db, newProduct("Candy", "0.01", 0, 0));
        SqlRunner.execute(db, newProduct("Gold bar", "10000.00", 0, 0));
        assertEquals("8", count("product"));
    }

    @Test
    void theDatabaseAndJavaAgreeOnWhichPricesAreAllowed() throws SQLException {
        int n = 0;
        for (String price : List.of("0.00", "0.01", "0.99", "9999.99", "10000.00", "10000.01", "-5.00")) {
            boolean javaAllows = Product.isValidPrice(new BigDecimal(price));
            boolean databaseAllows;
            try {
                SqlRunner.execute(db, newProduct("Probe " + n++, price, 0, 0));
                databaseAllows = true;
            } catch (SQLException e) {
                databaseAllows = false;
            }
            assertEquals(javaAllows, databaseAllows, "price " + price);
        }
    }

    @Test
    void quantityAndReorderLevelCannotBeNegative() {
        assertTrue(refused(newProduct("Salt", "5.00", 0, -1)).contains("CK_PRODUCT_QUANTITY"));
        assertTrue(refused(newProduct("Pepper", "5.00", -1, 0)).contains("CK_PRODUCT_REORDER"));
    }

    @Test
    void reorderLevelAndQuantityDefaultToZero() throws SQLException {
        SqlRunner.execute(db, "INSERT INTO product (name, price) VALUES ('Salt', 5.00)");
        QueryResult row = SqlRunner.query(db, "SELECT reorder_level, quantity FROM product WHERE name = 'Salt'");
        assertEquals(List.of("0", "0"), row.rows().get(0));
    }

    @Test
    void aCustomerIsUniqueByNameAndPhone() throws SQLException {
        assertTrue(refused("INSERT INTO customer (name, phone) VALUES ('Ana Reyes', '09171234567')").contains("UQ_CUSTOMER_PERSON"));
        SqlRunner.execute(db, "INSERT INTO customer (name, phone) VALUES ('Ana Reyes', '09990000000')"); // same name, other person
        assertEquals("4", count("customer"));
    }

    @Test
    void paymentMustBeOneOfTheKnownMethods() {
        assertTrue(refused("INSERT INTO sale (sold_at, payment) VALUES (TIMESTAMP '2026-03-04 08:00:00', 'CHEQUE')").contains("CK_SALE_PAYMENT"));
    }

    @Test
    void aSaleLineNeedsAtLeastOneUnit() {
        assertTrue(refused("INSERT INTO sale_line (sale_id, product_id, unit_price, quantity) VALUES (1, 1, 10.00, 0)").contains("CK_SALE_LINE_QUANTITY"));
    }

    @Test
    void aStockMovementIsReceivedOrSold() {
        assertTrue(refused("INSERT INTO stock_movement (product_id, type, quantity, moved_at) VALUES (1, 'LOST', 1, TIMESTAMP '2026-03-04 08:00:00')")
                .contains("CK_MOVEMENT_TYPE"));
    }

    @Test
    void aSaleLineCannotPointAtAProductThatDoesNotExist() {
        assertTrue(refused("INSERT INTO sale_line (sale_id, product_id, unit_price, quantity) VALUES (1, 99, 10.00, 1)").contains("FK_SALE_LINE_PRODUCT"));
    }

    @Test
    void aProductWithSalesCannotBeDeleted() throws SQLException {
        assertTrue(refused("DELETE FROM product WHERE name = 'Rice 25kg'").contains("FK_SALE_LINE_PRODUCT"));
        assertEquals("6", count("product"));
    }

    @Test
    void aProductNobodyBoughtCanBeDeleted() throws SQLException {
        SqlRunner.execute(db, "DELETE FROM product WHERE name = 'Soy Sauce 500ml'");
        assertEquals("5", count("product"));
    }

    @Test
    void deletingASaleDeletesItsLines() throws SQLException {
        SqlRunner.execute(db, "DELETE FROM sale WHERE id = 3");
        assertEquals("4", count("sale"));
        assertEquals("6", count("sale_line")); // sale 3 had two lines
    }

    @Test
    void deletingACustomerKeepsTheirSalesAsWalkIns() throws SQLException {
        SqlRunner.execute(db, "DELETE FROM customer WHERE name = 'Ana Reyes'");
        assertEquals("5", count("sale"));
        assertEquals("4", SqlRunner.query(db, "SELECT COUNT(*) FROM sale WHERE customer_id IS NULL").cell(0, 0));
    }

    @Test
    void theFourIndexesExist() throws SQLException {
        QueryResult indexes = SqlRunner.query(db, "SELECT DISTINCT LOWER(index_name) AS index_name FROM information_schema.indexes "
                + "WHERE LOWER(index_name) LIKE 'idx\\_%' ESCAPE '\\' ORDER BY 1");
        assertEquals(List.of("idx_movement_product", "idx_sale_line_product", "idx_sale_line_sale", "idx_sale_sold_at"), indexes.column("index_name"));
    }

    @Test
    void theDesignedSchemaHasTheSameShapeAsThePracticeSchema() throws SQLException {
        try (Connection practice = PracticeDb.open()) {
            assertEquals(Erd.mermaid(practice), Erd.mermaid(db));
        }
    }
}
