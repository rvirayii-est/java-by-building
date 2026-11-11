package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Exercises02Test {

    @Test
    void discountIsAPercentageOfTheAmount() {
        assertEquals(2000, Exercises02.discount(20000, 10));
        assertEquals(0, Exercises02.discount(20000, 0));
    }

    @Test
    void changeDueIsPaidMinusTotal() {
        assertEquals(500, Exercises02.changeDue(9500, 10000));
        assertEquals(0, Exercises02.changeDue(9500, 9500));
    }

    @Test
    void changeDueIsMinusOneWhenUnderpaid() {
        assertEquals(-1, Exercises02.changeDue(9500, 9000));
    }
}
