package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class Exercises07Test {

    private static ArrayList<String> list(String... items) {
        return new ArrayList<>(List.of(items));
    }

    @Test
    void removeBlankKeepsOnlyRealItemsInOrder() {
        assertEquals(list("Rice", "Oil"), Exercises07.removeBlank(list("Rice", "  ", "", "Oil")));
    }

    @Test
    void removeBlankDoesNotChangeTheOriginal() {
        ArrayList<String> original = list("Rice", "");
        Exercises07.removeBlank(original);
        assertEquals(2, original.size());
    }

    @Test
    void countOccurrencesCountsEachItem() {
        var counts = Exercises07.countOccurrences(list("Rice", "Oil", "Rice"));
        assertEquals(2, counts.size());
        assertEquals(2, counts.get("Rice"));
        assertEquals(1, counts.get("Oil"));
    }

    @Test
    void countOccurrencesOfAnEmptyListIsEmpty() {
        assertEquals(0, Exercises07.countOccurrences(list()).size());
    }

    @Test
    void mostCommonFindsTheWinner() {
        assertEquals("Rice", Exercises07.mostCommon(list("Oil", "Rice", "Rice")));
    }

    @Test
    void mostCommonBreaksTiesByFirstAppearance() {
        assertEquals("Oil", Exercises07.mostCommon(list("Oil", "Rice", "Rice", "Oil")));
    }

    @Test
    void mostCommonOfAnEmptyListIsNull() {
        assertNull(Exercises07.mostCommon(list()));
    }
}
