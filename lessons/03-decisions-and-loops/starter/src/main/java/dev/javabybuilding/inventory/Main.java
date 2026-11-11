package dev.javabybuilding.inventory;

public class Main {

    public static String banner() {
        return "=== Inventory & Sales System ===";
    }

    public static void main(String[] args) {
        System.out.println(banner());
        System.out.println("Version 0.3 - Stock report");
        System.out.println();

        String[] names = {"Rice 25kg", "Cooking Oil 1L", "Sugar 1kg", "Sardines 155g"};
        int[] unitPrices = {125050, 9800, 7500, 2400};
        int[] quantities = {12, 4, 0, 60};
        int[] reorderLevels = {10, 10, 10, 20};

        System.out.println(StockReport.build(names, quantities, reorderLevels));
        System.out.println();
        System.out.println("Total stock value: "
                + PriceCalculator.format(StockReport.totalValue(unitPrices, quantities)));
    }
}
