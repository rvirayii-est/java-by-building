package dev.javabybuilding.inventory;

/** Checks that typed text really is a number before we use it. */
public class Validation {

    /** Helper (done for you): true when every character of text is a digit 0-9 and there is at least one. */
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

    /**
     * TODO 1: true for 1 to 9 digits (spaces at the ends are ignored). "42" and " 7 " are fine;
     * "", "abc", "-5", "4.5" and "1234567890" are not. Use text.trim(), length() and allDigits.
     */
    public static boolean isWholeNumber(String text) {
        return false;
    }

    /**
     * TODO 2: turn pesos typed as text into centavos: "125.50" gives 12550, "125" gives 12500, "125.5" gives 12550,
     * "0.05" gives 5. Return -1 for anything that is not a valid amount: "abc", "-5", "", "1.234", "125.", ".5".
     * Hint: trim, find the dot with indexOf('.'), split with substring, check each part with allDigits
     * (pesos at most 7 digits, centavos exactly 2 after padding "5" to "50"), then Integer.parseInt.
     */
    public static int parseCentavos(String text) {
        return -1;
    }
}
