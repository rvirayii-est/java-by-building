package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DiscountCodesTest {

    @Test
    void everyKindOfDiscountHasACode() {
        assertEquals("none", DiscountCodes.encode(new NoDiscount()));
        assertEquals("percent:10", DiscountCodes.encode(new PercentDiscount(10)));
        assertEquals("fixed:50.00", DiscountCodes.encode(new FixedDiscount(Money.of("50.00"))));
        assertEquals("senior", DiscountCodes.encode(new SeniorPwdDiscount()));
    }

    @Test
    void decodeRebuildsTheSameKindOfDiscount() {
        assertInstanceOf(NoDiscount.class, DiscountCodes.decode("none"));
        assertEquals(10, assertInstanceOf(PercentDiscount.class, DiscountCodes.decode("percent:10")).getPercent());
        assertEquals(Money.of("50.00"), assertInstanceOf(FixedDiscount.class, DiscountCodes.decode("fixed:50.00")).getAmount());
        assertInstanceOf(SeniorPwdDiscount.class, DiscountCodes.decode("senior"));
    }

    @Test
    void unknownCodesAndBadNumbersAreRefused() {
        assertEquals("Unknown discount code: half price",
                assertThrows(InvalidInputException.class, () -> DiscountCodes.decode("half price")).getMessage());
        assertThrows(InvalidInputException.class, () -> DiscountCodes.decode("percent:abc"));
        assertThrows(InvalidInputException.class, () -> DiscountCodes.decode("fixed:5.555")); // more than 2 decimals
        assertThrows(InvalidInputException.class, () -> DiscountCodes.decode("percent:150")); // PercentDiscount refuses it
    }
}
