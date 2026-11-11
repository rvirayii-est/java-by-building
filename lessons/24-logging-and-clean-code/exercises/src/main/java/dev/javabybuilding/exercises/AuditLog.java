package dev.javabybuilding.exercises;

import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Lesson 24, exercise 1: logging with java.util.logging. One logger for the class, named after it; each method picks the
 * right LEVEL for the event:
 *   SEVERE  the program cannot do what it was asked (someone must act)
 *   WARNING something unexpected, but the program carried on
 *   INFO    a normal, important event (a sale, a start-up)
 *   FINE    detail for finding bugs; hidden unless someone asks for it
 *
 * Useful: LOG.log(Level.INFO, "Sale #{0}: {1} x {2}", new Object[] {a, b, c}) fills the {0} {1} {2} placeholders;
 * LOG.fine(() -> "text") builds the text only when FINE is on; LOG.log(Level.SEVERE, "text", exception) keeps the exception.
 */
public class AuditLog {

    private static final Logger LOG = Logger.getLogger(AuditLog.class.getName());

    /** TODO: INFO, message "Sale #{0}: {1} x {2}" with the arguments number, quantity, product, so it reads "Sale #7: 3 x Rice 25kg". */
    public static void saleMade(int number, String product, int quantity) {
    }

    /** TODO: WARNING, message "Refused: {0}" with the reason. */
    public static void refused(String reason) {
    }

    /** TODO: INFO, message "Starting version {0}". */
    public static void startup(String version) {
    }

    /** TODO: FINE, using the Supplier form so the text is only built when FINE is on. The message is "Detail: " + detail.get(). */
    public static void detail(Supplier<String> detail) {
    }

    /** TODO: SEVERE, message "Failed: " + what, with the exception attached (the three-argument log method). */
    public static void failure(String what, Throwable cause) {
    }
}
