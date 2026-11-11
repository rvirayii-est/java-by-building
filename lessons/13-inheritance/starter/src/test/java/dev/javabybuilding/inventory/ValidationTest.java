package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ValidationTest {

    @Test
    void wholeNumbersAreAccepted() {
        assertTrue(Validation.isWholeNumber("42"));
        assertTrue(Validation.isWholeNumber(" 7 "));
        assertTrue(Validation.isWholeNumber("0"));
    }

    @Test
    void thingsThatAreNotWholeNumbersAreRejected() {
        assertFalse(Validation.isWholeNumber(""));
        assertFalse(Validation.isWholeNumber("abc"));
        assertFalse(Validation.isWholeNumber("-5"));
        assertFalse(Validation.isWholeNumber("4.5"));
        assertFalse(Validation.isWholeNumber("4 5"));
    }

    @Test
    void tooManyDigitsAreRejectedSoTheyCannotOverflowAnInt() {
        assertTrue(Validation.isWholeNumber("999999999"));
        assertFalse(Validation.isWholeNumber("1234567890"));
    }

    @Test
    void pesosBecomeCentavos() {
        assertEquals(12550, Validation.parseCentavos("125.50"));
        assertEquals(12500, Validation.parseCentavos("125"));
        assertEquals(5, Validation.parseCentavos("0.05"));
        assertEquals(1000, Validation.parseCentavos(" 10.00 "));
    }

    @Test
    void oneDecimalDigitMeansTensOfCentavos() {
        assertEquals(12550, Validation.parseCentavos("125.5"));
    }

    @Test
    void invalidAmountsGiveMinusOne() {
        assertEquals(-1, Validation.parseCentavos(""));
        assertEquals(-1, Validation.parseCentavos("abc"));
        assertEquals(-1, Validation.parseCentavos("-5"));
        assertEquals(-1, Validation.parseCentavos("1.234"));
        assertEquals(-1, Validation.parseCentavos("125."));
        assertEquals(-1, Validation.parseCentavos(".5"));
        assertEquals(-1, Validation.parseCentavos("12,50"));
    }
}
