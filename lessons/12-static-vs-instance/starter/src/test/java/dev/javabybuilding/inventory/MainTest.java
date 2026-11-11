package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import org.junit.jupiter.api.Test;

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
        assertTrue(output.contains("Version 0.12 -"));
        assertTrue(output.contains("5. Quit"));
    }

    @Test
    void runLetsTheCashierSellFromTheSampleShop() {
        String output = runWith("3\nrice 25kg\n2\n5\n");
        assertTrue(output.contains("Sold 2 x Rice 25kg = PHP 2,501.00"));
    }
}
