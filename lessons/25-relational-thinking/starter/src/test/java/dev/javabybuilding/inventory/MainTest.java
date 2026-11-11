package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Scanner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MainTest {

    private static String runWith(String typedInput) {
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        Main.run(new Scanner(typedInput), new PrintStream(captured), Inventory.sample());
        return captured.toString();
    }

    @Test
    void bannerHasTheApplicationName() {
        assertEquals("=== Inventory & Sales System ===", Main.banner());
    }

    @Test
    void runPrintsTheBannerVersionAndMenu() {
        String output = runWith("5\n");
        assertTrue(output.contains("=== Inventory & Sales System ==="));
        assertTrue(output.contains("Version 0.25 -"));
        assertTrue(output.contains("5. Quit"));
    }

    @Test
    void theFirstRunStartsWithTheSampleShop(@TempDir Path dir) throws IOException {
        Inventory inventory = Main.loadOrSample(dir.resolve("nothing-saved-yet"));
        assertEquals(4, inventory.size());
        assertEquals(12, inventory.quantityOf("Rice 25kg"));
    }

    @Test
    void laterRunsStartFromWhatWasSaved(@TempDir Path dir) throws IOException {
        Inventory inventory = Inventory.sample();
        inventory.sell("Rice 25kg", 5);
        DataStore.save(inventory, dir);
        Inventory reloaded = Main.loadOrSample(dir);
        assertEquals(7, reloaded.quantityOf("Rice 25kg"));
        assertEquals(1, reloaded.sales().size());
    }

    @Test
    void runLetsTheCashierSellFromTheSampleShop() {
        String output = runWith("3\nrice 25kg\n2\n5\n");
        assertTrue(output.contains("Sold 2 x Rice 25kg = PHP 2,501.00"));
    }
}
