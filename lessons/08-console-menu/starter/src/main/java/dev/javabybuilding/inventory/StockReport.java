package dev.javabybuilding.inventory;

public class StockReport {

    /** One header line, then one line per product. Line i uses names[i], quantities[i], reorderLevels[i]. */
    public static String build(String[] names, int[] quantities, int[] reorderLevels) {
        StringBuilder report = new StringBuilder(String.format("%-14s%5s  %s", "Product", "Qty", "Status"));
        for (int i = 0; i < names.length; i++) {
            String status = StockChecker.status(quantities[i], reorderLevels[i]);
            report.append("\n").append(String.format("%-14s%5d  %s", names[i], quantities[i], status));
        }
        return report.toString();
    }

    /** Value of everything on the shelves, in centavos: sum of unit price x quantity. */
    public static int totalValue(int[] unitPrices, int[] quantities) {
        int total = 0;
        for (int i = 0; i < unitPrices.length; i++) {
            total += PriceCalculator.lineTotal(unitPrices[i], quantities[i]);
        }
        return total;
    }
}
