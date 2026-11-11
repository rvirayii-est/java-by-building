package dev.javabybuilding.exercises;

import java.math.BigDecimal;

/**
 * Lesson 21, exercise: bulk pricing, written TEST-FIRST. Do NOT start by writing this class. Start in BulkPricingTest.
 *
 * The rules (the specification):
 * - quantity 1 to 9: no discount (0%)
 * - quantity 10 to 49: 5% off
 * - quantity 50 to 99: 10% off
 * - quantity 100 or more: 15% off
 * - quantity below 1 is a mistake: throw IllegalArgumentException("Quantity must be at least 1.")
 * lineTotal is unit price x quantity, less the discount, rounded half up to the centavo (2 decimals).
 *
 * The methods below are empty on purpose: that is the "red" you start from.
 */
public class BulkPricing {

    /** The percentage taken off when buying this many at once. */
    public static int discountPercent(int quantity) {
        throw new UnsupportedOperationException("TODO: write a failing test first, then make it pass");
    }

    /** What the customer pays for quantity units at unitPrice each, after the bulk discount. */
    public static BigDecimal lineTotal(BigDecimal unitPrice, int quantity) {
        throw new UnsupportedOperationException("TODO: write a failing test first, then make it pass");
    }
}
