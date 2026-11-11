package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ProductTest {

    private Product rice() {
        return new Product("Rice 25kg", 125050, 10);
    }

    @Test
    void aNewProductRemembersItsValuesAndStartsWithZeroStock() {
        Product rice = rice();
        assertEquals("Rice 25kg", rice.getName());
        assertEquals(125050, rice.getPriceCentavos());
        assertEquals(10, rice.getReorderLevel());
        assertEquals(0, rice.getQuantity());
    }

    @Test
    void statusFollowsTheReorderLevel() {
        Product rice = rice();
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
        assertTrue(sugar.receive(24));
        assertTrue(sugar.receive(6));
        assertEquals(30, sugar.getQuantity());
    }

    @Test
    void receiveRefusesZeroOrLessAndChangesNothing() {
        Product sugar = rice();
        sugar.receive(5);
        assertFalse(sugar.receive(0));
        assertFalse(sugar.receive(-3));
        assertEquals(5, sugar.getQuantity());
    }

    @Test
    void sellTakesStockOffAndReturnsTrue() {
        Product sugar = rice();
        sugar.receive(10);
        assertTrue(sugar.sell(4));
        assertEquals(6, sugar.getQuantity());
    }

    @Test
    void sellRefusesWhenThereIsNotEnoughAndChangesNothing() {
        Product sugar = rice();
        sugar.receive(3);
        assertFalse(sugar.sell(4));
        assertEquals(3, sugar.getQuantity());
    }

    @Test
    void sellRefusesZeroOrLess() {
        Product sugar = rice();
        sugar.receive(3);
        assertFalse(sugar.sell(0));
        assertFalse(sugar.sell(-1));
        assertEquals(3, sugar.getQuantity());
    }

    @Test
    void setPriceChangesThePrice() {
        Product rice = rice();
        assertTrue(rice.setPrice(130000));
        assertEquals(130000, rice.getPriceCentavos());
    }

    @Test
    void setPriceRefusesZeroOrLessAndKeepsTheOldPrice() {
        Product rice = rice();
        assertFalse(rice.setPrice(0));
        assertFalse(rice.setPrice(-500));
        assertEquals(125050, rice.getPriceCentavos());
    }

    @Test
    void setReorderLevelAllowsZeroButNotNegative() {
        Product rice = rice();
        assertTrue(rice.setReorderLevel(0));
        assertEquals(0, rice.getReorderLevel());
        assertFalse(rice.setReorderLevel(-1));
        assertEquals(0, rice.getReorderLevel());
    }

    @Test
    void eachObjectKeepsItsOwnData() {
        Product a = new Product("A", 100, 1);
        Product b = new Product("B", 200, 1);
        a.receive(5);
        assertEquals(5, a.getQuantity());
        assertEquals(0, b.getQuantity());
    }

    @Test
    void toStringDescribesTheProduct() {
        Product rice = rice();
        rice.receive(12);
        assertEquals("Rice 25kg (12 in stock at PHP 1,250.50)", rice.toString());
    }
}
