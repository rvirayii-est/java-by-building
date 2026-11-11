package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class JdbcSalesRepositoryTest extends SalesRepositoryContract {

    private Database database;

    @Override
    Repos create() {
        database = TestDb.fresh();
        return new Repos(new JdbcProductRepository(database), new JdbcSaleRepository(database), new JdbcStockMovementRepository(database));
    }

    @AfterEach
    void close() {
        database.close();
    }

    private static Sale draftFor(Customer customer, LocalDateTime at, int riceQuantity) {
        Sale sale = new Sale(0, customer, at);
        sale.addLine(new SaleLine("Rice 25kg", Money.of("1250.50"), riceQuantity));
        return sale;
    }

    @Test
    void theSameCustomerIsOneObjectInAllTheirSalesAndTheirPointsAreWorkedOutAgain() {
        LocalDateTime day = LocalDateTime.of(2026, 3, 1, 9, 30, 0);
        repos.sales().add(draftFor(new Customer("Ana Reyes", "09171234567"), day, 2));          // 2501.00 -> 25 points
        repos.sales().add(draftFor(new Customer("Ana Reyes", "09171234567"), day.plusDays(1), 1)); // 1250.50 -> 12 points
        java.util.List<Sale> sales = repos.sales().findAll();
        org.junit.jupiter.api.Assertions.assertSame(sales.get(0).getCustomer(), sales.get(1).getCustomer());
        assertEquals(37, sales.get(0).getCustomer().getPoints());
    }

    @Test
    void aCustomerIsStoredOnceHoweverManyTimesTheyBuy() throws java.sql.SQLException {
        LocalDateTime day = LocalDateTime.of(2026, 3, 1, 9, 30, 0);
        repos.sales().add(draftFor(new Customer("Ana Reyes", "0917"), day, 1));
        repos.sales().add(draftFor(new Customer("Ana Reyes", "0917"), day, 1));
        try (var rows = database.connection().createStatement().executeQuery("SELECT COUNT(*) FROM customer")) {
            rows.next();
            assertEquals(1, rows.getInt(1));
        }
    }

    // What only a database can do: refuse nonsense (the foreign keys of lesson 28).

    @Test
    void aMovementForAnUnknownProductIsRefusedByTheDatabase() {
        assertThrows(DatabaseException.class, () -> repos.movements().add(
                new StockMovement("Ghost", MovementType.SOLD, 1, LocalDateTime.of(2026, 3, 1, 9, 0))));
        assertEquals(0, repos.movements().findAll().size());
    }

    @Test
    void aSaleLineForAnUnknownProductIsRefusedByTheDatabase() {
        Sale sale = new Sale(0, Customer.WALK_IN, LocalDateTime.of(2026, 3, 1, 9, 0));
        sale.addLine(new SaleLine("Ghost", Money.of("1.00"), 1));
        assertThrows(DatabaseException.class, () -> repos.sales().add(sale));
    }
}
