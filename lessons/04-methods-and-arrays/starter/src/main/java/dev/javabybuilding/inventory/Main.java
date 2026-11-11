package dev.javabybuilding.inventory;

public class Main {

    public static String banner() {
        return "=== Inventory & Sales System ===";
    }

    /** Prints the stock report and the total value. One method, used twice from main. */
    static void printReport(String[] names, int[] unitPrices, int[] quantities, int[] reorderLevels) {
        System.out.println(StockReport.build(names, quantities, reorderLevels));
        System.out.println();
        System.out.println("Total stock value: "
                + PriceCalculator.format(StockReport.totalValue(unitPrices, quantities)));
    }

    public static void main(String[] args) {
        System.out.println(banner());
        System.out.println("Version 0.4 - Methods and arrays");
        System.out.println();

        String[] names = {"Rice 25kg", "Cooking Oil 1L", "Sugar 1kg", "Sardines 155g"};
        int[] unitPrices = {125050, 9800, 7500, 2400};
        int[] quantities = {12, 4, 0, 60};
        int[] reorderLevels = {10, 10, 10, 20};

        printReport(names, unitPrices, quantities, reorderLevels);
        System.out.println();

        int sugar = StockUpdater.indexOf(names, "Sugar 1kg");
        StockUpdater.receive(quantities, sugar, 24);
        System.out.println("Received 24 x Sugar 1kg");

        int rice = StockUpdater.indexOf(names, "Rice 25kg");
        if (StockUpdater.sell(quantities, rice, 3)) {
            System.out.println("Sold 3 x Rice 25kg");
        } else {
            System.out.println("Not enough Rice 25kg in stock");
        }
        System.out.println();

        printReport(names, unitPrices, quantities, reorderLevels);
    }
}
