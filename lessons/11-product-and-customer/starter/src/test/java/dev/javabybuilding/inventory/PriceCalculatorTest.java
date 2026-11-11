package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PriceCalculatorTest {

    @Test
    void lineTotalMultipliesPriceByQuantity() {
        assertEquals(250100, PriceCalculator.lineTotal(125050, 2));
    }

    @Test
    void vatIsTwelveOverOneTwelveOfTheTotal() {
        assertEquals(1200, PriceCalculator.vatIncluded(11200));
    }

    @Test
    void vatRoundsToTheNearestCentavo() {
        assertEquals(268, PriceCalculator.vatIncluded(2500)); // 267.857...
    }

    @Test
    void formatShowsPesosWithCommasAndTwoDecimals() {
        assertEquals("PHP 1,234.50", PriceCalculator.format(123450));
        assertEquals("PHP 0.05", PriceCalculator.format(5));
    }
}
