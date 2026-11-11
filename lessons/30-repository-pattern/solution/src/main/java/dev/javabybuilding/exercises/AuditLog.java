package dev.javabybuilding.exercises;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lesson 24, exercise 1: logging with java.util.logging. One logger for the class, named after it; each method picks the
 * right LEVEL for the event:
 *   SEVERE  the program cannot do what it was asked (someone must act)
 *   WARNING something unexpected, but the program carried on
 *   INFO    a normal, important event (a sale, a start-up)
 *   FINE    detail for finding bugs; hidden unless someone asks for it
 */
public class AuditLog {

    private static final Logger LOG = Logger.getLogger(AuditLog.class.getName());

    /** INFO: "Sale #7: 3 x Rice 25kg" (placeholders {0} {1} {2} filled from the arguments). */
    public static void saleMade(int number, String product, int quantity) {
        LOG.log(Level.INFO, "Sale #{0}: {1} x {2}", new Object[] {number, quantity, product});
    }

    /** WARNING: "Refused: Unknown product: salt". */
    public static void refused(String reason) {
        LOG.log(Level.WARNING, "Refused: {0}", reason);
    }

    /** INFO: "Starting version 0.24". */
    public static void startup(String version) {
        LOG.log(Level.INFO, "Starting version {0}", version);
    }

    /** FINE, with a Supplier so the text is only built when FINE is switched on: the message is "Detail: " + detail.get(). */
    public static void detail(java.util.function.Supplier<String> detail) {
        LOG.fine(() -> "Detail: " + detail.get());
    }

    /** SEVERE: "Failed: " + what, with the exception attached so its stack trace is kept. */
    public static void failure(String what, Throwable cause) {
        LOG.log(Level.SEVERE, "Failed: " + what, cause);
    }
}
