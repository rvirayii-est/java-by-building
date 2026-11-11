package dev.javabybuilding.inventory;

public class StockReport {

    /**
     * TODO 2: build the report text. First line is the header, then one line per product,
     * lines separated by a newline (no newline at the very end).
     * Header: String.format("%-14s%5s  %s", "Product", "Qty", "Status")
     * Row i : String.format("%-14s%5d  %s", names[i], quantities[i], StockChecker.status(...))
     * Hint: loop with for (int i = 0; i < names.length; i++) and append to a StringBuilder.
     */
    public static String build(String[] names, int[] quantities, int[] reorderLevels) {
        return "";
    }

    /** TODO 3: sum of unit price x quantity for every product (use PriceCalculator.lineTotal). */
    public static int totalValue(int[] unitPrices, int[] quantities) {
        return 0;
    }
}
