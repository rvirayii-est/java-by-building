package dev.javabybuilding.inventory;

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
     * Turns pesos typed as text into centavos: "125.50" gives 12550, "125" gives 12500, "0.05" gives 5.
     * Returns -1 when the text is not a valid amount (letters, minus sign, more than 2 decimals, nothing after the dot).
     */
    public static int parseCentavos(String text) {
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
        if (pesos.length() > 7 || !allDigits(pesos) || centavos.length() != 2 || !allDigits(centavos)) {
            return -1;
        }
        return Integer.parseInt(pesos) * 100 + Integer.parseInt(centavos);
    }
}
