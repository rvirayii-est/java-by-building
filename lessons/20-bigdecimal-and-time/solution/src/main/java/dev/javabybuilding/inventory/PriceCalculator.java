package dev.javabybuilding.inventory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/** All money is a BigDecimal in pesos with 2 decimal places (see Money). Lessons 02-19 used int centavos. */
public class PriceCalculator {

    private static final BigDecimal VAT_RATE = new BigDecimal("12");
    private static final BigDecimal VAT_INCLUSIVE_DIVISOR = new BigDecimal("112"); // 100% + 12%

    /** Price of one product line: unit price x quantity. */
    public static BigDecimal lineTotal(BigDecimal unitPrice, int quantity) {
        return Money.round(unitPrice.multiply(BigDecimal.valueOf(quantity)));
    }

    /** The 12% VAT that is already inside a VAT-inclusive total, rounded to the nearest centavo. */
    public static BigDecimal vatIncluded(BigDecimal total) {
        return total.multiply(VAT_RATE).divide(VAT_INCLUSIVE_DIVISOR, 2, RoundingMode.HALF_UP); // divide says HOW to round
    }

    /** Turns 1234.50 into "PHP 1,234.50". */
    public static String format(BigDecimal amount) {
        return String.format(Locale.US, "PHP %,.2f", amount);
    }
}
