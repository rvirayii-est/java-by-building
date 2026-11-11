package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * The course's own check of the specification. It is NOT a substitute for your tests: it only tells you when the
 * code is correct. Your tests (BulkPricingTest) are what you write first and what guards the code later.
 */
class BulkPricingSpecTest {

    @Test
    void discountBoundaries() {
        assertEquals(0, BulkPricing.discountPercent(9));
        assertEquals(5, BulkPricing.discountPercent(10));
        assertEquals(5, BulkPricing.discountPercent(49));
        assertEquals(10, BulkPricing.discountPercent(50));
        assertEquals(10, BulkPricing.discountPercent(99));
        assertEquals(15, BulkPricing.discountPercent(100));
    }

    @Test
    void smallestQuantityHasNoDiscountAndZeroOrLessIsRefused() {
        assertEquals(0, BulkPricing.discountPercent(1));
        assertThrows(IllegalArgumentException.class, () -> BulkPricing.discountPercent(0));
        assertThrows(IllegalArgumentException.class, () -> BulkPricing.discountPercent(-5));
    }

    @Test
    void lineTotalAppliesTheDiscount() {
        assertEquals(new BigDecimal("216.00"), BulkPricing.lineTotal(new BigDecimal("24.00"), 9));
        assertEquals(new BigDecimal("228.00"), BulkPricing.lineTotal(new BigDecimal("24.00"), 10));
        assertEquals(new BigDecimal("1080.00"), BulkPricing.lineTotal(new BigDecimal("24.00"), 50)); // 1200.00 less 10%
        assertEquals(new BigDecimal("2040.00"), BulkPricing.lineTotal(new BigDecimal("24.00"), 100)); // 2400.00 less 15%
    }

    @Test
    void lineTotalRoundsHalfUpToTwoDecimals() {
        assertEquals(new BigDecimal("1.43"), BulkPricing.lineTotal(new BigDecimal("0.15"), 10)); // 1.425
        assertEquals(new BigDecimal("0.15"), BulkPricing.lineTotal(new BigDecimal("0.15"), 1));
    }

    @Test
    void lineTotalRefusesZeroUnits() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> BulkPricing.lineTotal(new BigDecimal("24.00"), 0));
        assertEquals("Quantity must be at least 1.", e.getMessage());
    }
}
