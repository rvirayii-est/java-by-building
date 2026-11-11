package dev.javabybuilding.inventory;

import java.util.Locale;

/** All money is stored as whole centavos (int) so 125.50 pesos is 12550. */
public class PriceCalculator {

    /** TODO 1: return unit price x quantity. */
    public static int lineTotal(int unitPriceCentavos, int quantity) {
        return 0;
    }

    /**
     * TODO 2: the 12% VAT already inside a VAT-inclusive total, rounded to the nearest centavo.
     * Hint: vat = total x 12 / 112. Add 56 before dividing to round instead of cut.
     */
    public static int vatIncluded(int totalCentavos) {
        return 0;
    }

    /**
     * TODO 3: turn 123450 into "PHP 1,234.50".
     * Hint: pesos = centavos / 100, centavos left over = centavos % 100,
     * then String.format(Locale.US, "PHP %,d.%02d", pesos, left).
     */
    public static String format(int centavos) {
        return "";
    }
}
