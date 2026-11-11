package dev.javabybuilding.inventory;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    public static String banner() {
        return "=== Inventory & Sales System ===";
    }

    /** Prints the banner, then runs the menu on the given inventory. Tests call this with a fake keyboard. */
    public static void run(Scanner in, PrintStream out, Inventory inventory) {
        out.println(banner());
        out.println("Version 0.22 - Debugging");
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
        Inventory inventory = loadOrSample(dir);
        run(new Scanner(System.in), System.out, inventory);
        DataStore.save(inventory, dir);
        System.out.println("Saved to " + dir + ".");
    }
}
