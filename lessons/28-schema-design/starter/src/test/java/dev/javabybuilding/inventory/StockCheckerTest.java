package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StockCheckerTest {

    @Test
    void zeroMeansOut() {
        assertEquals("OUT", StockChecker.status(0, 10));
    }

    @Test
    void atOrBelowReorderLevelMeansLow() {
        assertEquals("LOW", StockChecker.status(10, 10));
        assertEquals("LOW", StockChecker.status(1, 10));
    }

    @Test
    void aboveReorderLevelMeansOk() {
        assertEquals("OK", StockChecker.status(11, 10));
    }
}
