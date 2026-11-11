package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.jupiter.api.Test;

/** The BigDecimal rules from the Money class, as tests you can read. */
class MoneyTest {

    @Test
    void aDoubleCannotHoldTenthsExactlyButBigDecimalCan() {
        assertNotEquals(0.3, 0.1 + 0.2); // 0.30000000000000004
        assertEquals(new BigDecimal("0.3"), new BigDecimal("0.1").add(new BigDecimal("0.2")));
    }

    @Test
    void buildingFromADoubleCopiesItsError() {
        assertNotEquals(new BigDecimal("0.1"), new BigDecimal(0.1)); // 0.1000000000000000055511151231257827...
        assertEquals(new BigDecimal("0.1"), BigDecimal.valueOf(0.1)); // valueOf goes through text, so it is clean
    }

    @Test
    void equalsAlsoComparesTheScaleButCompareToDoesNot() {
        BigDecimal two = new BigDecimal("2.0");
        BigDecimal twoExactly = new BigDecimal("2.00");
        assertNotEquals(two, twoExactly); // different scale (1 decimal vs 2)
        assertEquals(0, two.compareTo(twoExactly)); // same value
    }

    @Test
    void ofGivesExactlyTwoDecimals() {
        assertEquals("35.00", Money.of("35").toPlainString());
        assertEquals("1250.50", Money.of(" 1250.5 ").toPlainString());
        assertEquals("35.00", Money.of(35).toPlainString());
        assertEquals("0.00", Money.ZERO.toPlainString());
    }

    @Test
    void ofRefusesToThrowAwayDigitsSilently() {
        assertThrows(ArithmeticException.class, () -> Money.of("1.005"));
        assertThrows(NumberFormatException.class, () -> Money.of("abc"));
    }

    @Test
    void roundGoesHalfUp() {
        assertEquals(Money.of("0.13"), Money.round(new BigDecimal("0.125")));
        assertEquals(Money.of("0.12"), Money.round(new BigDecimal("0.124")));
        assertEquals(Money.of("2.68"), Money.round(new BigDecimal("2.678571")));
    }

    @Test
    void dividingNeedsARoundingRuleOrItMayThrow() {
        BigDecimal one = new BigDecimal("1");
        BigDecimal three = new BigDecimal("3");
        assertThrows(ArithmeticException.class, () -> one.divide(three)); // 0.333... never ends
        assertEquals(new BigDecimal("0.33"), one.divide(three, 2, RoundingMode.HALF_UP));
    }

    @Test
    void arithmeticIsMethodCallsBecauseBigDecimalIsAnObject() {
        BigDecimal price = Money.of("1250.50");
        BigDecimal total = price.multiply(BigDecimal.valueOf(3)).subtract(Money.of("1.50")).add(Money.of("0.25"));
        assertEquals(Money.of("3750.25"), total);
        assertTrue(price.compareTo(Money.of("1000")) > 0);
        assertEquals(-1, Money.of("-5").signum());
    }

    @Test
    void theShopBuysAndSellsWholeCentavosSoRoundedResultsStayAtScaleTwo() {
        assertEquals(2, PriceCalculator.lineTotal(Money.of("24.00"), 5).scale());
        assertEquals(2, PriceCalculator.vatIncluded(Money.of("100.00")).scale());
        assertEquals(2, new PercentDiscount(15).discountFor(Money.of("19.99")).scale());
    }
}
