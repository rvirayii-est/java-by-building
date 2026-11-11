package dev.javabybuilding.inventory;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static String banner() {
        return "=== Inventory & Sales System ===";
    }

    /** Prints the banner, then runs the menu on the given inventory. Tests call this with a fake keyboard. */
    public static void run(Scanner in, PrintStream out, Inventory inventory) {
        out.println(banner());
        out.println("Version 0.28 - Schema design for the shop");
        Menu.run(in, out, inventory);
    }

    /** Where the shop's data lives when you do not say otherwise: a folder called shop-data next to where you run the app. */
    public static final Path DEFAULT_DATA_DIR = Path.of("shop-data");

    /** The saved shop in dir, or the sample shop the very first time (when nothing has been saved yet). */
    public static Inventory loadOrSample(Path dir) throws IOException {
        if (!DataStore.exists(dir)) {
            return Inventory.sample();
        }
        return DataStore.load(dir);
    }

    /** Loads the data folder (the first argument, or shop-data), runs the menu, then saves everything back. */
    public static void main(String[] args) throws IOException {
        Path dir = args.length > 0 ? Path.of(args[0]) : DEFAULT_DATA_DIR;
        Logger logging = AppLogging.start(dir.resolve("shop.log"), Level.INFO); // log file next to the data files
        boolean failed = false;
        try {
            Inventory inventory = loadOrSample(dir);
            run(new Scanner(System.in), System.out, inventory);
            DataStore.save(inventory, dir);
            System.out.println("Saved to " + dir + ".");
        } catch (DataFileException e) {
            // Logged ONCE, here, where the problem is finally handled (not where it was thrown, and not at every layer in between).
            LOG.log(Level.SEVERE, "The shop data cannot be used: {0}", e.getMessage());
            System.err.println("Cannot start: " + e.getMessage());
            failed = true;
        } finally {
            AppLogging.stop(logging); // flush and release the log file even if something went wrong
        }
        if (failed) {
            System.exit(1);
        }
    }
}
