package dev.javabybuilding.exercises;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Lesson 21, exercise: bulk pricing, written test-first.
 *
 * The rules (the specification):
 * - quantity 1 to 9: no discount (0%)
 * - quantity 10 to 49: 5% off
 * - quantity 50 to 99: 10% off
 * - quantity 100 or more: 15% off
 * - quantity below 1 is a mistake: throw IllegalArgumentException("Quantity must be at least 1.")
 * lineTotal is unit price x quantity, less the discount, rounded half up to the centavo (2 decimals).
 */
public class BulkPricing {

    /** The percentage taken off when buying this many at once. */
    public static int discountPercent(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
        if (quantity >= 100) {
            return 15;
        }
        if (quantity >= 50) {
            return 10;
        }
        if (quantity >= 10) {
            return 5;
        }
        return 0;
    }

    /** What the customer pays for quantity units at unitPrice each, after the bulk discount. */
    public static BigDecimal lineTotal(BigDecimal unitPrice, int quantity) {
        BigDecimal full = unitPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal keep = BigDecimal.valueOf(100 - discountPercent(quantity)); // the percentage the customer still pays
        return full.multiply(keep).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
