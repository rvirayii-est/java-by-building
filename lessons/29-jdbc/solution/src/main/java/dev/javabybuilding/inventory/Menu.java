package dev.javabybuilding.inventory;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final Logger LOG = Logger.getLogger(Menu.class.getName());

    // Lesson 24 (clean code): the numbers 1 to 5 had no names, so the reader had to look at menuText() to know what "3" means.
    private static final int ADD_PRODUCT = 1;
    private static final int RECEIVE_STOCK = 2;
    private static final int RECORD_SALE = 3;
    private static final int STOCK_REPORT = 4;
    private static final int QUIT = 5;

    /** Shows the menu again and again until the person chooses Quit (or the input runs out). */
    public static void run(Scanner in, PrintStream out, Inventory inventory) {
        try {
            boolean running = true;
            while (running) {
                out.println();
                out.println(menuText());
                int choice = InputReader.readInt(in, out, "Choose 1-5: ", ADD_PRODUCT, QUIT);
                running = handle(choice, in, out, inventory);
            }
        } catch (NoSuchElementException e) {
            // Scanner has no more input (for example the person pressed Ctrl+D). Stop politely.
            out.println();
            out.println("Input ended. Goodbye.");
        }
    }

    /** Does what the choice says. Returns false when the person chose Quit, true to show the menu again. */
    private static boolean handle(int choice, Scanner in, PrintStream out, Inventory inventory) {
        switch (choice) {
            case ADD_PRODUCT -> addProduct(in, out, inventory);
            case RECEIVE_STOCK -> receiveStock(in, out, inventory);
            case RECORD_SALE -> recordSale(in, out, inventory);
            case STOCK_REPORT -> showReport(out, inventory);
            case QUIT -> {
                out.println("Goodbye.");
                return false;
            }
            default -> throw new IllegalStateException("readInt allows only 1 to 5, but got " + choice);
        }
        return true;
    }

    /** Tells the person what went wrong, and keeps a WARNING in the log for whoever looks after the shop. */
    private static void refuse(PrintStream out, ShopException problem) {
        out.println(problem.getMessage());
        LOG.log(Level.WARNING, "Refused: {0}", problem.getMessage());
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
            refuse(out, e);
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
            refuse(out, e); // for example "Unknown product: salt"
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
            refuse(out, e); // for example "Not enough stock: only 12 of Rice 25kg left."
        }
    }

    static void showReport(PrintStream out, Inventory inventory) {
        out.println(inventory.report());
        out.println();
        out.println("Total stock value: " + PriceCalculator.format(inventory.totalValue()));
        out.println("Sales so far: " + PriceCalculator.format(inventory.salesTotal()));
        out.println();
        out.println("To reorder:");
        out.println(ReorderAdvisor.report(inventory.reorderSuggestions()));
    }
}
