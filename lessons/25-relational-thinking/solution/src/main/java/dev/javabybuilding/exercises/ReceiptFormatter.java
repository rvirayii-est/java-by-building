package dev.javabybuilding.exercises;

import java.math.BigDecimal;
import java.util.List;

/**
 * Lesson 24, exercise 2: clean code. One job per method, each with a name that says it, so the biggest method
 * (receipt) reads like a recipe. Compare with the one-giant-method version in examples/CleanCodeDemo.java.
 */
public class ReceiptFormatter {

    private static final int NAME_WIDTH = 14;
    private static final int QUANTITY_WIDTH = 4;
    private static final int PRICE_WIDTH = 9;
    private static final int TOTAL_WIDTH = 11;

    /** The text cut or padded with spaces on the RIGHT to exactly width characters: padRight("Rice", 6) is "Rice  ". */
    public static String padRight(String text, int width) {
        String cut = text.length() > width ? text.substring(0, width) : text;
        return cut + " ".repeat(width - cut.length());
    }

    /** The text padded with spaces on the LEFT to width characters (never cut): padLeft("7", 3) is "  7". */
    public static String padLeft(String text, int width) {
        return " ".repeat(Math.max(0, width - text.length())) + text;
    }

    /** An amount with thousands commas and two decimals: 1250.5 becomes "1,250.50". */
    public static String money(BigDecimal amount) {
        return String.format(java.util.Locale.US, "%,.2f", amount);
    }

    /** One receipt line: name (14 wide), quantity (4), " x ", unit price (9), line total (11). */
    public static String line(String name, int quantity, BigDecimal unitPrice) {
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return padRight(name, NAME_WIDTH)
                + padLeft(String.valueOf(quantity), QUANTITY_WIDTH)
                + " x "
                + padLeft(money(unitPrice), PRICE_WIDTH)
                + padLeft(money(lineTotal), TOTAL_WIDTH);
    }

    /** The whole receipt: the shop name, a rule of dashes as wide as a line, the lines, the rule again, then the total. */
    public static String receipt(String shopName, List<String> lines, BigDecimal total) {
        String rule = "-".repeat(NAME_WIDTH + QUANTITY_WIDTH + 3 + PRICE_WIDTH + TOTAL_WIDTH);
        String totalLine = padRight("TOTAL", rule.length() - TOTAL_WIDTH) + padLeft(money(total), TOTAL_WIDTH);
        return shopName + "\n" + rule + "\n" + String.join("\n", lines) + (lines.isEmpty() ? "" : "\n") + rule + "\n" + totalLine;
    }
}
