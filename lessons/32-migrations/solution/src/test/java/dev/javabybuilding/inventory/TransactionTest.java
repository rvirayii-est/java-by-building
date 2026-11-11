package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Clock;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** A sale is all-or-nothing. The tests break the sale on purpose, half way, and look at what is left in the database. */
class TransactionTest {


    private String name;
    private Database database;
    private ProductRepository products;
    private JdbcSaleRepository realSales;
    private JdbcStockMovementRepository realMovements;

    /** Counts what is really in a table, asking over a SECOND connection: it sees only what has been committed. */
    private int rowsCommitted(String table) throws SQLException {
        try (Connection other = DriverManager.getConnection("jdbc:h2:mem:" + name);
             Statement statement = other.createStatement();
             ResultSet rows = statement.executeQuery("SELECT COUNT(*) FROM " + table)) {
            rows.next();
            return rows.getInt(1);
        }
    }

    @BeforeEach
    void open() {
        name = "tx-" + UUID.randomUUID();
        database = Database.openMemory(name);
        Migrator.standard(database).migrate();
        products = new JdbcProductRepository(database);
        realSales = new JdbcSaleRepository(database);
        realMovements = new JdbcStockMovementRepository(database);
        products.add(new Product("Rice 25kg", Money.of("1250.50"), 10, 12));
    }

    @AfterEach
    void close() {
        database.close();
    }

    /** A stock-log repository that breaks on demand: it pretends the disk filled up just before the last step. */
    private static final class BrokenMovements implements StockMovementRepository {
        @Override
        public void add(StockMovement movement) {
            throw new IllegalStateException("disk full");
        }

        @Override
        public List<StockMovement> findAll() {
            return List.of();
        }
    }

    private ShopService serviceWithBrokenLog(Transactor transactor) {
        return new ShopService(products, realSales, new BrokenMovements(), Clock.system(Inventory.SHOP_ZONE), transactor);
    }

    // ---- Database.inTransaction itself

    @Test
    void whenTheWorkFinishesEverythingIsCommitted() throws SQLException {
        database.runInTransaction(() -> products.add(new Product("Sugar 1kg", Money.of("75.00"), 10)));
        assertEquals(2, rowsCommitted("product")); // visible to another connection: really saved
    }

    @Test
    void whenTheWorkThrowsEverythingIsRolledBackAndTheSameExceptionArrives() throws SQLException {
        IllegalStateException boom = new IllegalStateException("boom");
        IllegalStateException caught = assertThrows(IllegalStateException.class, () -> database.runInTransaction(() -> {
            products.add(new Product("Sugar 1kg", Money.of("75.00"), 10));
            products.add(new Product("Cooking Oil 1L", Money.of("98.00"), 10));
            throw boom;
        }));
        assertSame(boom, caught);
        assertEquals(1, rowsCommitted("product")); // only Rice, from setup
        assertEquals(1, products.findAll().size());
    }

    @Test
    void anInnerTransactionJoinsTheOuterOne() throws SQLException {
        assertThrows(IllegalStateException.class, () -> database.runInTransaction(() -> {
            database.runInTransaction(() -> products.add(new Product("Sugar 1kg", Money.of("75.00"), 10))); // "finished" inner
            throw new IllegalStateException("the outer work fails afterwards");
        }));
        assertEquals(1, rowsCommitted("product")); // the inner work was undone too
    }

    @Test
    void changesInsideATransactionAreInvisibleToOthersUntilTheCommit() throws SQLException {
        database.runInTransaction(() -> {
            products.add(new Product("Sugar 1kg", Money.of("75.00"), 10));
            try {
                assertEquals(1, rowsCommitted("product")); // another connection still sees the old state
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
            assertEquals(2, products.findAll().size());    // this connection sees its own change
        });
        assertEquals(2, rowsCommitted("product"));
    }

    @Test
    void afterAFailedTransactionTheDatabaseWorksNormallyAgain() throws SQLException {
        assertThrows(IllegalStateException.class, () -> database.runInTransaction(() -> {
            throw new IllegalStateException("boom");
        }));
        assertTrue(database.connection().getAutoCommit());
        products.add(new Product("Sugar 1kg", Money.of("75.00"), 10)); // outside a transaction: saved at once
        assertEquals(2, rowsCommitted("product"));
    }

    @Test
    void theResultOfTheWorkIsReturned() {
        assertEquals(42, database.inTransaction(() -> 42));
    }

    @Test
    void theNoneTransactorJustRunsTheWork() {
        assertEquals("done", Transactor.NONE.inTransaction(() -> "done"));
    }

    // ---- ShopService: a sale is all-or-nothing

    @Test
    void withoutATransactionAFailedSaleLeavesTheShopHalfChanged() throws SQLException {
        ShopService careless = serviceWithBrokenLog(Transactor.NONE);
        assertThrows(IllegalStateException.class, () -> careless.sell("Rice 25kg", 5, Customer.WALK_IN, new NoDiscount(), PaymentMethod.CASH));
        // THE BUG: step 1 and step 2 were saved, step 3 failed. 5 sacks are gone and a receipt exists, but the log says nothing.
        assertEquals(7, products.findByName("Rice 25kg").orElseThrow().getQuantity());
        assertEquals(1, rowsCommitted("sale"));
        assertEquals(0, rowsCommitted("stock_movement"));
    }

    @Test
    void withATransactionAFailedSaleChangesNothing() throws SQLException {
        ShopService careful = serviceWithBrokenLog(database);
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> careful.sell("Rice 25kg", 5, Customer.WALK_IN, new NoDiscount(), PaymentMethod.CASH));
        assertEquals("disk full", e.getMessage());
        assertEquals(12, products.findByName("Rice 25kg").orElseThrow().getQuantity());
        assertEquals(0, rowsCommitted("sale"));
        assertEquals(0, rowsCommitted("sale_line"));
        assertEquals(0, rowsCommitted("stock_movement"));
    }

