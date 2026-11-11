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
        Main.run(new Scanner(typedInput), new PrintStream(captured));
        return captured.toString();
    }

    @Test
    void bannerHasTheApplicationName() {
        assertEquals("=== Inventory & Sales System ===", Main.banner());
    }

    @Test
    void runPrintsTheBanner() {
        assertTrue(runWith("rice\n1\n10\n").contains("=== Inventory & Sales System ==="));
    }

    @Test
    void runPricesASaleLine() {
        String output = runWith("rice   25kg\n2\n1250.50\n");
        assertTrue(output.contains("Sale line: Rice 25kg x 2"));
        assertTrue(output.contains("Total: PHP 2,501.00"));
        assertTrue(output.contains("VAT included: PHP 267.96")); // 2501.00 x 12 / 112 = 267.964...
    }

    @Test
    void runKeepsAskingUntilTheAnswersAreValid() {
        String output = runWith("\nSugar\nlots\n0\n3\nfree\n75\n");
        assertTrue(output.contains("Please type something."));
        assertTrue(output.contains("Please type a whole number."));
        assertTrue(output.contains("Please type a number from 1 to 100."));
        assertTrue(output.contains("Please type an amount like 125 or 125.50"));
        assertTrue(output.contains("Total: PHP 225.00"));
    }
}
