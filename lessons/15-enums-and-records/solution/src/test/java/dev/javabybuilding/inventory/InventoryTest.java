package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class InventoryTest {

    private Inventory shop() {
        Inventory inventory = new Inventory();
        inventory.addProduct("Rice 25kg", 125050, 10);
        inventory.addProduct("Sugar 1kg", 7500, 10);
        inventory.receive("Rice 25kg", 12);
        return inventory;
    }

    @Test
    void addProductStartsWithZeroStock() {
        Inventory inventory = shop();
        assertEquals(2, inventory.size());
        assertEquals(7500, inventory.priceOf("Sugar 1kg"));
        assertEquals(0, inventory.quantityOf("Sugar 1kg"));
    }

    @Test
    void findIgnoresCapitalsAndExtraSpacesAndReturnsTheStoredName() {
        assertEquals("Sugar 1kg", shop().find("  sUGAr   1KG "));
        assertNull(shop().find("Salt"));
    }

    @Test
    void addProductRejectsADuplicateIgnoringCapitals() {
        Inventory inventory = shop();
        assertFalse(inventory.addProduct("rice 25KG", 100, 1));
        assertEquals(2, inventory.size());
        assertEquals(125050, inventory.priceOf("Rice 25kg"));
    }

    @Test
    void addProductRejectsBadValues() {
        Inventory inventory = new Inventory();
        assertFalse(inventory.addProduct("   ", 100, 1));
        assertFalse(inventory.addProduct("Salt", 0, 1));
        assertFalse(inventory.addProduct("Salt", 100, -1));
        assertEquals(0, inventory.size());
    }

    @Test
    void unknownProductsAreMinusOne() {
        assertEquals(-1, shop().quantityOf("Salt"));
        assertEquals(-1, shop().priceOf("Salt"));
    }

    @Test
    void receiveAddsStock() {
        Inventory inventory = shop();
        assertTrue(inventory.receive(" rice 25kg ", 8));
        assertEquals(20, inventory.quantityOf("Rice 25kg"));
    }

    @Test
    void receiveRejectsUnknownProductsAndAmountsOfZeroOrLess() {
        Inventory inventory = shop();
        assertFalse(inventory.receive("Salt", 5));
        assertFalse(inventory.receive("Rice 25kg", 0));
        assertFalse(inventory.receive("Rice 25kg", -4));
        assertEquals(12, inventory.quantityOf("Rice 25kg"));
    }

    @Test
    void sellReturnsTheTotalAndReducesStock() {
        Inventory inventory = shop();
        assertEquals(250100, inventory.sell("rice 25kg", 2));
        assertEquals(10, inventory.quantityOf("Rice 25kg"));
    }

    @Test
    void salesTotalAddsUpEverySale() {
        Inventory inventory = shop();
        inventory.sell("Rice 25kg", 1);
        inventory.sell("Rice 25kg", 2);
        assertEquals(3 * 125050, inventory.salesTotal());
    }

    @Test
    void sellRefusesAndChangesNothingWhenItCannot() {
        Inventory inventory = shop();
        assertEquals(-1, inventory.sell("Rice 25kg", 13));
        assertEquals(-1, inventory.sell("Sugar 1kg", 1));
        assertEquals(-1, inventory.sell("Salt", 1));
        assertEquals(-1, inventory.sell("Rice 25kg", 0));
        assertEquals(12, inventory.quantityOf("Rice 25kg"));
        assertEquals(0, inventory.salesTotal());
    }

    @Test
    void sellingToACustomerRecordsASaleAndGivesPoints() {
        Inventory inventory = shop();
        Customer ana = new Customer("Ana Reyes", "");
        Sale sale = inventory.sell("rice 25kg", 2, ana);
        assertEquals(1, sale.getNumber());
        assertEquals(250100, sale.total());
        assertEquals("Rice 25kg", sale.getLines().get(0).productName());
        assertEquals(25, ana.getPoints());
        assertEquals(10, inventory.quantityOf("Rice 25kg"));
    }

    @Test
    void addProductRefusesAPriceAboveTheMaximum() {
        Inventory inventory = new Inventory();
        assertFalse(inventory.addProduct("Gold bar", Product.MAX_PRICE + 1, 1));
        assertTrue(inventory.addProduct("Fancy rice", Product.MAX_PRICE, 1));
        assertEquals(1, inventory.size());
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
        assertEquals(250100, sale.subtotal());
        assertEquals(200080, sale.total());
        assertEquals(20, lola.getPoints()); // 2000.80 pesos earns 20 points, not 25
        assertEquals(200080, inventory.salesTotal());
    }

    @Test
    void aSaleWithoutADiscountArgumentUsesNoDiscount() {
        Sale sale = shop().sell("Rice 25kg", 1, new Customer("Ana", ""));
        assertEquals(0, sale.discountAmount());
        assertEquals("No discount", sale.getDiscount().label());
    }

    @Test
    void theStockLogRecordsEveryAcceptedReceiveAndSaleInOrder() {
        Inventory inventory = shop();
        inventory.receive("Sugar 1kg", 24);
        inventory.sell("Rice 25kg", 2);
        List<StockMovement> log = inventory.movements();
        assertEquals(new StockMovement("Rice 25kg", MovementType.RECEIVED, 12), log.get(0));
        assertEquals(new StockMovement("Sugar 1kg", MovementType.RECEIVED, 24), log.get(1));
        assertEquals(new StockMovement("Rice 25kg", MovementType.SOLD, 2), log.get(2));
        assertEquals(3, log.size());
    }

    @Test
    void refusedOperationsLeaveNothingInTheLog() {
        Inventory inventory = shop();
        int before = inventory.movements().size();
        inventory.receive("Salt", 5);
        inventory.receive("Rice 25kg", 0);
        inventory.sell("Rice 25kg", 99);
        inventory.sell("Sugar 1kg", 1);
        assertEquals(before, inventory.movements().size());
    }

    @Test
    void theLogAndTheShelfAgree() {
        Inventory sample = Inventory.sample();
        sample.sell("Rice 25kg", 5);
        for (Product product : sample.products()) {
            assertEquals(product.getQuantity(), sample.netMovement(product.getName()));
        }
        assertEquals(-1, sample.netMovement("Salt"));
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
    void aRefusedSaleIsNullAndLeavesNoTrace() {
        Inventory inventory = shop();
        Customer ana = new Customer("Ana", "");
        assertNull(inventory.sell("Rice 25kg", 13, ana));
        assertNull(inventory.sell("Salt", 1, ana));
        assertEquals(0, inventory.sales().size());
        assertEquals(0, ana.getPoints());
        assertEquals(12, inventory.quantityOf("Rice 25kg"));
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
        assertTrue(inventory.changePrice(" sugar 1KG ", 8000));
        assertEquals(8000, inventory.priceOf("Sugar 1kg"));
        assertFalse(inventory.changePrice("Sugar 1kg", 0));
        assertFalse(inventory.changePrice("Salt", 100));
        assertEquals(8000, inventory.priceOf("Sugar 1kg"));
    }

    @Test
    void productsIsASnapshotInTheOrderTheyWereAdded() {
        Inventory inventory = shop();
        List<Product> snapshot = inventory.products();
        inventory.addProduct("Salt", 3500, 5);
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
        assertEquals(12 * 125050, shop().totalValue());
    }

    @Test
    void sampleHasTheFourProductsFromTheStockReport() {
        Inventory sample = Inventory.sample();
        assertEquals(4, sample.size());
        assertEquals(12, sample.quantityOf("Rice 25kg"));
        assertEquals(0, sample.quantityOf("Sugar 1kg"));
        assertEquals(1683800, sample.totalValue());
    }
}
