package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void theConstructorRefusesValuesThatWouldMakeANonsenseProduct() {
        assertThrows(InvalidInputException.class, () -> new Product("  ", 100, 1));
        assertThrows(InvalidInputException.class, () -> new Product(null, 100, 1));
        assertThrows(InvalidInputException.class, () -> new Product("Salt", 0, 1));
        assertThrows(InvalidInputException.class, () -> new Product("Salt", Product.MAX_PRICE + 1, 1));
        assertThrows(InvalidInputException.class, () -> new Product("Salt", 100, -1));
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
        sugar.receive(24);
        sugar.receive(6);
        assertEquals(30, sugar.getQuantity());
    }

    @Test
    void receiveRefusesZeroOrLessAndChangesNothing() {
        Product sugar = rice();
        sugar.receive(5);
        InvalidInputException e = assertThrows(InvalidInputException.class, () -> sugar.receive(0));
        assertEquals("Quantity must be at least 1.", e.getMessage());
        assertThrows(InvalidInputException.class, () -> sugar.receive(-3));
        assertEquals(5, sugar.getQuantity());
    }

    @Test
    void sellTakesStockOff() {
        Product sugar = rice();
        sugar.receive(10);
        sugar.sell(4);
        assertEquals(6, sugar.getQuantity());
    }

    @Test
    void sellRefusesWhenThereIsNotEnoughAndChangesNothing() {
        Product sugar = rice();
        sugar.receive(3);
        InsufficientStockException e = assertThrows(InsufficientStockException.class, () -> sugar.sell(4));
        assertEquals(4, e.getRequested());
        assertEquals(3, e.getAvailable());
        assertEquals(3, sugar.getQuantity());
    }

    @Test
    void sellingExactlyWhatIsLeftIsAllowed() {
        Product sugar = rice();
        sugar.receive(3);
        sugar.sell(3);
        assertEquals(0, sugar.getQuantity());
    }

    @Test
    void sellRefusesZeroOrLess() {
        Product sugar = rice();
        sugar.receive(3);
        assertThrows(InvalidInputException.class, () -> sugar.sell(0));
        assertThrows(InvalidInputException.class, () -> sugar.sell(-1));
        assertEquals(3, sugar.getQuantity());
    }

    @Test
    void setPriceChangesThePrice() {
        Product rice = rice();
        rice.setPrice(130000);
        assertEquals(130000, rice.getPriceCentavos());
    }

    @Test
    void setPriceRefusesBadPricesAndKeepsTheOldPrice() {
        Product rice = rice();
        assertThrows(InvalidInputException.class, () -> rice.setPrice(0));
        assertThrows(InvalidInputException.class, () -> rice.setPrice(-500));
        assertThrows(InvalidInputException.class, () -> rice.setPrice(Product.MAX_PRICE + 1));
        assertEquals(125050, rice.getPriceCentavos());
    }

    @Test
    void setReorderLevelAllowsZeroButNotNegative() {
        Product rice = rice();
        rice.setReorderLevel(0);
        assertEquals(0, rice.getReorderLevel());
        assertThrows(InvalidInputException.class, () -> rice.setReorderLevel(-1));
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

    @Test
    void theMaximumPriceIsTenThousandPesos() {
        assertEquals(1_000_000, Product.MAX_PRICE);
    }

    @Test
    void aValidPriceIsFromOneCentavoUpToTheMaximum() {
        assertFalse(Product.isValidPrice(0));
        assertTrue(Product.isValidPrice(1));
        assertTrue(Product.isValidPrice(Product.MAX_PRICE));
        assertFalse(Product.isValidPrice(Product.MAX_PRICE + 1));
    }

    @Test
    void aValidReorderLevelIsZeroOrMore() {
        assertFalse(Product.isValidReorderLevel(-1));
        assertTrue(Product.isValidReorderLevel(0));
    }
}
