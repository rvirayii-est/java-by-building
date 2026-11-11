package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    void bannerHasTheApplicationName() {
        assertEquals("=== Inventory & Sales System ===", Main.banner());
    }

    @Test
    void mainPrintsTheBanner() {
        PrintStream original = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            Main.main(new String[0]);
        } finally {
            System.setOut(original);
        }
        assertTrue(captured.toString().contains("=== Inventory & Sales System ==="));
    }
}
