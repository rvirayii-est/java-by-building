package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import org.junit.jupiter.api.Test;

class MenuTest {

    private final Inventory inventory = Inventory.sample();

    /** Runs the menu with the typed lines as input and returns everything it printed. */
    private String session(String... lines) {
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        Menu.run(new Scanner(String.join("\n", lines) + "\n"), new PrintStream(captured), inventory);
        return captured.toString();
    }

    @Test
    void menuListsTheFiveChoices() {
        String output = session("5");
        assertTrue(output.contains("1. Add product"));
        assertTrue(output.contains("2. Receive stock"));
        assertTrue(output.contains("3. Record sale"));
        assertTrue(output.contains("4. Stock report"));
        assertTrue(output.contains("5. Quit"));
    }

    @Test
    void quitSaysGoodbye() {
        assertTrue(session("5").contains("Goodbye."));
    }

    @Test
    void anInvalidChoiceAsksAgain() {
        String output = session("9", "abc", "5");
        assertTrue(output.contains("Please type a number from 1 to 5."));
        assertTrue(output.contains("Please type a whole number."));
        assertTrue(output.contains("Goodbye."));
    }

    @Test
    void addProductStoresItWithTheTypedPriceAndReorderLevel() {
        String output = session("1", "salt   500g", "35", "15", "5");
        assertNotNull(inventory.find("Salt 500g"));
        assertEquals(3500, inventory.priceOf("Salt 500g"));
        assertEquals(0, inventory.quantityOf("Salt 500g"));
        assertTrue(output.contains("Added salt 500g at PHP 35.00."));
        assertEquals(5, inventory.size());
    }

    @Test
    void addingAnExistingProductIsRefused() {
        String output = session("1", "sugar 1KG", "5");
        assertTrue(output.contains("That product already exists: Sugar 1kg"));
        assertEquals(4, inventory.size());
    }

    @Test
    void receiveStockAddsToTheShelf() {
        String output = session("2", "sugar 1kg", "24", "5");
        assertEquals(24, inventory.quantityOf("Sugar 1kg"));
        assertTrue(output.contains("Received 24 x Sugar 1kg. In stock now: 24"));
    }

    @Test
    void receiveStockForAnUnknownProductIsRefused() {
        String output = session("2", "salt", "5");
        assertTrue(output.contains("Unknown product: salt"));
        assertEquals(4, inventory.size());
    }

    @Test
    void recordSaleShowsTheTotalAndVat() {
        String output = session("3", "rice 25kg", "3", "5");
        assertEquals(9, inventory.quantityOf("Rice 25kg"));
        assertEquals(375150, inventory.salesTotal());
        assertTrue(output.contains("Sold 3 x Rice 25kg = PHP 3,751.50"));
        assertTrue(output.contains("VAT included: PHP 401.95")); // 3751.50 x 12 / 112 = 401.946...
    }

    @Test
    void recordSaleRefusesWhenThereIsNotEnoughStock() {
        String output = session("3", "rice 25kg", "50", "5");
        assertTrue(output.contains("Not enough stock: only 12 of Rice 25kg left."));
        assertEquals(12, inventory.quantityOf("Rice 25kg"));
        assertEquals(0, inventory.salesTotal());
    }

    @Test
    void recordSaleForAnUnknownProductIsRefused() {
        assertTrue(session("3", "salt", "5").contains("Unknown product: salt"));
    }

    @Test
    void stockReportShowsTheTableTotalValueAndSales() {
        String output = session("4", "5");
        assertTrue(output.contains("Product         Qty  Status"));
        assertTrue(output.contains("Total stock value: PHP 16,838.00"));
        assertTrue(output.contains("Sales so far: PHP 0.00"));
    }

    @Test
    void aFullSessionAddReceiveSellReportQuit() {
        String output = session(
                "1", "Salt 500g", "35", "15",
                "2", "salt 500g", "30",
                "3", "salt 500g", "10",
                "4",
                "5");
        assertTrue(output.contains(String.format("%-14s%5d  %s", "Salt 500g", 20, "OK")));
        assertTrue(output.contains("Sales so far: PHP 350.00"));
        assertTrue(output.endsWith("Goodbye.\n") || output.endsWith("Goodbye.\r\n"));
    }

    @Test
    void whenTheInputEndsTheMenuStopsPolitelyInsteadOfCrashing() {
        String output = session("1"); // wants a product name, but there is no more input
        assertTrue(output.contains("Input ended. Goodbye."));
    }
}
