package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    void typedPesosBecomeMoneyWithTwoDecimals() {
        assertEquals(Money.of("125.50"), Validation.parseMoney("125.50"));
        assertEquals(Money.of("125.00"), Validation.parseMoney("125"));
        assertEquals(Money.of("0.05"), Validation.parseMoney("0.05"));
        assertEquals(Money.of("10.00"), Validation.parseMoney(" 10.00 "));
    }

    @Test
    void oneDecimalDigitMeansTensOfCentavos() {
        assertEquals(Money.of("125.50"), Validation.parseMoney("125.5"));
    }

    @Test
    void invalidAmountsGiveNull() {
        assertNull(Validation.parseMoney(""));
        assertNull(Validation.parseMoney("abc"));
        assertNull(Validation.parseMoney("-5"));
        assertNull(Validation.parseMoney("1.234"));
        assertNull(Validation.parseMoney("125."));
        assertNull(Validation.parseMoney(".5"));
        assertNull(Validation.parseMoney("12,50"));
    }
}
