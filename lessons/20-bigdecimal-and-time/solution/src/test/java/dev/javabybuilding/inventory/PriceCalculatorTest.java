package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PriceCalculatorTest {

    @Test
    void lineTotalMultipliesPriceByQuantity() {
        assertEquals(Money.of("2501.00"), PriceCalculator.lineTotal(Money.of("1250.50"), 2));
    }

    @Test
    void vatIsTwelveOverOneTwelveOfTheTotal() {
        assertEquals(Money.of("12.00"), PriceCalculator.vatIncluded(Money.of("112.00")));
    }

    @Test
    void vatRoundsToTheNearestCentavo() {
        assertEquals(Money.of("2.68"), PriceCalculator.vatIncluded(Money.of("25.00"))); // 2.67857...
    }

    @Test
    void formatShowsPesosWithCommasAndTwoDecimals() {
        assertEquals("PHP 1,234.50", PriceCalculator.format(Money.of("1234.50")));
        assertEquals("PHP 0.05", PriceCalculator.format(Money.of("0.05")));
        assertEquals("PHP 35.00", PriceCalculator.format(Money.of("35")));
    }
}
