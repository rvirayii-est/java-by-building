package dev.javabybuilding.inventory;

import java.util.Locale;

/** All money is stored as whole centavos (int) so 125.50 pesos is 12550. */
public class PriceCalculator {

    /** Price of one product line: unit price x quantity. */
    public static int lineTotal(int unitPriceCentavos, int quantity) {
        return unitPriceCentavos * quantity;
    }

    /** The 12% VAT that is already inside a VAT-inclusive total, rounded to the nearest centavo. */
    public static int vatIncluded(int totalCentavos) {
        return (totalCentavos * 12 + 56) / 112;
    }

    /** Turns 123450 into "PHP 1,234.50". */
    public static String format(int centavos) {
        return String.format(Locale.US, "PHP %,d.%02d", centavos / 100, centavos % 100);
    }
}
