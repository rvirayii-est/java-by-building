package dev.javabybuilding.inventory;

import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** The interactive menu. Everything reads from the Scanner and writes to the PrintStream it is given. */
public class Menu {

    /** Largest unit price we accept: PHP 10,000.00. Keeps int centavo arithmetic safe. */
    static final int MAX_PRICE = 1_000_000;

    /** The five choices, one per line. (Done for you.) */
    public static String menuText() {
        return "--- Menu ---\n"
                + "1. Add product\n"
                + "2. Receive stock\n"
                + "3. Record sale\n"
                + "4. Stock report\n"
                + "5. Quit";
    }

    /**
     * TODO 1: show the menu again and again until the person chooses Quit.
     * Inside a try block, loop with while (true):
     *   out.println(); out.println(menuText());
     *   int choice = InputReader.readInt(in, out, "Choose 1-5: ", 1, 5);
     *   1 -> addProduct(in, out, inventory)       2 -> receiveStock(in, out, inventory)
     *   3 -> recordSale(in, out, inventory)       4 -> showReport(out, inventory)
     *   5 -> out.println("Goodbye."); and return.
     * After the try block add: catch (NoSuchElementException e) { out.println(); out.println("Input ended. Goodbye."); }
     * (Scanner throws that exception when the input runs out; the catch stops the program politely.)
     */
    public static void run(Scanner in, PrintStream out, Inventory inventory) {
    }

    /**
     * TODO 2: ask for the product name (InputReader.readText, prompt "Product name: ").
     * If inventory.find(name) is not null, print "That product already exists: " + the stored name and return.
     * Otherwise ask for the price (InputReader.readMoney, prompt "Unit price in PHP: ", MAX_PRICE) and the reorder
     * level (readInt, prompt "Reorder level (0-1000): ", 0 to 1000), call inventory.addProduct(...), and print
     * "Added " + name + " at " + PriceCalculator.format(price) + "."
     */
    static void addProduct(Scanner in, PrintStream out, Inventory inventory) {
    }

    /**
     * TODO 3: ask for the name ("Product name: "). Unknown product (find returns null): print
     * "Unknown product: " + name and return. Otherwise ask "Quantity received (1-10000): " (1 to 10000),
     * call inventory.receive, and print "Received 24 x Sugar 1kg. In stock now: 24"
     * (amount, stored name, then inventory.quantityOf(stored)).
     */
    static void receiveStock(Scanner in, PrintStream out, Inventory inventory) {
    }

    /**
     * TODO 4: ask for the name ("Product name: "). Unknown: print "Unknown product: " + name and return.
     * Ask "Quantity sold (1-100): " (1 to 100). If the amount is more than inventory.quantityOf(stored) print
     * "Not enough stock: only <n> of <stored name> left." and return. Otherwise total = inventory.sell(stored, amount) and print
     * "Sold 3 x Rice 25kg = PHP 3,751.50" (amount, stored name, PriceCalculator.format(total)) and on the next line
     * "VAT included: " + PriceCalculator.format(PriceCalculator.vatIncluded(total)).
     */
    static void recordSale(Scanner in, PrintStream out, Inventory inventory) {
    }

    /** The stock report, the total value and the sales so far. (Done for you.) */
    static void showReport(PrintStream out, Inventory inventory) {
        out.println(inventory.report());
        out.println();
        out.println("Total stock value: " + PriceCalculator.format(inventory.totalValue()));
        out.println("Sales so far: " + PriceCalculator.format(inventory.salesTotal()));
    }
}
