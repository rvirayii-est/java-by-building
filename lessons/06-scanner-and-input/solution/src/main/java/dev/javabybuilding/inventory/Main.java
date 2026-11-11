package dev.javabybuilding.inventory;

import java.io.PrintStream;
import java.util.Scanner;

public class Main {

    public static String banner() {
        return "=== Inventory & Sales System ===";
    }

    /** One sale line, asked question by question. Reads from in, writes to out (so tests need no keyboard). */
    public static void run(Scanner in, PrintStream out) {
        out.println(banner());
        out.println("Version 0.6 - Scanner and input");
        out.println();

        String name = InputReader.readText(in, out, "Product name: ");
        int quantity = InputReader.readInt(in, out, "Quantity (1-100): ", 1, 100);
        int unitPrice = InputReader.readMoney(in, out, "Unit price in PHP: ", 1_000_000);

        int total = PriceCalculator.lineTotal(unitPrice, quantity);
        out.println();
        out.println("Sale line: " + TextUtil.titleCase(name) + " x " + quantity);
        out.println("Total: " + PriceCalculator.format(total));
        out.println("VAT included: " + PriceCalculator.format(PriceCalculator.vatIncluded(total)));
    }

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }
}
