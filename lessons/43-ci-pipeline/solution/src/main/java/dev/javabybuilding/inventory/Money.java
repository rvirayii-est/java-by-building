package dev.javabybuilding.inventory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Helpers for money. Money is a BigDecimal in PESOS with exactly 2 decimal places: 1250.50 is new BigDecimal("1250.50").
 * Why not double? A double stores 0.1 as a long binary fraction that is slightly off, so 0.1 + 0.2 is 0.30000000000000004.
 * Why not int centavos any more? It worked, but every method had to remember "this int is centavos", the formatting
 * code was full of /100 and %100, and an int overflows near PHP 21 million. BigDecimal is exact and knows about decimals.
 *
 * The three rules of BigDecimal that this course follows:
 * 1. Build it from TEXT: new BigDecimal("0.10"), never new BigDecimal(0.10) (that copies the double's error).
 * 2. Compare with compareTo, not equals: equals says 2.0 and 2.00 are different because their scales differ.
 *    We avoid the problem by always keeping scale 2 (round every result), so assertEquals works in our tests.
 * 3. Say how to round when dividing, or Java throws ArithmeticException for 1 / 3.
 */
public final class Money {

    public static final BigDecimal ZERO = new BigDecimal("0.00");

    private Money() {} // only static helpers: nobody makes a Money object

    /** "1250.50" gives 1250.50; "35" gives 35.00. The text must be a number with at most 2 decimals. */
    public static BigDecimal of(String text) {
        return new BigDecimal(text.trim()).setScale(2, RoundingMode.UNNECESSARY); // throws if it would have to drop digits
    }

    /** A whole number of pesos: of(35) gives 35.00. */
    public static BigDecimal of(long pesos) {
        return BigDecimal.valueOf(pesos).setScale(2);
    }

    /** Rounds any result to exactly 2 decimals, half up (0.125 becomes 0.13), the usual rule for money. */
    public static BigDecimal round(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
