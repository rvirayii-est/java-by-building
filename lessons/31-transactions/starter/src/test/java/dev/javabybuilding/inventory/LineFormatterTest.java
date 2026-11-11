package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.ZoneId;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.junit.jupiter.api.Test;

class LineFormatterTest {

    private final LineFormatter formatter = new LineFormatter(ZoneId.of("Asia/Manila"));

    private LogRecord record(Level level, String loggerName, String message) {
        LogRecord record = new LogRecord(level, message);
        record.setLoggerName(loggerName);
        record.setInstant(Instant.parse("2026-03-01T01:30:05Z")); // 09:30:05 in Manila
        return record;
    }

    @Test
    void aLineHasTimeLevelShortLoggerNameAndMessage() {
        LogRecord record = record(Level.INFO, "dev.javabybuilding.inventory.Inventory", "hello");

        assertEquals("2026-03-01 09:30:05 INFO    Inventory: hello" + System.lineSeparator(), formatter.format(record));
    }

    @Test
    void levelsAreAlignedSoMessagesStartInTheSameColumn() {
        String warning = formatter.format(record(Level.WARNING, "a.B", "x"));
        String severe = formatter.format(record(Level.SEVERE, "a.B", "x"));

        assertEquals(warning.indexOf("B:"), severe.indexOf("B:"));
    }

    @Test
    void placeholdersInTheMessageAreFilledIn() {
        LogRecord record = record(Level.INFO, "x.Y", "Received {0} x {1}");
        record.setParameters(new Object[] {8, "Rice 25kg"});

        assertTrue(formatter.format(record).contains("Y: Received 8 x Rice 25kg"));
    }

    @Test
    void aLoggedExceptionAddsItsStackTrace() {
        LogRecord record = record(Level.SEVERE, "x.Y", "failed");
        record.setThrown(new IllegalStateException("boom"));

        String text = formatter.format(record);
        assertTrue(text.contains("SEVERE  Y: failed"));
        assertTrue(text.contains("java.lang.IllegalStateException: boom"));
    }

    @Test
    void theShortNameIsWhatComesAfterTheLastDot() {
        assertEquals("Inventory", LineFormatter.shortName("dev.javabybuilding.inventory.Inventory"));
        assertEquals("Plain", LineFormatter.shortName("Plain"));
        assertEquals("-", LineFormatter.shortName(null));
        assertEquals("-", LineFormatter.shortName(""));
    }
}
