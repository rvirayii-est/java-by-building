package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;

class MainTest {

    private static String runMain() {
        PrintStream original = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            Main.main(new String[0]);
        } finally {
            System.setOut(original);
        }
        return captured.toString();
    }

    @Test
    void bannerHasTheApplicationName() {
        assertEquals("=== Inventory & Sales System ===", Main.banner());
    }

    @Test
    void mainPrintsTheBanner() {
        assertTrue(runMain().contains("=== Inventory & Sales System ==="));
    }

    @Test
    void mainCleansMessyTextAndFindsTheProduct() {
        String output = runMain();
        assertTrue(output.contains("Cleaned: [sUGAr 1KG]"));
        assertTrue(output.contains("Title case: Sugar 1kg"));
        assertTrue(output.contains("Found at position 2: Sugar 1kg"));
    }

    @Test
    void mainListsProductsThatMatchOil() {
        assertTrue(runMain().contains("  Cooking Oil 1L"));
    }
}
