package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * ShopService must behave exactly like Inventory, whatever its repositories store things in. These tests run the same
 * steps on both and compare. Each subclass says which storage to use.
 */
abstract class ShopServiceContract {

    /** A ShopService on a brand-new empty storage. */
    abstract ShopService create(TestClock clock);

    final TestClock clock = new TestClock(Instant.parse("2026-03-01T01:30:00Z"), Inventory.SHOP_ZONE); // 09:30 in Manila
    ShopService shop;

    @BeforeEach
    void createShop() {
        shop = create(clock);
        shop.importFrom(Inventory.sample(new Inventory(clock)));
    }

    @Test
    void importFromCopiesProductsAndTheStockLog() {
        Inventory sample = Inventory.sample(new Inventory(clock));
        assertEquals(sample.report(), shop.report());
        assertEquals(sample.movements(), shop.movements());
        assertEquals(sample.totalValue(), shop.totalValue());
        assertTrue(shop.audit().isEmpty());
    }

    @Test
    void findAnswersWithTheStoredNameOrNull() {
        assertEquals("Rice 25kg", shop.find("  rice 25KG"));
        assertNull(shop.find("salt"));
    }

    @Test
    void sellingTakesStockLogsTheMovementAndStoresTheSale() {
        Sale sale = shop.sell("Rice 25kg", 5, Customer.WALK_IN, new NoDiscount(), PaymentMethod.CASH);
        assertEquals(1, sale.getNumber());
        assertEquals(Money.of("6252.50"), sale.total());
        assertEquals(7, shop.quantityOf("Rice 25kg"));
        assertEquals(1, shop.sales().size());
        StockMovement last = shop.movements().get(shop.movements().size() - 1);
        assertEquals(new StockMovement("Rice 25kg", MovementType.SOLD, 5, last.at()), last);
        assertTrue(shop.audit().isEmpty());
    }

    @Test
    void aRefusedSaleChangesNothing() {
        int movementsBefore = shop.movements().size();
        assertThrows(InsufficientStockException.class, () -> shop.sell("Rice 25kg", 13, Customer.WALK_IN, new NoDiscount(), PaymentMethod.CASH));
        assertThrows(UnknownProductException.class, () -> shop.sell("salt", 1, Customer.WALK_IN, new NoDiscount(), PaymentMethod.CASH));
        assertThrows(InvalidInputException.class, () -> shop.sell("Rice 25kg", 0, Customer.WALK_IN, new NoDiscount(), PaymentMethod.CASH));
        assertEquals(12, shop.quantityOf("Rice 25kg"));
        assertEquals(0, shop.sales().size());
        assertEquals(movementsBefore, shop.movements().size());
    }

    @Test
    void receivingAddsStockAndLogsIt() {
        shop.receive("Sugar 1kg", 20);
        assertEquals(20, shop.quantityOf("Sugar 1kg"));
        StockMovement last = shop.movements().get(shop.movements().size() - 1);
        assertEquals(MovementType.RECEIVED, last.type());
        assertEquals(20, last.quantity());
        assertTrue(shop.audit().isEmpty());
    }

    @Test
    void receivingForAnUnknownProductIsRefused() {
        assertThrows(UnknownProductException.class, () -> shop.receive("salt", 5));
        assertThrows(InvalidInputException.class, () -> shop.receive("Sugar 1kg", 0));
    }

    @Test
    void addingAProductStartsItAtZero() {
        shop.addProduct("  Coffee   Sachet ", Money.of("8.50"), 50);
        assertEquals(0, shop.quantityOf("coffee sachet"));
        assertEquals("Coffee Sachet", shop.getProduct("coffee sachet").getName());
        assertEquals(5, shop.products().size());
    }

    @Test
    void addingTheSameProductTwiceOrABadOneIsRefused() {
        assertThrows(InvalidInputException.class, () -> shop.addProduct("rice 25kg", Money.of("1.00"), 1));
        assertThrows(InvalidInputException.class, () -> shop.addProduct("Salt", Money.of("0.00"), 1));
        assertThrows(InvalidInputException.class, () -> shop.addProduct("   ", Money.of("1.00"), 1));
        assertEquals(4, shop.products().size());
    }

    @Test
    void anUnknownProductIsReportedByName() {
        UnknownProductException e = assertThrows(UnknownProductException.class, () -> shop.getProduct("salt"));
        assertEquals("Unknown product: salt", e.getMessage());
    }

    @Test
    void aCustomerEarnsPointsFromTheSale() {
        Customer ana = new Customer("Ana Reyes", "0917");
        shop.sell("Rice 25kg", 2, ana, new NoDiscount(), PaymentMethod.CARD); // 2501.00 -> 25 points
        assertEquals(25, ana.getPoints());
    }

    @Test
    void theSaleKeepsItsDiscountAndPayment() {
        shop.sell("Cooking Oil 1L", 2, Customer.WALK_IN, new PercentDiscount(10), PaymentMethod.GCASH);
        Sale stored = shop.sales().get(0);
        assertEquals(PaymentMethod.GCASH, stored.getPayment());
        assertEquals(Money.of("176.40"), stored.total()); // 196.00 minus 10 percent
    }

    @Test
    void theSameStepsGiveTheSameAnswersAsInventory() {
        Inventory inventory = Inventory.sample(new Inventory(clock));
        for (Shop each : List.<Shop>of(inventory, shop)) {
            each.receive("Sugar 1kg", 30);
            each.sell("Rice 25kg", 4);
            each.sell("Cooking Oil 1L", 2, new Customer("Ben Santos", "0918"), new PercentDiscount(10), PaymentMethod.GCASH);
            each.sell("Sardines 155g", 60);
            each.addProduct("Coffee Sachet", Money.of("8.50"), 50);
        }
        assertEquals(inventory.report(), shop.report());
        assertEquals(inventory.totalValue(), shop.totalValue());
        assertEquals(inventory.salesTotal(), shop.salesTotal());
        assertEquals(inventory.movements(), shop.movements());
        assertEquals(inventory.audit(), shop.audit());
        assertEquals(ReorderAdvisor.report(inventory.reorderSuggestions()), ReorderAdvisor.report(shop.reorderSuggestions()));
        BigDecimal inventorySales = inventory.sales().stream().map(Sale::total).reduce(Money.ZERO, BigDecimal::add);
        BigDecimal shopSales = shop.sales().stream().map(Sale::total).reduce(Money.ZERO, BigDecimal::add);
        assertEquals(inventorySales, shopSales);
    }
}
