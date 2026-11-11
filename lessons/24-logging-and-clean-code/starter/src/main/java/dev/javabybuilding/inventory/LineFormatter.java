package dev.javabybuilding.inventory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Decides how one log record looks as a line of text:
 * <pre>2026-03-01 09:30:00 INFO    Inventory: Sale #1: 3 x Rice 25kg for PHP 3,751.50 (Cash)</pre>
 * time, level, the short name of the class that logged it, then the message. If an exception was logged, its stack
 * trace follows. java.util.logging calls format(...) for every record a Handler writes.
 */
public class LineFormatter extends Formatter {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ZoneId zone;

    /** A formatter that shows times in the shop's time zone. */
    public LineFormatter() {
        this(Inventory.SHOP_ZONE);
    }

    public LineFormatter(ZoneId zone) {
        this.zone = zone;
    }

    @Override
    public String format(LogRecord record) {
        // TODO 4: build  time + " " + level + " " + short logger name + ": " + message + line separator
        //   time:    TIME.format(record.getInstant().atZone(zone))
        //   level:   String.format("%-7s", record.getLevel().getName())   (padded to 7 so messages line up)
        //   name:    shortName(record.getLoggerName())
        //   message: formatMessage(record)   (this fills the {0} {1} placeholders)
        // and when record.getThrown() is not null, add its stack trace: record.getThrown().printStackTrace(new PrintWriter(stringWriter))
        return "";
    }

    /** "dev.javabybuilding.inventory.Inventory" becomes "Inventory". A logger with no name becomes "-". */
    static String shortName(String loggerName) {
        if (loggerName == null || loggerName.isEmpty()) {
            return "-";
        }
        return loggerName.substring(loggerName.lastIndexOf('.') + 1);
    }
}
