package dev.javabybuilding.exercises;

import java.math.BigDecimal;
import java.util.List;

/**
 * Lesson 24, exercise 2: clean code. One job per method, each with a name that says it, so the biggest method
 * (receipt) reads like a recipe. Compare with the one-giant-method version in examples/CleanCodeDemo.java.
 * The widths are named constants, not mystery numbers inside the code.
 */
public class ReceiptFormatter {

    private static final int NAME_WIDTH = 14;
    private static final int QUANTITY_WIDTH = 4;
    private static final int PRICE_WIDTH = 9;
    private static final int TOTAL_WIDTH = 11;

    /**
     * TODO: the text cut or padded with spaces on the RIGHT to exactly width characters: padRight("Rice", 6) is "Rice  ",
     * padRight("Cooking Oil", 5) is "Cooki". Hint: " ".repeat(n) makes n spaces.
     */
    public static String padRight(String text, int width) {
        return null;
    }

    /** TODO: the text padded with spaces on the LEFT to width characters, never cut: padLeft("7", 3) is "  7". */
    public static String padLeft(String text, int width) {
        return null;
    }

    /** TODO: an amount with thousands commas and two decimals: 1250.5 becomes "1,250.50". Hint: String.format(Locale.US, "%,.2f", amount). */
    public static String money(BigDecimal amount) {
        return null;
    }

    /**
     * TODO: one receipt line, built ONLY from the small methods above:
     * padRight(name, NAME_WIDTH) + padLeft(quantity, QUANTITY_WIDTH) + " x " + padLeft(money(unit price), PRICE_WIDTH)
     * + padLeft(money(line total), TOTAL_WIDTH). The line total is unit price x quantity.
     */
    public static String line(String name, int quantity, BigDecimal unitPrice) {
        return null;
    }

    /**
     * TODO: the whole receipt, as these lines joined with "\n" (see the test for the exact text): the shop name, a rule of
     * dashes as wide as a line, the item lines, the rule again, then a TOTAL line: "TOTAL" padded on the right so that the
     * money (padLeft, TOTAL_WIDTH) ends in the last column. With no item lines there are no blank lines in between.
     */
    public static String receipt(String shopName, List<String> lines, BigDecimal total) {
        return null;
    }
}
