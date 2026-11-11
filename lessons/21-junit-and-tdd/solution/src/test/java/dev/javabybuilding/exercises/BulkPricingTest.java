package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * YOUR tests, written FIRST (reference version). One test per rule, boundaries included: the bugs hide at 9/10, 49/50, 99/100.
 */
class BulkPricingTest {

    @ParameterizedTest(name = "{0} units -> {1}% off")
    @CsvSource({"1, 0", "9, 0", "10, 5", "49, 5", "50, 10", "99, 10", "100, 15", "5000, 15"})
    @DisplayName("the discount grows at 10, 50 and 100 units")
    void discountSteps(int quantity, int expectedPercent) {
        assertEquals(expectedPercent, BulkPricing.discountPercent(quantity));
    }

    @ParameterizedTest(name = "quantity {0} is refused")
    @ValueSource(ints = {0, -1, -100})
    @DisplayName("a quantity below 1 is a mistake")
    void tooSmallQuantityIsRefused(int quantity) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> BulkPricing.discountPercent(quantity));
        assertEquals("Quantity must be at least 1.", e.getMessage());
    }

    @Test
    @DisplayName("no discount below 10: the line total is price x quantity")
    void noDiscountBelowTen() {
        assertEquals(new BigDecimal("216.00"), BulkPricing.lineTotal(new BigDecimal("24.00"), 9));
    }

    @Test
    @DisplayName("ten units get 5% off")
    void fivePercentAtTen() {
        assertEquals(new BigDecimal("228.00"), BulkPricing.lineTotal(new BigDecimal("24.00"), 10)); // 240.00 less 12.00
    }

    @Test
    @DisplayName("the total is rounded half up to the centavo")
    void roundsHalfUp() {
        // 10 x 0.15 = 1.50, less 5% = 1.425 -> 1.43
        assertEquals(new BigDecimal("1.43"), BulkPricing.lineTotal(new BigDecimal("0.15"), 10));
    }

    @Test
    @DisplayName("lineTotal refuses a quantity below 1")
    void lineTotalRefusesZero() {
        assertThrows(IllegalArgumentException.class, () -> BulkPricing.lineTotal(new BigDecimal("24.00"), 0));
    }
}
