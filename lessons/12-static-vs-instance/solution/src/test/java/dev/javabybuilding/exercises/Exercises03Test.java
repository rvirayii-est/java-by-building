package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Exercises03Test {

    @Test
    void countsProductsAtOrBelowTheReorderLevel() {
        assertEquals(2, Exercises03.countLowStock(new int[] {3, 50, 10}, 10));
        assertEquals(0, Exercises03.countLowStock(new int[] {}, 10));
    }

    @Test
    void totalUnitsAddsEverything() {
        assertEquals(63, Exercises03.totalUnits(new int[] {3, 50, 10}));
        assertEquals(0, Exercises03.totalUnits(new int[] {}));
    }
}
