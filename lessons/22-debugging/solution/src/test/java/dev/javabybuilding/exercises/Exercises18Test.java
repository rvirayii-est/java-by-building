package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class Exercises18Test {

    private final List<String> words = List.of("rice", "to", "oil", "in", "sugar", "oil");

    @Test
    void shoutKeepsLongWordsAndMakesThemCapitals() {
        assertEquals(List.of("RICE", "SUGAR"), StreamDrills.shout(words, 4));
        assertEquals(List.of(), StreamDrills.shout(words, 99));
    }

    @Test
    void sumOfSquaresOfOddsIgnoresEvenNumbers() {
        assertEquals(10, StreamDrills.sumOfSquaresOfOdds(List.of(1, 2, 3, 4)));
        assertEquals(0, StreamDrills.sumOfSquaresOfOdds(List.of(2, 4)));
        assertEquals(0, StreamDrills.sumOfSquaresOfOdds(List.of()));
    }

    @Test
    void groupByLengthPutsWordsOfTheSameLengthTogether() {
        Map<Integer, List<String>> groups = StreamDrills.groupByLength(words);
        assertEquals(List.of("to", "in"), groups.get(2));
        assertEquals(List.of("oil", "oil"), groups.get(3));
        assertEquals(List.of("rice"), groups.get(4));
        assertEquals(List.of("sugar"), groups.get(5));
        assertEquals(4, groups.size());
    }

    @Test
    void longestWordIsAnOptionalAndTheFirstWinsATie() {
        assertEquals("sugar", StreamDrills.longestWord(words).orElseThrow());
        assertEquals("aa", StreamDrills.longestWord(List.of("aa", "bb")).orElseThrow());
        assertTrue(StreamDrills.longestWord(List.of()).isEmpty());
    }

    @Test
    void joinSortedRemovesRepeatsSortsAndJoins() {
        assertEquals("in|oil|rice|sugar|to", StreamDrills.joinSorted(words));
        assertEquals("", StreamDrills.joinSorted(List.of()));
    }

    @Test
    void averageLengthIsZeroForNoWords() {
        assertEquals(19.0 / 6, StreamDrills.averageLength(words), 0.0001); // (4 + 2 + 3 + 2 + 5 + 3) / 6
        assertEquals(0.0, StreamDrills.averageLength(List.of()));
    }
}
