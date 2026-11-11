package dev.javabybuilding.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class PracticeDbTest {

    private static String count(Connection db, String table) throws SQLException {
        return SqlRunner.query(db, "SELECT COUNT(*) AS n FROM " + table).cell(0, 0);
    }

    @Test
    void theSampleShopIsLoaded() throws SQLException {
        try (Connection db = PracticeDb.open()) {
            assertEquals("6", count(db, "product"));
            assertEquals("3", count(db, "customer"));
            assertEquals("5", count(db, "sale"));
            assertEquals("8", count(db, "sale_line"));
            assertEquals("13", count(db, "stock_movement"));
        }
    }

    @Test
    void anEmptyDatabaseHasNoTables() throws SQLException {
        try (Connection db = PracticeDb.openEmpty()) {
            assertEquals(0, Erd.tables(db).size());
        }
    }

    @Test
    void everyCallIsADatabaseOfItsOwn() throws SQLException {
        try (Connection first = PracticeDb.open(); Connection second = PracticeDb.open()) {
            SqlRunner.execute(first, "DELETE FROM stock_movement");
            assertEquals("0", count(first, "stock_movement"));
            assertEquals("13", count(second, "stock_movement"));
        }
    }

    @Test
    void theShelvesAgreeWithTheStockLog() throws SQLException {
        // The same health check as Inventory.audit() in Java: quantity on the shelf = RECEIVED minus SOLD.
        try (Connection db = PracticeDb.open()) {
            QueryResult problems = SqlRunner.query(db,
                    "SELECT p.name FROM product p "
                    + "LEFT JOIN (SELECT product_id, SUM(CASE type WHEN 'RECEIVED' THEN quantity ELSE -quantity END) AS net "
                    + "           FROM stock_movement GROUP BY product_id) m ON m.product_id = p.id "
                    + "WHERE p.quantity <> COALESCE(m.net, 0)");
            assertEquals(0, problems.rowCount());
        }
    }

    @Test
    void theSeedMatchesTheJavaSampleShop() throws SQLException {
        // The first four products are the shop from lessons 03-24: same names, same prices.
        dev.javabybuilding.inventory.Inventory java = dev.javabybuilding.inventory.Inventory.sample();
        try (Connection db = PracticeDb.open()) {
            QueryResult sql = SqlRunner.query(db, "SELECT name, price FROM product WHERE id <= 4 ORDER BY id");
            for (int i = 0; i < 4; i++) {
                assertEquals(java.products().get(i).getName(), sql.cell(i, 0));
                assertEquals(java.products().get(i).getPrice().toPlainString(), sql.cell(i, 1));
            }
        }
    }
}
