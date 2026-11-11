package dev.javabybuilding.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShopReportsTest {

    private Connection db;

    @BeforeEach
    void open() throws SQLException {
        db = PracticeDb.open();
    }

    @AfterEach
    void close() throws SQLException {
        db.close();
    }

    private QueryResult run(String sql) throws SQLException {
        return SqlRunner.query(db, sql);
    }

    @Test
    void aReceiptListsTheLinesOfOneSale() throws SQLException {
        QueryResult receipt = run(ShopReports.receipt(3));
        assertEquals(List.of("product", "quantity", "unit_price", "line_total"), receipt.columns());
        assertEquals(List.of("Cooking Oil 1L", "Sugar 1kg"), receipt.column("product"));
        assertEquals(List.of("294.00", "300.00"), receipt.column("line_total"));
    }

    @Test
    void aReceiptForASaleThatDoesNotExistIsEmpty() throws SQLException {
        assertEquals(0, run(ShopReports.receipt(99)).rowCount());
    }

    @Test
    void theSalesListNamesTheCustomerOrWalkIn() throws SQLException {
        QueryResult sales = run(ShopReports.salesList());
        assertEquals(List.of("Ana Reyes", "Walk-in", "Ben Santos", "Ana Reyes", "Walk-in"), sales.column("customer"));
        assertEquals(List.of("2501.00", "284.00", "594.00", "1346.50", "350.00"), sales.column("subtotal"));
    }

    @Test
    void everySaleAppearsOnceInTheSalesList() throws SQLException {
        assertEquals(List.of("1", "2", "3", "4", "5"), run(ShopReports.salesList()).column("id"));
    }

    @Test
    void revenueByProductAddsUpUnitsAndMoney() throws SQLException {
        QueryResult revenue = run(ShopReports.revenueByProduct());
        assertEquals(List.of("Rice 25kg", "Instant Noodles", "Sugar 1kg", "Cooking Oil 1L", "Sardines 155g"), revenue.column("product"));
        assertEquals(List.of("3", "35", "4", "3", "10"), revenue.column("units"));
        assertEquals("3751.50", revenue.cell(0, 2));
    }

    @Test
    void revenueByProductAddsUpToTheSameTotalAsTheSalesList() throws SQLException {
        assertEquals(run("SELECT SUM(subtotal) FROM (" + ShopReports.salesList() + ")").cell(0, 0),
                run("SELECT SUM(revenue) FROM (" + ShopReports.revenueByProduct() + ")").cell(0, 0));
    }

    @Test
    void bestSellersAreOrderedByUnitsSold() throws SQLException {
        assertEquals(List.of("Instant Noodles", "Sardines 155g", "Sugar 1kg"), run(ShopReports.bestSellers(3)).column("product"));
        assertEquals(1, run(ShopReports.bestSellers(1)).rowCount());
    }

    @Test
    void neverSoldFindsProductsWithNoSaleLine() throws SQLException {
        assertEquals(List.of("Soy Sauce 500ml"), run(ShopReports.neverSold()).column("product"));
    }

    @Test
    void customersWithoutPurchasesFindsCarla() throws SQLException {
        assertEquals(List.of("Carla Dizon"), run(ShopReports.customersWithoutPurchases()).column("customer"));
    }

    @Test
    void dailySalesGroupsByCalendarDay() throws SQLException {
        QueryResult days = run(ShopReports.dailySales());
        assertEquals(List.of("2026-03-01", "2026-03-02", "2026-03-03"), days.column("sale_day"));
        assertEquals(List.of("2", "2", "1"), days.column("sales"));
        assertEquals(List.of("2785.00", "1940.50", "350.00"), days.column("revenue"));
    }

    @Test
    void theStockAuditIsEmptyBecauseTheShelvesAgreeWithTheLog() throws SQLException {
        assertEquals(0, run(ShopReports.stockAudit()).rowCount());
    }

    @Test
    void theStockAuditCatchesAShelfThatWasChangedByHand() throws SQLException {
        SqlRunner.execute(db, "UPDATE product SET quantity = quantity + 3 WHERE name = 'Sugar 1kg'");
        QueryResult problems = run(ShopReports.stockAudit());
        assertEquals(List.of("Sugar 1kg"), problems.column("product"));
        assertEquals(List.of("7"), problems.column("shelf"));
        assertEquals(List.of("4"), problems.column("logged"));
        assertTrue(problems.toTable().contains("(1 row)"));
    }
}
