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
    void mainShowsTheStockAfterReceivingSugarAndSellingRice() {
        String output = runMain();
        assertTrue(output.contains(String.format("%-14s%5d  %s", "Sugar 1kg", 24, "OK")));
        assertTrue(output.contains(String.format("%-14s%5d  %s", "Rice 25kg", 9, "LOW")));
    }
}
