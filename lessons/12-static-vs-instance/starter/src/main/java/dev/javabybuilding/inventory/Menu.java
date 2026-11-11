package dev.javabybuilding.inventory;

import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** The interactive menu. Everything reads from the Scanner and writes to the PrintStream it is given. */
public class Menu {

    /** The five choices, one per line. */
    public static String menuText() {
        return "--- Menu ---\n"
                + "1. Add product\n"
                + "2. Receive stock\n"
                + "3. Record sale\n"
                + "4. Stock report\n"
                + "5. Quit";
    }

    /** Shows the menu again and again until the person chooses Quit (or the input runs out). */
    public static void run(Scanner in, PrintStream out, Inventory inventory) {
        try {
            while (true) {
                out.println();
                out.println(menuText());
                int choice = InputReader.readInt(in, out, "Choose 1-5: ", 1, 5);
                if (choice == 1) {
                    addProduct(in, out, inventory);
                } else if (choice == 2) {
                    receiveStock(in, out, inventory);
                } else if (choice == 3) {
                    recordSale(in, out, inventory);
                } else if (choice == 4) {
                    showReport(out, inventory);
                } else {
                    out.println("Goodbye.");
                    return;
                }
            }
        } catch (NoSuchElementException e) {
            // Scanner has no more input (for example the person pressed Ctrl+D). Stop politely.
            out.println();
            out.println("Input ended. Goodbye.");
        }
    }

    static void addProduct(Scanner in, PrintStream out, Inventory inventory) {
        String name = InputReader.readText(in, out, "Product name: ");
        String existing = inventory.find(name);
        if (existing != null) {
            out.println("That product already exists: " + existing);
            return;
        }
        int price = InputReader.readMoney(in, out, "Unit price in PHP: ", Product.MAX_PRICE);
        int reorderLevel = InputReader.readInt(in, out, "Reorder level (0-1000): ", 0, 1000);
        inventory.addProduct(name, price, reorderLevel);
        out.println("Added " + name + " at " + PriceCalculator.format(price) + ".");
    }

    static void receiveStock(Scanner in, PrintStream out, Inventory inventory) {
        String name = InputReader.readText(in, out, "Product name: ");
        String stored = inventory.find(name);
        if (stored == null) {
            out.println("Unknown product: " + name);
            return;
        }
        int amount = InputReader.readInt(in, out, "Quantity received (1-10000): ", 1, 10000);
        inventory.receive(stored, amount);
        out.println("Received " + amount + " x " + stored + ". In stock now: " + inventory.quantityOf(stored));
    }

    static void recordSale(Scanner in, PrintStream out, Inventory inventory) {
        String name = InputReader.readText(in, out, "Product name: ");
        String stored = inventory.find(name);
        if (stored == null) {
            out.println("Unknown product: " + name);
            return;
        }
        int amount = InputReader.readInt(in, out, "Quantity sold (1-100): ", 1, 100);
        int inStock = inventory.quantityOf(stored);
        if (amount > inStock) {
            out.println("Not enough stock: only " + inStock + " of " + stored + " left.");
            return;
        }
        int total = inventory.sell(stored, amount);
        out.println("Sold " + amount + " x " + stored + " = " + PriceCalculator.format(total));
        out.println("VAT included: " + PriceCalculator.format(PriceCalculator.vatIncluded(total)));
    }

    static void showReport(PrintStream out, Inventory inventory) {
        out.println(inventory.report());
        out.println();
        out.println("Total stock value: " + PriceCalculator.format(inventory.totalValue()));
        out.println("Sales so far: " + PriceCalculator.format(inventory.salesTotal()));
    }
}
