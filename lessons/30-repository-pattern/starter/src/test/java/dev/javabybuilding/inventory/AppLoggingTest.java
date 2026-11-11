package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AppLoggingTest {

    private final Logger shopLogger = Logger.getLogger(Inventory.class.getName());

    @Test
    void messagesFromTheShopClassesGoToTheLogFile(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("shop.log");
        Logger root = AppLogging.start(file, Level.INFO);
        try {
            shopLogger.info("hello from the test");
            shopLogger.fine("too detailed for INFO"); // below INFO: must not appear
        } finally {
            AppLogging.stop(root); // closes the file; Windows cannot delete an open file
        }

        List<String> lines = Files.readAllLines(file);
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).endsWith("INFO    Inventory: hello from the test"), lines.get(0));
    }

    @Test
    void aMoreDetailedLevelShowsMore(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("shop.log");
        Logger root = AppLogging.start(file, Level.FINE);
        try {
            shopLogger.fine("detail");
        } finally {
            AppLogging.stop(root);
        }

        assertTrue(Files.readString(file).contains("FINE    Inventory: detail"));
    }

    @Test
    void theNextRunAddsToTheEndOfTheFile(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("logs").resolve("shop.log"); // the folder does not exist yet
        for (String text : List.of("first run", "second run")) {
            Logger root = AppLogging.start(file, Level.INFO);
            try {
                shopLogger.info(text);
            } finally {
                AppLogging.stop(root);
            }
        }

        List<String> lines = Files.readAllLines(file);
        assertEquals(2, lines.size());
        assertTrue(lines.get(0).endsWith("first run"));
        assertTrue(lines.get(1).endsWith("second run"));
    }

    @Test
    void stopRemovesTheHandlersAndReleasesTheFile(@TempDir Path dir) throws IOException {
        Logger root = AppLogging.start(dir.resolve("shop.log"), Level.INFO);
        assertEquals(2, root.getHandlers().length); // file + console
        assertFalse(root.getUseParentHandlers());

        AppLogging.stop(root);

        assertEquals(0, root.getHandlers().length);
        assertTrue(root.getUseParentHandlers());
        assertFalse(Files.exists(dir.resolve("shop.log.lck")), "the lock file goes away when the handler is closed");
    }
}
