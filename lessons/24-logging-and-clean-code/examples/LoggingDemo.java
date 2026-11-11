// Run with:  java LoggingDemo.java
// java.util.logging is part of Java: nothing to install. A Logger receives messages; a Handler decides where they go
// (console, file); a Formatter decides how a line looks; a Level decides which messages are kept.
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggingDemo {

    // One logger per class, named after it. static final: created once.
    private static final Logger LOG = Logger.getLogger(LoggingDemo.class.getName());

    /** A line like  [WARNING] LoggingDemo: text  -- no time, so the output is the same every run. */
    static class PlainFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            String shortName = record.getLoggerName().substring(record.getLoggerName().lastIndexOf('.') + 1);
            return "[" + record.getLevel() + "] " + shortName + ": " + formatMessage(record) + "\n";
        }
    }

    /** A handler that writes to System.out, so the demo prints everything in one place. */
    static class OutHandler extends ConsoleHandler {
        OutHandler() {
            setOutputStream(System.out); // ConsoleHandler normally writes to System.err
            setFormatter(new PlainFormatter());
        }
    }

    public static void main(String[] args) {
        // 1. Wire it up once, at the start of the program (never inside the classes that log).
        LOG.setUseParentHandlers(false); // do not also use the default console handler
        Handler handler = new OutHandler();
        handler.setLevel(Level.ALL);     // the handler lets everything through; the LOGGER decides
        LOG.addHandler(handler);

        // 2. Levels, from most to least serious: SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST.
        LOG.setLevel(Level.INFO);
        LOG.severe("Cannot open the data folder");
        LOG.warning("Refused: Unknown product: salt");
        LOG.info("Sale #1: 3 x Rice 25kg");
        LOG.fine("this FINE message is below INFO, so nobody sees it");
        System.out.println("-- FINE is loggable at INFO? " + LOG.isLoggable(Level.FINE));

        // 3. Turn the detail up while hunting a bug, without changing any other line of code.
        LOG.setLevel(Level.FINE);
        LOG.fine("now the FINE message is shown");

        // 4. Placeholders {0} {1}: the text is only assembled if the message is going to be written.
        LOG.setLevel(Level.INFO);
        LOG.log(Level.INFO, "Received {0} x {1}", new Object[] {8, "Rice 25kg"});

        // 5. A Supplier (lambda) is even lazier: it is not called at all when FINE is off.
        int[] calls = {0};
        LOG.fine(() -> {
            calls[0]++;
            return "expensive text";
        });
        System.out.println("-- the expensive text was built " + calls[0] + " times while FINE was off");

        // 6. Log an exception WITH the exception, so the cause is not lost. (Here we print just its first line.)
        LOG.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (record.getThrown() != null) {
                    System.out.println("   attached exception: " + record.getThrown());
                }
            }

            @Override
            public void flush() {}

            @Override
            public void close() {}
        });
        try {
            Integer.parseInt("12.5");
        } catch (NumberFormatException e) {
            LOG.log(Level.WARNING, "Could not read the quantity", e);
        }

        // 7. Loggers form a tree by name: a child sends its records up to its parent's handlers.
        Logger child = Logger.getLogger(LoggingDemo.class.getName() + ".child");
        child.info("a message from a child logger");
        System.out.println("-- the child's parent is: " + child.getParent().getName());
    }
}
