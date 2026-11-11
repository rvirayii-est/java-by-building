package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductTest {

    private Product rice() {
        return new Product("Rice 25kg", Money.of("1250.50"), 10);
    }

    @Test
    void aNewProductRemembersItsValuesAndStartsWithZeroStock() {
        Product rice = rice();
        assertEquals("Rice 25kg", rice.getName());
        assertEquals(Money.of("1250.50"), rice.getPrice());
        assertEquals(10, rice.getReorderLevel());
        assertEquals(0, rice.getQuantity());
    }

    @Test
    void theConstructorRefusesValuesThatWouldMakeANonsenseProduct() {
        assertThrows(InvalidInputException.class, () -> new Product("  ", Money.of("1.00"), 1));
        assertThrows(InvalidInputException.class, () -> new Product(null, Money.of("1.00"), 1));
        assertThrows(InvalidInputException.class, () -> new Product("Salt", Money.ZERO, 1));
        assertThrows(InvalidInputException.class, () -> new Product("Salt", Product.MAX_PRICE.add(Money.of("0.01")), 1));
        assertThrows(InvalidInputException.class, () -> new Product("Salt", Money.of("1.00"), -1));
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
        Product oil = new Product("Cooking Oil 1L", Money.of("98.00"), 10);
        oil.receive(4);
        assertEquals(Money.of("392.00"), oil.stockValue());
    }

    @Test
    void receiveAddsToTheQuantity() {
        Product sugar = new Product("Sugar 1kg", Money.of("75.00"), 10);
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
        rice.setPrice(Money.of("1300.00"));
        assertEquals(Money.of("1300.00"), rice.getPrice());
    }

    @Test
    void setPriceRefusesBadPricesAndKeepsTheOldPrice() {
        Product rice = rice();
        assertThrows(InvalidInputException.class, () -> rice.setPrice(Money.ZERO));
        assertThrows(InvalidInputException.class, () -> rice.setPrice(Money.of("-5.00")));
        assertThrows(InvalidInputException.class, () -> rice.setPrice(Product.MAX_PRICE.add(Money.of("0.01"))));
        assertEquals(Money.of("1250.50"), rice.getPrice());
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
        Product a = new Product("A", Money.of("1.00"), 1);
        Product b = new Product("B", Money.of("2.00"), 1);
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
        assertEquals(Money.of("10000.00"), Product.MAX_PRICE);
    }

    @Test
    void aValidPriceIsFromOneCentavoUpToTheMaximum() {
        assertFalse(Product.isValidPrice(Money.ZERO));
        assertTrue(Product.isValidPrice(Money.of("0.01")));
        assertTrue(Product.isValidPrice(Product.MAX_PRICE));
        assertFalse(Product.isValidPrice(Product.MAX_PRICE.add(Money.of("0.01"))));
        assertFalse(Product.isValidPrice(null));
    }

    @Test
    void aPriceIsKeptWithExactlyTwoDecimals() {
        assertEquals(Money.of("35.00"), new Product("Salt", new BigDecimal("35"), 1).getPrice());
        assertEquals(Money.of("35.13"), new Product("Salt", new BigDecimal("35.125"), 1).getPrice()); // half up
    }

    @Test
    void aProductCanBeRebuiltWithItsStock() {
        Product salt = new Product("Salt", Money.of("35.00"), 5, 20);
        assertEquals(20, salt.getQuantity());
        assertThrows(InvalidInputException.class, () -> new Product("Salt", Money.of("35.00"), 5, -1));
    }

    @Test
    void aValidReorderLevelIsZeroOrMore() {
        assertFalse(Product.isValidReorderLevel(-1));
        assertTrue(Product.isValidReorderLevel(0));
    }
}