    @Test
    void aFailedSaleDoesNotGiveTheCustomerPoints() {
        Customer ana = new Customer("Ana Reyes", "0917");
        ShopService careful = serviceWithBrokenLog(database);
        assertThrows(IllegalStateException.class, () -> careful.sell("Rice 25kg", 5, ana, new NoDiscount(), PaymentMethod.CASH));
        assertEquals(0, ana.getPoints());
    }

    @Test
    void aFailedSaleDoesNotLeaveANewCustomerBehind() throws SQLException {
        ShopService careful = serviceWithBrokenLog(database);
        assertThrows(IllegalStateException.class,
                () -> careful.sell("Rice 25kg", 5, new Customer("Ana Reyes", "0917"), new NoDiscount(), PaymentMethod.CASH));
        assertEquals(0, rowsCommitted("customer"));
    }

    @Test
    void aSaleThatSucceedsIsCommittedEverywhere() throws SQLException {
        ShopService shop = new ShopService(products, realSales, realMovements, Clock.system(Inventory.SHOP_ZONE), database);
        shop.sell("Rice 25kg", 5, new Customer("Ana Reyes", "0917"), new NoDiscount(), PaymentMethod.CASH);
        assertEquals(7, products.findByName("Rice 25kg").orElseThrow().getQuantity());
        assertEquals(1, rowsCommitted("sale"));
        assertEquals(1, rowsCommitted("sale_line"));
        assertEquals(1, rowsCommitted("customer"));
        assertEquals(1, rowsCommitted("stock_movement"));
    }

    @Test
    void aRefusedSaleForTooLittleStockLeavesNoTrace() throws SQLException {
        ShopService shop = new ShopService(products, realSales, realMovements, Clock.system(Inventory.SHOP_ZONE), database);
        assertThrows(InsufficientStockException.class, () -> shop.sell("Rice 25kg", 13, Customer.WALK_IN, new NoDiscount(), PaymentMethod.CASH));
        assertEquals(12, products.findByName("Rice 25kg").orElseThrow().getQuantity());
        assertEquals(0, rowsCommitted("sale"));
    }

    @Test
    void aFailedDeliveryDoesNotChangeTheShelf() {
        ShopService careful = serviceWithBrokenLog(database);
        assertThrows(IllegalStateException.class, () -> careful.receive("Rice 25kg", 20));
        assertEquals(12, products.findByName("Rice 25kg").orElseThrow().getQuantity());
    }

    @Test
    void aSaleThatTheDatabaseItselfRefusesAlsoPutsTheStockBack() throws SQLException {
        // A real database failure instead of a pretend one: a sale line may not have a price of 0 (CHECK, lesson 28).
        products.add(new Product("Free sample", Money.of("1.00"), 0, 5));
        Sale draft = new Sale(0, Customer.WALK_IN, java.time.LocalDateTime.now());
        draft.addLine(new SaleLine("Free sample", Money.of("0.00"), 1)); // breaks ck_sale_line_price
        assertThrows(DatabaseException.class, () -> database.runInTransaction(() -> {
            Product product = products.findByName("Free sample").orElseThrow();
            product.sell(1);
            products.update(product);   // step 1 works ...
            realSales.add(draft);       // ... step 2 is refused by the schema
        }));
        assertEquals(5, products.findByName("Free sample").orElseThrow().getQuantity());
        assertEquals(0, rowsCommitted("sale"));
    }

    @Test
    void aHalfFinishedImportLeavesTheDatabaseEmpty() throws SQLException {
        Inventory sample = Inventory.sample();
        sample.sell("Rice 25kg", 2);
        ProductRepository empty = new JdbcProductRepository(database);
        SaleRepository brokenSales = new SaleRepository() {
            @Override
            public Sale add(Sale draft) {
                throw new IllegalStateException("the import broke at the sales");
            }

            @Override
            public List<Sale> findAll() {
                return List.of();
            }
        };
        try (Database fresh = Database.openMemory("tx-fresh-" + UUID.randomUUID())) {
            Migrator.standard(fresh).migrate();
            ShopService importing = new ShopService(new JdbcProductRepository(fresh), brokenSales, new JdbcStockMovementRepository(fresh),
                    Clock.system(Inventory.SHOP_ZONE), fresh);
            assertThrows(IllegalStateException.class, () -> importing.importFrom(sample));
            assertEquals(0, new JdbcProductRepository(fresh).findAll().size());
        }
        assertEquals(1, empty.findAll().size()); // the other database is untouched
    }
}
