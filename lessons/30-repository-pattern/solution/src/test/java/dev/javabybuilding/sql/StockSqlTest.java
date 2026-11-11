package dev.javabybuilding.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.javabybuilding.inventory.Product;
import dev.javabybuilding.inventory.StockChecker;
import dev.javabybuilding.inventory.StockReport;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StockSqlTest {

    private Connection db;

    @BeforeEach
    void open() throws SQLException {
        db = PracticeDb.open();
    }

    @AfterEach
    void close() throws SQLException {
        db.close();
    }

    /** The same rows as Java objects, so SQL's answers can be compared with Java's. */
    private List<Product> productsInJava() throws SQLException {
        QueryResult rows = SqlRunner.query(db, "SELECT name, price, reorder_level, quantity FROM product ORDER BY id");
        List<Product> products = new ArrayList<>();
        for (int r = 0; r < rows.rowCount(); r++) {
            products.add(new Product(rows.cell(r, 0), new BigDecimal(rows.cell(r, 1)),
                    Integer.parseInt(rows.cell(r, 2)), Integer.parseInt(rows.cell(r, 3))));
        }
        return products;
    }

    @Test
    void allProductsAreListedAToZ() throws SQLException {
        QueryResult result = SqlRunner.query(db, StockSql.allProducts());
        assertEquals(List.of("name", "price", "quantity"), result.columns());
        assertEquals(List.of("Cooking Oil 1L", "Instant Noodles", "Rice 25kg", "Sardines 155g", "Soy Sauce 500ml", "Sugar 1kg"),
                result.column("name"));
    }

    @Test
    void lowOrOutListsTheEmptiestFirst() throws SQLException {
        QueryResult result = SqlRunner.query(db, StockSql.lowOrOut());
        assertEquals(List.of("Soy Sauce 500ml", "Sugar 1kg", "Instant Noodles"), result.column("name"));
        assertEquals(List.of("0", "4", "5"), result.column("quantity"));
    }

    @Test
    void lowOrOutAgreesWithJavasNeedsRestock() throws SQLException {
        List<String> inJava = new ArrayList<>();
        for (Product p : dev.javabybuilding.inventory.SalesReport.needsRestock(productsInJava())) {
            inJava.add(p.getName());
        }
        inJava.sort(null);
        List<String> inSql = new ArrayList<>(SqlRunner.query(db, StockSql.lowOrOut()).column("name"));
        inSql.sort(null);
        assertEquals(inJava, inSql);
    }

    @Test
    void theStatusColumnAgreesWithStockCheckerForEveryProduct() throws SQLException {
        QueryResult result = SqlRunner.query(db, StockSql.statusReport());
        List<Product> products = productsInJava();
        assertEquals(products.size(), result.rowCount());
        for (int r = 0; r < products.size(); r++) {
            assertEquals(StockChecker.status(products.get(r).getQuantity(), products.get(r).getReorderLevel()), result.cell(r, 2),
                    products.get(r).getName());
        }
    }

    @Test
    void theTotalValueAgreesWithJava() throws SQLException {
        QueryResult result = SqlRunner.query(db, StockSql.totalStockValue());
        assertEquals(List.of("total_value"), result.columns());
        assertEquals(StockReport.totalValue(productsInJava()), new BigDecimal(result.cell(0, 0)));
        assertEquals("23524.50", result.cell(0, 0));
    }

    @Test
    void theSqlReportLooksExactlyLikeTheJavaReport() throws SQLException {
        assertEquals(StockReport.build(productsInJava()), StockSql.report(db));
    }
}
