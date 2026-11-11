package dev.javabybuilding.inventory;

public class Main {

    public static String banner() {
        return "=== Inventory & Sales System ===";
    }

    public static void main(String[] args) {
        System.out.println(banner());
        System.out.println("Version 0.2 - Prices in centavos");

        String product = "Rice 25kg";
        int unitPrice = 125050; // PHP 1,250.50
        int quantity = 2;

        int total = PriceCalculator.lineTotal(unitPrice, quantity);
        System.out.println(product + " x " + quantity + " = " + PriceCalculator.format(total));
        System.out.println("VAT included: " + PriceCalculator.format(PriceCalculator.vatIncluded(total)));
    }
}
