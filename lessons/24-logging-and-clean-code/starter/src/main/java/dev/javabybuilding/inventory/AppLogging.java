package dev.javabybuilding.inventory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sets up where the shop's log messages go. Only the application's main method calls this; the classes that log
 * (Inventory, Menu, DataStore) just say what happened and never decide where it is written.
 *
 * Messages from every class in the package dev.javabybuilding go to the log file (everything at the given level and
 * above) and the console (warnings and errors only, so the cashier's screen stays quiet).
 */
public class AppLogging {

    /** The parent of all our loggers: Logger.getLogger("dev.javabybuilding.inventory.Inventory") is its child. */
    static final String ROOT_NAME = "dev.javabybuilding";

    /**
     * Starts logging to logFile. The file's folder is created if needed, and new messages are added to the end.
     *
     * @return the logger that was configured; pass it to {@link #stop(Logger)} before the program ends
     * @throws IOException when the file cannot be opened
     */
    public static Logger start(Path logFile, Level level) throws IOException {
        Path folder = logFile.toAbsolutePath().getParent();
        if (folder != null) {
            Files.createDirectories(folder);
        }
        Logger root = Logger.getLogger(ROOT_NAME);
        root.setLevel(level);
        root.setUseParentHandlers(false); // do not also send everything to the default console handler

        Handler file = new FileHandler(logFile.toString(), true); // true = append
        file.setLevel(level);
        file.setFormatter(new LineFormatter());
        root.addHandler(file);

        Handler console = new ConsoleHandler(); // writes to System.err
        console.setLevel(Level.WARNING);
        console.setFormatter(new LineFormatter());
        root.addHandler(console);
        return root;
    }

    /** Flushes and closes every handler (this also releases the log file), forgets them and undoes what start changed. */
    public static void stop(Logger root) {
        // TODO 5: for every handler in root.getHandlers(): close() it (that releases the file) and root.removeHandler(it).
        // Then undo what start did: root.setUseParentHandlers(true); root.setLevel(null);
    }
}
