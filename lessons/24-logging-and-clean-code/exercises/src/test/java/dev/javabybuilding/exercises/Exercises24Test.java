package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Exercises24Test {

    // ---- AuditLog: listen to the logger with a handler that keeps the records
    private final List<LogRecord> records = new ArrayList<>();
    private final Logger logger = Logger.getLogger(AuditLog.class.getName());
    private final Handler handler = new Handler() {
        @Override public void publish(LogRecord record) { records.add(record); }
        @Override public void flush() {}
        @Override public void close() {}
    };

    @BeforeEach
    void listen() {
        handler.setLevel(Level.ALL);
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
    }

    @AfterEach
    void stopListening() {
        logger.removeHandler(handler);
        logger.setUseParentHandlers(true);
        logger.setLevel(null);
    }

    private String text(LogRecord record) {
        return new java.util.logging.SimpleFormatter().formatMessage(record);
    }

    @Test
    void aSaleIsLoggedAtInfo() {
        AuditLog.saleMade(7, "Rice 25kg", 3);

        assertEquals(1, records.size());
        assertEquals(Level.INFO, records.get(0).getLevel());
        assertEquals("Sale #7: 3 x Rice 25kg", text(records.get(0)));
    }

    @Test
    void aRefusalIsAWarning() {
        AuditLog.refused("Unknown product: salt");

        assertEquals(Level.WARNING, records.get(0).getLevel());
        assertEquals("Refused: Unknown product: salt", text(records.get(0)));
    }

    @Test
    void startUpIsInfoWithTheVersion() {
        AuditLog.startup("0.24");

        assertEquals(Level.INFO, records.get(0).getLevel());
        assertEquals("Starting version 0.24", text(records.get(0)));
    }

    @Test
    void detailIsFineAndOnlyBuiltWhenFineIsOn() {
        int[] built = {0};
        logger.setLevel(Level.INFO); // FINE is switched off
        AuditLog.detail(() -> {
            built[0]++;
            return "expensive text";
        });
        assertEquals(0, built[0]);
        assertTrue(records.isEmpty());

        logger.setLevel(Level.ALL); // FINE is on
        AuditLog.detail(() -> {
            built[0]++;
            return "expensive text";
        });
        assertEquals(1, built[0]);
        assertEquals(Level.FINE, records.get(0).getLevel());
        assertEquals("Detail: expensive text", records.get(0).getMessage());
    }

    @Test
    void aFailureKeepsTheExceptionSoTheStackTraceIsNotLost() {
        Exception cause = new IllegalStateException("disk full");

        AuditLog.failure("saving the shop", cause);

        assertEquals(Level.SEVERE, records.get(0).getLevel());
        assertEquals("Failed: saving the shop", records.get(0).getMessage());
        assertSame(cause, records.get(0).getThrown());
    }

    @Test
    void theLoggerIsNamedAfterTheClass() {
        AuditLog.startup("1");

        assertEquals("dev.javabybuilding.exercises.AuditLog", records.get(0).getLoggerName());
    }

    // ---- ReceiptFormatter
    @Test
    void padRightPadsAndCuts() {
        assertEquals("Rice  ", ReceiptFormatter.padRight("Rice", 6));
        assertEquals("Cooki", ReceiptFormatter.padRight("Cooking Oil", 5));
        assertEquals("abc", ReceiptFormatter.padRight("abc", 3));
    }

    @Test
    void padLeftPadsButNeverCuts() {
        assertEquals("  7", ReceiptFormatter.padLeft("7", 3));
        assertEquals("12345", ReceiptFormatter.padLeft("12345", 3));
    }

    @Test
    void moneyHasCommasAndTwoDecimals() {
        assertEquals("1,250.50", ReceiptFormatter.money(new BigDecimal("1250.5")));
        assertEquals("0.05", ReceiptFormatter.money(new BigDecimal("0.05")));
        assertEquals("1,000,000.00", ReceiptFormatter.money(new BigDecimal("1000000")));
    }

    @Test
    void aLineHasFixedColumns() {
        String line = ReceiptFormatter.line("Rice 25kg", 3, new BigDecimal("1250.50"));

        assertEquals("Rice 25kg        3 x  1,250.50   3,751.50", line);
        assertEquals(41, line.length());
    }

    @Test
    void aLongNameIsCutToTheColumn() {
        assertEquals("Sardines 155g ", ReceiptFormatter.line("Sardines 155g with chili", 1, new BigDecimal("24.00")).substring(0, 14));
    }

    @Test
    void aReceiptHasTitleRulesLinesAndTotal() {
        List<String> lines = List.of(ReceiptFormatter.line("Rice 25kg", 3, new BigDecimal("1250.50")),
                ReceiptFormatter.line("Sardines", 2, new BigDecimal("24.00")));

        String expected = "Sari-Sari Store\n"
                + "-----------------------------------------\n"
                + "Rice 25kg        3 x  1,250.50   3,751.50\n"
                + "Sardines         2 x     24.00      48.00\n"
                + "-----------------------------------------\n"
                + "TOTAL                            3,799.50";
        assertEquals(expected, ReceiptFormatter.receipt("Sari-Sari Store", lines, new BigDecimal("3799.50")));
    }

    @Test
    void anEmptyReceiptStillHasItsTotal() {
        String receipt = ReceiptFormatter.receipt("Shop", List.of(), BigDecimal.ZERO);

        assertEquals("Shop\n-----------------------------------------\n-----------------------------------------\nTOTAL                                0.00", receipt);
    }
}
