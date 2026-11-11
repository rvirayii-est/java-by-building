package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;

class MainTest {

    private static String runMain() {
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        Main.run(new PrintStream(captured));
        return captured.toString();
    }

    @Test
    void bannerHasTheApplicationName() {
        assertEquals("=== Inventory & Sales System ===", Main.banner());
    }

    @Test
    void runPrintsTheBanner() {
        assertTrue(runMain().contains("=== Inventory & Sales System ==="));
    }

    @Test
    void runShowsTheNewProductSaleAndSalesTotal() {
        String output = runMain();
        assertTrue(output.contains(String.format("%-14s%5d  %s", "Salt 500g", 30, "OK")));
        assertTrue(output.contains(String.format("%-14s%5d  %s", "Rice 25kg", 9, "LOW")));
        assertTrue(output.contains("Sold 3 x Rice 25kg = PHP 3,751.50"));
        assertTrue(output.contains("Sales so far: PHP 3,751.50"));
        assertTrue(output.contains("Salt 500g already exists"));
    }
}
