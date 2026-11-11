package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Exercises04Test {

    @Test
    void highestQuantityIndexFindsTheBiggest() {
        assertEquals(1, Exercises04.highestQuantityIndex(new int[] {3, 50, 10}));
        assertEquals(0, Exercises04.highestQuantityIndex(new int[] {9}));
    }

    @Test
    void highestQuantityIndexIsMinusOneForEmpty() {
        assertEquals(-1, Exercises04.highestQuantityIndex(new int[] {}));
    }

    @Test
    void countEqualCountsMatches() {
        assertEquals(2, Exercises04.countEqual(new int[] {5, 0, 5, 7}, 5));
        assertEquals(0, Exercises04.countEqual(new int[] {5, 0, 5, 7}, 9));
    }

    @Test
    void reversedReturnsANewArrayInOppositeOrder() {
        int[] original = {1, 2, 3};
        int[] result = Exercises04.reversed(original);
        assertArrayEquals(new int[] {3, 2, 1}, result);
        assertArrayEquals(new int[] {1, 2, 3}, original);
    }
}
