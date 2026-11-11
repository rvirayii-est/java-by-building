package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Milestone M5, end to end: the console app on a database FILE, over several "days" (runs of the program). */
class MainDatabaseTest {

    private static String run(Path folder, String typedInput) throws IOException {
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        Main.runWithDatabase(new Scanner(typedInput), new PrintStream(captured), folder);
        return captured.toString();
    }

    @Test
    void theFirstRunStartsFromTheSampleShopAndCreatesTheDatabaseFile(@TempDir Path folder) throws IOException {
        String screen = run(folder, "4\n5\n");
        assertTrue(screen.contains("Rice 25kg"));
        assertTrue(screen.contains("Version 0.32 - Migrations"));
        assertTrue(Files.exists(folder.resolve("shop.mv.db")));
        assertTrue(screen.contains("Everything is saved in the database"));
    }

    @Test
    void aSaleMadeOnMondayIsStillThereOnTuesday(@TempDir Path folder) throws IOException {
        String monday = run(folder, "3\nrice 25kg\n2\n4\n5\n");
        assertTrue(monday.contains("Sold 2 x Rice 25kg = PHP 2,501.00"));
        String tuesday = run(folder, "4\n5\n");
        assertTrue(tuesday.contains("Sales so far: PHP 2,501.00"));
        assertTrue(tuesday.contains("Rice 25kg") && tuesday.contains("10  LOW"));
    }

    @Test
    void stockReceivedAndProductsAddedSurviveARestart(@TempDir Path folder) throws IOException {
        run(folder, "1\nSalt\n9.50\n10\n2\nsalt\n40\n5\n");
        String next = run(folder, "4\n5\n");
        assertTrue(next.contains("Salt"));
        assertTrue(next.contains("40  OK"));
    }

    @Test
    void theFirstRunCanTakeOverTheCsvFilesOfLesson19(@TempDir Path folder) throws IOException {
        Inventory old = Inventory.sample();
        old.sell("Rice 25kg", 5);
        DataStore.save(old, folder); // products.csv, sales.csv, movements.csv, as the old app left them
        String screen = run(folder, "4\n5\n");
        assertTrue(screen.contains("Sales so far: PHP 6,252.50"));
        assertTrue(screen.contains("7  LOW"));
        assertTrue(Files.exists(folder.resolve("products.csv")), "the old files are left alone");
    }

    @Test
    void theCsvFilesAreOnlyImportedOnce(@TempDir Path folder) throws IOException {
        DataStore.save(Inventory.sample(), folder);
        run(folder, "5\n");
        Inventory changed = Inventory.sample();
        changed.sell("Rice 25kg", 12);
        DataStore.save(changed, folder); // someone edits the old files afterwards
        String screen = run(folder, "4\n5\n");
        assertTrue(screen.contains("Sales so far: PHP 0.00"), "the database is now the truth, not the files");
    }

    @Test
    void startShopBringsTheDatabaseToTheLatestVersion(@TempDir Path folder) throws IOException {
        try (Database database = Database.openFile(folder.resolve("shop"))) {
            ShopService shop = Main.startShop(database, folder.resolve("no-csv-here"));
            assertEquals(3, new Migrator(database, Migrations.fromClasspath(), java.time.Clock.systemUTC()).currentVersion());
            assertEquals(4, shop.products().size());
            assertTrue(shop.audit().isEmpty());
        }
    }

    @Test
    void anImportedShopPassesTheAudit(@TempDir Path folder) throws IOException {
        Inventory old = Inventory.sample();
        old.sell("Rice 25kg", 5);
        old.sell("Sardines 155g", 3);
        DataStore.save(old, folder);
        try (Database database = Database.openFile(folder.resolve("shop"))) {
            ShopService shop = Main.startShop(database, folder);
            assertTrue(shop.audit().isEmpty());
            assertEquals(2, shop.sales().size());
            assertEquals(old.salesTotal(), shop.salesTotal());
        }
    }

    @Test
    void theOldFileModeStillWorks(@TempDir Path folder) throws IOException {
        Inventory inventory = Main.loadOrSample(folder.resolve("nothing-saved-yet"));
        inventory.sell("Rice 25kg", 5);
        DataStore.save(inventory, folder);
        assertEquals(7, Main.loadOrSample(folder).quantityOf("Rice 25kg"));
    }
}
