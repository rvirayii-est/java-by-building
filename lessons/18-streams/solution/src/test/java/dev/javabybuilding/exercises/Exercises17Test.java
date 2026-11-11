package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class Exercises17Test {

    @Test
    void swapTurnsPairOfAAndBIntoPairOfBAndA() {
        Pair<String, Integer> item = new Pair<>("Rice 25kg", 12);
        Pair<Integer, String> swapped = item.swap(); // the compiler knows the new types
        assertEquals(12, swapped.first());
        assertEquals("Rice 25kg", swapped.second());
        assertEquals(item, swapped.swap());
    }

    @Test
    void distinctDropsRepeatsAndKeepsFirstSeenOrder() {
        assertEquals(List.of("b", "a", "c"), ListTools.distinct(List.of("b", "a", "b", "c", "a")));
        assertEquals(List.of(), ListTools.distinct(List.<Integer>of()));
    }

    @Test
    void countAllCountsEachItem() {
        Map<String, Integer> counts = ListTools.countAll(List.of("rice", "oil", "rice", "rice"));
        assertEquals(Map.of("rice", 3, "oil", 1), counts);
        assertEquals(List.of("rice", "oil"), List.copyOf(counts.keySet())); // first-seen order
    }

    @Test
    void largestWorksForAnyComparableType() {
        assertEquals(40, ListTools.largest(List.of(12, 40, 7)));
        assertEquals("pear", ListTools.largest(List.of("apple", "pear", "fig")));
    }

    @Test
    void largestOfAnEmptyListThrows() {
        assertThrows(IllegalArgumentException.class, () -> ListTools.largest(List.<Integer>of()));
    }

    @Test
    void shortestFirstSortsByLengthThenAlphabetically() {
        List<String> words = List.of("rice", "oil", "salt", "sugar", "egg");
        assertEquals(List.of("egg", "oil", "rice", "salt", "sugar"), ListTools.shortestFirst(words));
        assertEquals("rice", words.get(0)); // the original list is untouched
    }
}
