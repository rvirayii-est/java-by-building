package dev.javabybuilding.inventory;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static String banner() {
        return "=== Inventory & Sales System ===";
    }

    /** Prints the banner, then runs the menu on the given shop. Tests call this with a fake keyboard. */
    public static void run(Scanner in, PrintStream out, Shop shop) {
        out.println(banner());
        out.println("Version 0.32 - Migrations (Milestone M5: database-backed)");
        Menu.run(in, out, shop);
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

    /**
     * Migrates the database to the newest version and returns the shop that runs on it (Milestone M5: the repositories are
     * JDBC ones, and the sale is a transaction because the Database is the Transactor).
     * The very first time, when the database holds no products, it is filled from the CSV files of lesson 19 in csvDir
     * if they exist there, and with the sample shop otherwise.
     */
    public static ShopService startShop(Database database, Path csvDir) throws IOException {
        // TODO 5: (a) Migrator.standard(database).migrate();
        // (b) build the shop: new ShopService(new JdbcProductRepository(database), new JdbcSaleRepository(database),
        //     new JdbcStockMovementRepository(database), Clock.system(Inventory.SHOP_ZONE), database);
        // (c) when shop.products().isEmpty(): shop.importFrom(loadOrSample(csvDir)); (d) return the shop.
        throw new UnsupportedOperationException("TODO 5");
    }

    /**
     * Runs the app on the H2 database stored in dir (files shop.mv.db and friends). Nothing needs saving at the end:
     * every sale was committed when it was made. Tests call this with a fake keyboard and a temporary folder.
     */
    public static void runWithDatabase(Scanner in, PrintStream out, Path dir) throws IOException {
        try (Database database = Database.openFile(dir.resolve("shop"))) {
            run(in, out, startShop(database, dir));
            out.println("Everything is saved in the database in " + dir + ".");
        }
    }

    /**
     * Starts the app. Default: the database in the folder shop-data (or the folder in the first argument).
     * With the word files first (java ... Main files [folder]) it works the way lessons 19-24 did, on CSV files.
     */
    public static void main(String[] args) throws IOException {
        boolean useFiles = args.length > 0 && args[0].equals("files");
        int folderArgument = useFiles ? 1 : 0;
        Path dir = args.length > folderArgument ? Path.of(args[folderArgument]) : DEFAULT_DATA_DIR;
        Logger logging = AppLogging.start(dir.resolve("shop.log"), Level.INFO); // log file next to the data
        boolean failed = false;
        try {
            if (useFiles) {
                Inventory inventory = loadOrSample(dir);
                run(new Scanner(System.in), System.out, inventory);
                DataStore.save(inventory, dir);
                System.out.println("Saved to " + dir + ".");
            } else {
                runWithDatabase(new Scanner(System.in), System.out, dir);
            }
        } catch (DataFileException | DatabaseException | MigrationException e) {
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
