package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void aNewProductRemembersItsValuesAndStartsWithZeroStock() {
        Product rice = new Product("Rice 25kg", 125050, 10);
        assertEquals("Rice 25kg", rice.name);
        assertEquals(125050, rice.priceCentavos);
        assertEquals(10, rice.reorderLevel);
        assertEquals(0, rice.quantity);
    }

    @Test
    void statusFollowsTheReorderLevel() {
        Product rice = new Product("Rice 25kg", 125050, 10);
        assertEquals("OUT", rice.status());
        rice.receive(10);
        assertEquals("LOW", rice.status());
        rice.receive(1);
        assertEquals("OK", rice.status());
    }

    @Test
    void stockValueIsPriceTimesQuantity() {
        Product oil = new Product("Cooking Oil 1L", 9800, 10);
        oil.receive(4);
        assertEquals(39200, oil.stockValue());
    }

    @Test
    void receiveAddsToTheQuantity() {
        Product sugar = new Product("Sugar 1kg", 7500, 10);
        sugar.receive(24);
        sugar.receive(6);
        assertEquals(30, sugar.quantity);
    }

    @Test
    void sellTakesStockOffAndReturnsTrue() {
        Product sugar = new Product("Sugar 1kg", 7500, 10);
        sugar.receive(10);
        assertTrue(sugar.sell(4));
        assertEquals(6, sugar.quantity);
    }

    @Test
    void sellRefusesWhenThereIsNotEnoughAndChangesNothing() {
        Product sugar = new Product("Sugar 1kg", 7500, 10);
        sugar.receive(3);
        assertFalse(sugar.sell(4));
        assertEquals(3, sugar.quantity);
    }

    @Test
    void eachObjectKeepsItsOwnData() {
        Product a = new Product("A", 100, 1);
        Product b = new Product("B", 200, 1);
        a.receive(5);
        assertEquals(5, a.quantity);
        assertEquals(0, b.quantity);
    }

    @Test
    void toStringDescribesTheProduct() {
        Product rice = new Product("Rice 25kg", 125050, 10);
        rice.receive(12);
        assertEquals("Rice 25kg (12 in stock at PHP 1,250.50)", rice.toString());
    }
}
