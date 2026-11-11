package dev.javabybuilding.inventory;

public class Main {

    public static String banner() {
        return "=== Inventory & Sales System ===";
    }

    /** Prints the stock report and the total value. */
    static void printReport(String[] names, int[] unitPrices, int[] quantities, int[] reorderLevels) {
        System.out.println(StockReport.build(names, quantities, reorderLevels));
        System.out.println();
        System.out.println("Total stock value: "
                + PriceCalculator.format(StockReport.totalValue(unitPrices, quantities)));
    }

    public static void main(String[] args) {
        System.out.println(banner());
        System.out.println("Version 0.5 - Strings and text");
        System.out.println();

        String[] names = {"Rice 25kg", "Cooking Oil 1L", "Sugar 1kg", "Sardines 155g"};
        int[] unitPrices = {125050, 9800, 7500, 2400};
        int[] quantities = {12, 4, 0, 60};
        int[] reorderLevels = {10, 10, 10, 20};

        printReport(names, unitPrices, quantities, reorderLevels);
        System.out.println();

        // Messy text, like a tired cashier might type it.
        String typed = "  sUGAr   1KG ";
        System.out.println("Typed: [" + typed + "]");
        System.out.println("Cleaned: [" + TextUtil.clean(typed) + "]");
        System.out.println("Title case: " + TextUtil.titleCase(typed));

        int found = StockUpdater.indexOf(names, typed);
        if (found >= 0) {
            System.out.println("Found at position " + found + ": " + names[found]);
        } else {
            System.out.println("Not found");
        }

        System.out.println("Products that match \"oil\":");
        for (String name : names) {
            if (TextUtil.matches(name, "oil")) {
                System.out.println("  " + name);
            }
        }
    }
}
