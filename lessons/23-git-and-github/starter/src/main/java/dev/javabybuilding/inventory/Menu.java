package dev.javabybuilding.inventory;

import java.io.PrintStream;
import java.math.BigDecimal;
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
        String existing = inventory.find(name); // asked early so we do not make the person type a price for nothing
        if (existing != null) {
            out.println("That product already exists: " + existing);
            return;
        }
        BigDecimal price = InputReader.readMoney(in, out, "Unit price in PHP: ", Product.MAX_PRICE);
        int reorderLevel = InputReader.readInt(in, out, "Reorder level (0-1000): ", 0, 1000);
        try {
            inventory.addProduct(name, price, reorderLevel);
            out.println("Added " + name + " at " + PriceCalculator.format(price) + ".");
        } catch (ShopException e) {
            out.println(e.getMessage());
        }
    }

    static void receiveStock(Scanner in, PrintStream out, Inventory inventory) {
        String name = InputReader.readText(in, out, "Product name: ");
        try {
            Product product = inventory.getProduct(name); // throws UnknownProductException, so we never ask for a quantity
            int amount = InputReader.readInt(in, out, "Quantity received (1-10000): ", 1, 10000);
            inventory.receive(product.getName(), amount);
            out.println("Received " + amount + " x " + product.getName() + ". In stock now: " + product.getQuantity());
        } catch (ShopException e) {
            out.println(e.getMessage()); // for example "Unknown product: salt"
        }
    }

    static void recordSale(Scanner in, PrintStream out, Inventory inventory) {
        String name = InputReader.readText(in, out, "Product name: ");
        try {
            Product product = inventory.getProduct(name);
            int amount = InputReader.readInt(in, out, "Quantity sold (1-100): ", 1, 100);
            BigDecimal total = inventory.sell(product.getName(), amount); // may throw InsufficientStockException
            out.println("Sold " + amount + " x " + product.getName() + " = " + PriceCalculator.format(total));
            out.println("VAT included: " + PriceCalculator.format(PriceCalculator.vatIncluded(total)));
        } catch (ShopException e) {
            out.println(e.getMessage()); // for example "Not enough stock: only 12 of Rice 25kg left."
        }
    }

    static void showReport(PrintStream out, Inventory inventory) {
        out.println(inventory.report());
        out.println();
        out.println("Total stock value: " + PriceCalculator.format(inventory.totalValue()));
        out.println("Sales so far: " + PriceCalculator.format(inventory.salesTotal()));
        // TODO (do this on a branch: see the lesson, part 4). After "Sales so far", print a blank line, the heading
        // "To reorder:" and then  ReorderAdvisor.report(inventory.reorderSuggestions())  on its own line(s).
    }
}
