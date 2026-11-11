package dev.javabybuilding.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * A test helper: a log Handler that keeps every record in a list instead of printing it, so a test can ask
 * "was a WARNING logged, and what did it say?". Attach it to the logger of the class you test.
 */
final class ListHandler extends Handler {

    private final List<LogRecord> records = new ArrayList<>();
    private final LineFormatter formatter = new LineFormatter();
    private final Logger logger;

    /** Starts listening to this logger (and shows every level, down to FINEST). Call stop() when the test is over. */
    ListHandler(Logger logger) {
        this.logger = logger;
        setLevel(Level.ALL);
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false); // do not also print to the console
        logger.addHandler(this);
    }

    void stop() {
        logger.removeHandler(this);
        logger.setUseParentHandlers(true);
        logger.setLevel(null);
    }

    @Override
    public void publish(LogRecord record) {
        records.add(record);
    }

    List<LogRecord> records() {
        return List.copyOf(records);
    }

    /** The messages with their {0} {1} placeholders filled in, in the order they were logged. */
    List<String> messages() {
        return records.stream().map(formatter::formatMessage).toList();
    }

    /** The messages logged at exactly this level. */
    List<String> messages(Level level) {
        return records.stream().filter(record -> record.getLevel() == level).map(formatter::formatMessage).toList();
    }

    @Override
    public void flush() {}

    @Override
    public void close() {}
}
