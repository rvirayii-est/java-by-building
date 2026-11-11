package dev.javabybuilding.inventory;

import java.io.PrintStream;

public class Main {

    public static String banner() {
        return "=== Inventory & Sales System ===";
    }

    /** Shows the Inventory doing its job: report, add, receive, sell. Writes to out so tests can read it. */
    public static void run(PrintStream out) {
        out.println(banner());
        out.println("Version 0.7 - ArrayList and HashMap");
        out.println();

        Inventory inventory = Inventory.sample();
        out.println(inventory.report());
        out.println();
        out.println("Total stock value: " + PriceCalculator.format(inventory.totalValue()));
        out.println();

        if (inventory.addProduct("Salt 500g", 3500, 15)) {
            out.println("Added Salt 500g");
        }
        if (!inventory.addProduct("salt  500G", 3500, 15)) {
            out.println("Salt 500g already exists");
        }
        inventory.receive("  salt 500g", 30);
        out.println("Received 30 x Salt 500g");

        int total = inventory.sell("rice 25kg", 3);
        if (total >= 0) {
            out.println("Sold 3 x Rice 25kg = " + PriceCalculator.format(total));
        }
        if (inventory.sell("Rice 25kg", 500) < 0) {
            out.println("Could not sell 500 x Rice 25kg (not enough stock)");
        }
        out.println();

        out.println(inventory.report());
        out.println();
        out.println("Total stock value: " + PriceCalculator.format(inventory.totalValue()));
        out.println("Sales so far: " + PriceCalculator.format(inventory.salesTotal()));
    }

    public static void main(String[] args) {
        run(System.out);
    }
}
