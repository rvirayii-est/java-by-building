package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PaymentMethodTest {

    @Test
    void thereAreThreeMethodsInTheOrderTheyAreWritten() {
        PaymentMethod[] all = PaymentMethod.values();
        assertEquals(3, all.length);
        assertEquals(PaymentMethod.CASH, all[0]);
        assertEquals(PaymentMethod.GCASH, all[1]);
        assertEquals(PaymentMethod.CARD, all[2]);
    }

    @Test
    void eachMethodHasALabelForReceipts() {
        assertEquals("Cash", PaymentMethod.CASH.label());
        assertEquals("GCash", PaymentMethod.GCASH.label());
        assertEquals("Card", PaymentMethod.CARD.label());
    }

    @Test
    void onlyNonCashPaymentsNeedAReferenceNumber() {
        assertFalse(PaymentMethod.CASH.needsReference());
        assertTrue(PaymentMethod.GCASH.needsReference());
        assertTrue(PaymentMethod.CARD.needsReference());
    }

    @Test
    void fromTextIgnoresCapitalsAndSpaces() {
        assertEquals(PaymentMethod.GCASH, PaymentMethod.fromText("  gcash "));
        assertEquals(PaymentMethod.CARD, PaymentMethod.fromText("CARD"));
        assertNull(PaymentMethod.fromText("cheque"));
    }

    @Test
    void valueOfFindsAConstantByItsExactName() {
        assertEquals(PaymentMethod.CASH, PaymentMethod.valueOf("CASH"));
        assertEquals("GCASH", PaymentMethod.GCASH.name());
        assertEquals(1, PaymentMethod.GCASH.ordinal());
    }
}
