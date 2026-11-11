package dev.javabybuilding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.javabybuilding.inventory.Customer;
import dev.javabybuilding.inventory.Inventory;
import dev.javabybuilding.inventory.NoDiscount;
import dev.javabybuilding.inventory.PaymentMethod;
import dev.javabybuilding.inventory.ProductRepository;
import dev.javabybuilding.inventory.SaleRepository;
import dev.javabybuilding.inventory.ShopService;
import dev.javabybuilding.inventory.StockMovement;
import dev.javabybuilding.inventory.StockMovementRepository;
import dev.javabybuilding.inventory.Transactor;
import java.time.Clock;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * A sale is all-or-nothing (lesson 31), on JPA. We build a ShopService whose LAST step (the stock log) breaks on purpose, and
 * look at what the database holds afterwards. There is no test-managed transaction here: the SpringTransactor really commits or
 * really rolls back, and the reads that follow see only committed rows.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"spring.datasource.url=jdbc:h2:mem:tx-test;DB_CLOSE_DELAY=-1", "shop.seed-sample=false"})
class JpaTransactionTest {

    @Autowired ProductRepository products;
    @Autowired SaleRepository sales;
    @Autowired StockMovementRepository movements;
    @Autowired Transactor transactor;
    @Autowired ShopService shop;
    @Autowired Clock clock;

    /** A stock log that breaks on demand: it pretends the disk filled up just before the last step of the sale. */
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

    @Test
    void whenTheLastStepFailsTheShelfAndTheReceiptAreRolledBack() {
        shop.importFrom(Inventory.sample());
        ShopService broken = new ShopService(products, sales, new BrokenMovements(), clock, transactor);

        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> broken.sell("Rice 25kg", 5, Customer.WALK_IN, new NoDiscount(), PaymentMethod.CASH));

        assertEquals("disk full", e.getMessage());
        assertEquals(12, shop.quantityOf("Rice 25kg"));   // step 1 (take stock off) was undone
        assertTrue(shop.sales().isEmpty());                // step 2 (store the receipt) was undone
        assertTrue(shop.audit().isEmpty());                // shelf and stock log still agree
    }
}
