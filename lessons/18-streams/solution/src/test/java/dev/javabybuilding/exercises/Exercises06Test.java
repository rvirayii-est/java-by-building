package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class Exercises06Test {

    @Test
    void yesAnswers() {
        assertTrue(Exercises06.isYes("y"));
        assertTrue(Exercises06.isYes(" YES "));
        assertTrue(Exercises06.isYes("Yes"));
    }

    @Test
    void everythingElseIsNo() {
        assertFalse(Exercises06.isYes("n"));
        assertFalse(Exercises06.isYes(""));
        assertFalse(Exercises06.isYes("yess"));
    }

    @Test
    void parseQuantityAcceptsNumbersInRange() {
        assertEquals(12, Exercises06.parseQuantity(" 12 ", 100));
        assertEquals(100, Exercises06.parseQuantity("100", 100));
        assertEquals(1, Exercises06.parseQuantity("1", 100));
    }

    @Test
    void parseQuantityRejectsBadText() {
        assertEquals(-1, Exercises06.parseQuantity("abc", 100));
        assertEquals(-1, Exercises06.parseQuantity("", 100));
        assertEquals(-1, Exercises06.parseQuantity("-3", 100));
        assertEquals(-1, Exercises06.parseQuantity("2.5", 100));
        assertEquals(-1, Exercises06.parseQuantity("99999999999", 100));
    }

    @Test
    void parseQuantityRejectsNumbersOutOfRange() {
        assertEquals(-1, Exercises06.parseQuantity("0", 100));
        assertEquals(-1, Exercises06.parseQuantity("101", 100));
    }

    @Test
    void sumOfNumbersIgnoresWords() {
        assertEquals(35, Exercises06.sumOfNumbers("10 20 abc 5"));
        assertEquals(0, Exercises06.sumOfNumbers("no numbers here"));
        assertEquals(0, Exercises06.sumOfNumbers(""));
    }
}
