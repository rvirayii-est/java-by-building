package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class Exercises22Test {

    @Test
    void averageKeepsTheDecimals() {
        assertEquals(1.5, BugHunt.average(new int[] {1, 2}));
        assertEquals(2.5, BugHunt.average(new int[] {1, 2, 3, 4}));
        assertEquals(0.0, BugHunt.average(new int[] {}));
    }

    @Test
    void lastItemIsTheLastOne() {
        assertEquals("c", BugHunt.lastItem(List.of("a", "b", "c")));
        assertEquals("only", BugHunt.lastItem(List.of("only")));
    }

    @Test
    void sameWordComparesTheLettersNotTheObjects() {
        String typed = new String("rice"); // a different object with the same letters
        assertTrue(BugHunt.sameWord(typed, "rice"));
        assertFalse(BugHunt.sameWord("rice", "oil"));
    }

    @Test
    void sumUpToIncludesN() {
        assertEquals(10, BugHunt.sumUpTo(4));
        assertEquals(1, BugHunt.sumUpTo(1));
        assertEquals(0, BugHunt.sumUpTo(0));
    }

    @Test
    void percentOffTakesThePercentageOfThePrice() {
        assertEquals(40, BugHunt.percentOff(200, 20));
        assertEquals(125, BugHunt.percentOff(1250, 10));
        assertEquals(0, BugHunt.percentOff(1250, 0));
    }

    @Test
    void countVowelsLooksAtEveryLetterIncludingTheFirst() {
        assertEquals(4, BugHunt.countVowels("Apple Eat")); // A, e, E, a
        assertEquals(1, BugHunt.countVowels("owl"));       // the first letter counts
        assertEquals(0, BugHunt.countVowels("rhythm"));
    }
}
