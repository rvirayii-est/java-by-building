package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class InventoryTest {

    // A Clock that always says 1 March 2026, 09:30 in Manila, so the times in these tests never depend on the real day.
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-03-01T01:30:00Z"), Inventory.SHOP_ZONE);
    private static final LocalDateTime AT = LocalDateTime.of(2026, 3, 1, 9, 30);

    private Inventory shop() {
        Inventory inventory = new Inventory(CLOCK);
        inventory.addProduct("Rice 25kg", Money.of("1250.50"), 10);
        inventory.addProduct("Sugar 1kg", Money.of("75.00"), 10);
        inventory.receive("Rice 25kg", 12);
        return inventory;
    }

    @Test
    void addProductStartsWithZeroStock() {
        Inventory inventory = shop();
        assertEquals(2, inventory.size());
        assertEquals(Money.of("75.00"), inventory.priceOf("Sugar 1kg"));
        assertEquals(0, inventory.quantityOf("Sugar 1kg"));
    }

    @Test
    void findIgnoresCapitalsAndExtraSpacesAndReturnsTheStoredName() {
        assertEquals("Sugar 1kg", shop().find("  sUGAr   1KG "));
        assertNull(shop().find("Salt")); // find is a question ("is it there?"), so null is a fair answer
    }

    @Test
    void getProductFindsAProductOrThrows() {
        Inventory inventory = shop();
        assertEquals("Rice 25kg", inventory.getProduct(" RICE 25kg").getName());
        UnknownProductException e = assertThrows(UnknownProductException.class, () -> inventory.getProduct("Salt"));
        assertEquals("Unknown product: Salt", e.getMessage());
        assertEquals("Salt", e.getProductName());
    }

    @Test
    void addProductRejectsADuplicateIgnoringCapitals() {
        Inventory inventory = shop();
        InvalidInputException e = assertThrows(InvalidInputException.class, () -> inventory.addProduct("rice 25KG", Money.of("1.00"), 1));
        assertEquals("That product already exists: Rice 25kg", e.getMessage());
        assertEquals(2, inventory.size());
        assertEquals(Money.of("1250.50"), inventory.priceOf("Rice 25kg"));
    }

    @Test
    void addProductRejectsBadValues() {
        Inventory inventory = new Inventory();
        assertThrows(InvalidInputException.class, () -> inventory.addProduct("   ", Money.of("1.00"), 1));
        assertThrows(InvalidInputException.class, () -> inventory.addProduct("Salt", Money.ZERO, 1));
        assertThrows(InvalidInputException.class, () -> inventory.addProduct("Salt", Money.of("1.00"), -1));
        assertEquals(0, inventory.size());
    }

    @Test
    void addProductRefusesAPriceAboveTheMaximum() {
        Inventory inventory = new Inventory();
        assertThrows(InvalidInputException.class, () -> inventory.addProduct("Gold bar", Product.MAX_PRICE.add(Money.of("0.01")), 1));
        inventory.addProduct("Fancy rice", Product.MAX_PRICE, 1);
        assertEquals(1, inventory.size());
    }

    @Test
    void unknownProductsThrowInsteadOfReturningMinusOne() {
        Inventory inventory = shop();
        assertThrows(UnknownProductException.class, () -> inventory.quantityOf("Salt"));
        assertThrows(UnknownProductException.class, () -> inventory.priceOf("Salt"));
    }

    @Test
    void receiveAddsStock() {
        Inventory inventory = shop();
        inventory.receive(" rice 25kg ", 8);
        assertEquals(20, inventory.quantityOf("Rice 25kg"));
    }

    @Test
    void receiveRejectsUnknownProductsAndAmountsOfZeroOrLess() {
        Inventory inventory = shop();
        assertThrows(UnknownProductException.class, () -> inventory.receive("Salt", 5));
        assertThrows(InvalidInputException.class, () -> inventory.receive("Rice 25kg", 0));
        assertThrows(InvalidInputException.class, () -> inventory.receive("Rice 25kg", -4));
        assertEquals(12, inventory.quantityOf("Rice 25kg"));
    }

    @Test
    void sellReturnsTheTotalAndReducesStock() {
        Inventory inventory = shop();
        assertEquals(Money.of("2501.00"), inventory.sell("rice 25kg", 2));
        assertEquals(10, inventory.quantityOf("Rice 25kg"));
    }

    @Test
    void salesTotalAddsUpEverySale() {
        Inventory inventory = shop();
        inventory.sell("Rice 25kg", 1);
        inventory.sell("Rice 25kg", 2);
        assertEquals(Money.of("3751.50"), inventory.salesTotal());
    }

    @Test
    void sellRefusesAndChangesNothingWhenItCannot() {
        Inventory inventory = shop();
        assertThrows(InsufficientStockException.class, () -> inventory.sell("Rice 25kg", 13));
        assertThrows(InsufficientStockException.class, () -> inventory.sell("Sugar 1kg", 1));
        assertThrows(UnknownProductException.class, () -> inventory.sell("Salt", 1));
        assertThrows(InvalidInputException.class, () -> inventory.sell("Rice 25kg", 0));
        assertEquals(12, inventory.quantityOf("Rice 25kg"));
        assertEquals(Money.ZERO, inventory.salesTotal());
    }

    @Test
    void theInsufficientStockExceptionCarriesTheNumbers() {
        Inventory inventory = shop();
        InsufficientStockException e = assertThrows(InsufficientStockException.class, () -> inventory.sell("rice 25kg", 50));
        assertEquals("Rice 25kg", e.getProductName());
        assertEquals(50, e.getRequested());
        assertEquals(12, e.getAvailable());
        assertEquals("Not enough stock: only 12 of Rice 25kg left.", e.getMessage());
    }

    @Test
    void aRefusedSaleThrowsAndLeavesNoTrace() {
        Inventory inventory = shop();
        Customer ana = new Customer("Ana", "");
        assertThrows(InsufficientStockException.class, () -> inventory.sell("Rice 25kg", 13, ana));
        assertThrows(UnknownProductException.class, () -> inventory.sell("Salt", 1, ana));
        assertEquals(0, inventory.sales().size());
        assertEquals(0, ana.getPoints());
        assertEquals(12, inventory.quantityOf("Rice 25kg"));
        assertEquals(1, inventory.movements().size()); // only the original delivery
    }

    @Test
    void sellingToACustomerRecordsASaleAndGivesPoints() {
        Inventory inventory = shop();
        Customer ana = new Customer("Ana Reyes", "");
        Sale sale = inventory.sell("rice 25kg", 2, ana);
        assertEquals(1, sale.getNumber());
        assertEquals(Money.of("2501.00"), sale.total());
        assertEquals("Rice 25kg", sale.getLines().get(0).productName());
        assertEquals(25, ana.getPoints());
        assertEquals(10, inventory.quantityOf("Rice 25kg"));
    }

    @Test
    void menuStyleSalesAreRecordedForTheSharedWalkInCustomer() {
        Inventory inventory = shop();
        inventory.sell("Rice 25kg", 2);
        Sale sale = inventory.sales().get(0);
        assertSame(Customer.WALK_IN, sale.getCustomer());
        assertEquals(0, Customer.WALK_IN.getPoints());
    }

    @Test
    void aDiscountedSaleRecordsTheDiscountedTotalAndPointsOnThatTotal() {
        Inventory inventory = shop();
        Customer lola = new Customer("Lola Pepita", "");
        Sale sale = inventory.sell("Rice 25kg", 2, lola, new SeniorPwdDiscount());
        assertEquals(Money.of("2501.00"), sale.subtotal());
        assertEquals(Money.of("2000.80"), sale.total());
        assertEquals(20, lola.getPoints()); // 2000.80 pesos earns 20 points, not 25
        assertEquals(Money.of("2000.80"), inventory.salesTotal());
    }

    @Test
    void aSaleWithoutADiscountArgumentUsesNoDiscount() {
        Sale sale = shop().sell("Rice 25kg", 1, new Customer("Ana", ""));
        assertEquals(Money.ZERO, sale.discountAmount());
        assertEquals("No discount", sale.getDiscount().label());
    }

    @Test
    void theSalesPaymentMethodIsRecorded() {
        Inventory inventory = shop();
        Sale cash = inventory.sell("Rice 25kg", 1, new Customer("Ana", ""));
        Sale gcash = inventory.sell("Rice 25kg", 1, new Customer("Ben", ""), new NoDiscount(), PaymentMethod.GCASH);
        assertEquals(PaymentMethod.CASH, cash.getPayment());
        assertEquals(PaymentMethod.GCASH, gcash.getPayment());
    }

    @Test
    void theStockLogRecordsEveryAcceptedReceiveAndSaleInOrder() {
        Inventory inventory = shop();
        inventory.receive("Sugar 1kg", 24);
        inventory.sell("Rice 25kg", 2);
        List<StockMovement> log = inventory.movements();
        assertEquals(new StockMovement("Rice 25kg", MovementType.RECEIVED, 12, AT), log.get(0));
        assertEquals(new StockMovement("Sugar 1kg", MovementType.RECEIVED, 24, AT), log.get(1));
        assertEquals(new StockMovement("Rice 25kg", MovementType.SOLD, 2, AT), log.get(2));
        assertEquals(3, log.size());
    }

    @Test
    void refusedOperationsLeaveNothingInTheLog() {
        Inventory inventory = shop();
        int before = inventory.movements().size();
        assertThrows(UnknownProductException.class, () -> inventory.receive("Salt", 5));
        assertThrows(InvalidInputException.class, () -> inventory.receive("Rice 25kg", 0));
        assertThrows(InsufficientStockException.class, () -> inventory.sell("Rice 25kg", 99));
        assertThrows(InsufficientStockException.class, () -> inventory.sell("Sugar 1kg", 1));
        assertEquals(before, inventory.movements().size());
    }

    @Test
    void theLogAndTheShelfAgree() {
        Inventory sample = Inventory.sample();
        sample.sell("Rice 25kg", 5);
        for (Product product : sample.products()) {
            assertEquals(product.getQuantity(), sample.netMovement(product.getName()));
        }
        assertThrows(UnknownProductException.class, () -> sample.netMovement("Salt"));
    }

    @Test
    void salesAreNumberedInOrderAndSnapshotted() {
        Inventory inventory = shop();
        inventory.sell("Rice 25kg", 1);
        List<Sale> snapshot = inventory.sales();
        inventory.sell("Rice 25kg", 1);
        assertEquals(1, snapshot.size());
        assertEquals(2, inventory.sales().size());
        assertEquals(2, inventory.sales().get(1).getNumber());
    }

    @Test
    void changePriceUsesTheProductsOwnRules() {
        Inventory inventory = shop();
        inventory.changePrice(" sugar 1KG ", Money.of("80.00"));
        assertEquals(Money.of("80.00"), inventory.priceOf("Sugar 1kg"));
        assertThrows(InvalidInputException.class, () -> inventory.changePrice("Sugar 1kg", Money.of("0.00")));
        assertThrows(UnknownProductException.class, () -> inventory.changePrice("Salt", Money.of("1.00")));
        assertEquals(Money.of("80.00"), inventory.priceOf("Sugar 1kg"));
    }

    @Test
    void productsIsASnapshotInTheOrderTheyWereAdded() {
        Inventory inventory = shop();
        List<Product> snapshot = inventory.products();
        inventory.addProduct("Salt", Money.of("35.00"), 5);
        assertEquals(2, snapshot.size());
        assertEquals("Rice 25kg", snapshot.get(0).getName());
        assertEquals("Sugar 1kg", snapshot.get(1).getName());
        assertEquals(3, inventory.products().size());
    }

    @Test
    void reportListsProductsInTheOrderTheyWereAdded() {
        String expected = "Product         Qty  Status\n"
                + "Rice 25kg        12  OK\n"
                + "Sugar 1kg         0  OUT";
        assertEquals(expected, shop().report());
    }

    @Test
    void totalValueIsPriceTimesQuantityForEveryProduct() {
        assertEquals(Money.of("15006.00"), shop().totalValue());
    }

    @Test
    void sampleHasTheFourProductsFromTheStockReport() {
        Inventory sample = Inventory.sample();
        assertEquals(4, sample.size());
        assertEquals(12, sample.quantityOf("Rice 25kg"));
        assertEquals(0, sample.quantityOf("Sugar 1kg"));
        assertEquals(Money.of("16838.00"), sample.totalValue());
    }
}
