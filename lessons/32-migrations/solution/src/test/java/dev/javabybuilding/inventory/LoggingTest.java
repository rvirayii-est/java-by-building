package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** What the shop writes to its log, and at which level. Each test listens to the logger of the class it exercises. */
class LoggingTest {

    private ListHandler inventoryLog;
    private ListHandler menuLog;
    private ListHandler dataLog;
    private final Inventory inventory = Inventory.sample();

    @BeforeEach
    void listen() {
        inventoryLog = new ListHandler(Logger.getLogger(Inventory.class.getName()));
        menuLog = new ListHandler(Logger.getLogger(Menu.class.getName()));
        dataLog = new ListHandler(Logger.getLogger(DataStore.class.getName()));
    }

    @AfterEach
    void stopListening() {
        inventoryLog.stop();
        menuLog.stop();
        dataLog.stop();
    }

    private void runMenu(String... lines) {
        Menu.run(new Scanner(String.join("\n", lines) + "\n"), new PrintStream(new ByteArrayOutputStream()), inventory);
    }

    @Test
    void aSaleIsLoggedAtInfoWithTheDetails() {
        inventory.sell("Rice 25kg", 3, new Customer("Ana", ""), new NoDiscount(), PaymentMethod.GCASH);

        assertEquals(List.of("Sale #1: 3 x Rice 25kg for PHP 3,751.50 (GCash)"), inventoryLog.messages(Level.INFO).stream()
                .filter(m -> m.startsWith("Sale")).toList());
    }

    @Test
    void receivingStockIsLoggedAtInfoWithTheNewQuantity() {
        inventory.receive("Rice 25kg", 8);

        assertEquals(List.of("Received 8 x Rice 25kg (now 20 in stock)"), inventoryLog.messages(Level.INFO));
    }

    @Test
    void aRefusedSaleLogsNothingFromTheInventoryBecauseNothingHappened() {
        assertThrows(InsufficientStockException.class, () -> inventory.sell("Rice 25kg", 500));

        assertTrue(inventoryLog.records().isEmpty());
    }

    @Test
    void theMenuLogsARefusalAsAWarning() {
        runMenu("3", "salt", "5");

        assertEquals(List.of("Refused: Unknown product: salt"), menuLog.messages(Level.WARNING));
    }

    @Test
    void theMenuLogsEachRefusalInOrder() {
        runMenu("3", "rice 25kg", "99", "2", "salt", "5");

        assertEquals(List.of("Refused: Not enough stock: only 12 of Rice 25kg left.", "Refused: Unknown product: salt"),
                menuLog.messages(Level.WARNING));
    }

    @Test
    void aSuccessfulMenuActionLogsNoWarning() {
        runMenu("3", "rice 25kg", "1", "5");

        assertTrue(menuLog.messages(Level.WARNING).isEmpty());
    }

    @Test
    void savingAndLoadingAreLoggedAtInfo(@TempDir Path dir) throws IOException {
        inventory.sell("Rice 25kg", 1);
        DataStore.save(inventory, dir);
        DataStore.load(dir);

        assertEquals(List.of("Saved 4 products, 1 sales and 4 stock movements to " + dir, "Loaded 4 products and 1 sales from " + dir),
                dataLog.messages(Level.INFO));
    }

    @Test
    void readingAnOldFormatFileIsLoggedAsAWarning(@TempDir Path dir) throws IOException {
        Files.writeString(dir.resolve("products.csv"), "name,priceCentavos,reorderLevel,quantity\n");
        Files.writeString(dir.resolve("movements.csv"), "product,type,quantity\n");
        Files.writeString(dir.resolve("sales.csv"), "number,customer,phone,discount,payment,product,unitPriceCentavos,quantity\n");
        DataStore.load(dir);

        List<String> warnings = dataLog.messages(Level.WARNING);
        assertEquals(3, warnings.size());
        assertTrue(warnings.get(0).startsWith("products.csv is in the old lesson 19 format"));
    }

    @Test
    void aLoggerOnlyShowsMessagesAtOrAboveItsLevel() {
        Logger logger = Logger.getLogger(Inventory.class.getName());
        logger.setLevel(Level.INFO);

        assertTrue(logger.isLoggable(Level.WARNING));
        assertTrue(logger.isLoggable(Level.INFO));
        assertFalse(logger.isLoggable(Level.FINE)); // FINE is below INFO, so it is skipped
    }
}
