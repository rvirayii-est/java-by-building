package dev.javabybuilding.inventory;

import java.math.BigDecimal;

/** Checks that typed text really is a number before we use it. */
public class Validation {

    /** True when every character of text is a digit 0-9 and there is at least one. */
    private static boolean allDigits(String text) {
        if (text.isEmpty()) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /** True for 1 to 9 digits (spaces at the ends are ignored). Negative numbers and decimals are not accepted. */
    public static boolean isWholeNumber(String text) {
        String t = text.trim();
        return t.length() <= 9 && allDigits(t);
    }

    /**
     * Turns pesos typed as text into money: "125.50" gives 125.50, "125" gives 125.00, "0.05" gives 0.05.
     * Returns null when the text is not a valid amount (letters, minus sign, more than 2 decimals, nothing after the dot).
     * null, not -1: -1 is a real amount that a BigDecimal can hold, while null clearly means "no amount".
     */
    public static BigDecimal parseMoney(String text) {
        String t = text.trim();
        String pesos = t;
        String centavos = "00";
        int dot = t.indexOf('.');
        if (dot >= 0) {
            pesos = t.substring(0, dot);
            centavos = t.substring(dot + 1);
            if (centavos.length() == 1) {
                centavos = centavos + "0";
            }
        }
        if (pesos.length() > 9 || !allDigits(pesos) || centavos.length() != 2 || !allDigits(centavos)) {
            return null;
        }
        // TODO 6: the text is already checked, so build the money from it: return Money.of(pesos + "." + centavos);
        return null;
    }
}
