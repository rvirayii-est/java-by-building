package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TextUtilTest {

    @Test
    void cleanRemovesSpacesAtBothEnds() {
        assertEquals("Rice", TextUtil.clean("  Rice  "));
    }

    @Test
    void cleanSqueezesSpacesBetweenWords() {
        assertEquals("Rice 25kg", TextUtil.clean("  Rice   25kg "));
    }

    @Test
    void cleanOfOnlySpacesIsEmpty() {
        assertEquals("", TextUtil.clean("     "));
    }

    @Test
    void titleCaseFixesCapitals() {
        assertEquals("Cooking Oil 1l", TextUtil.titleCase("cooking OIL 1L"));
    }

    @Test
    void titleCaseAlsoCleansSpaces() {
        assertEquals("Rice 25kg", TextUtil.titleCase("  rice   25KG "));
    }

    @Test
    void sameNameIgnoresCapitalsAndExtraSpaces() {
        assertTrue(TextUtil.sameName("Sugar 1kg", "  sUGAr   1KG "));
        assertFalse(TextUtil.sameName("Sugar 1kg", "Sugar 2kg"));
    }

    @Test
    void matchesFindsTextInsideAName() {
        assertTrue(TextUtil.matches("Cooking Oil 1L", " OIL "));
        assertFalse(TextUtil.matches("Rice 25kg", "oil"));
    }

    @Test
    void anEmptyQueryMatchesNothing() {
        assertFalse(TextUtil.matches("Rice 25kg", ""));
        assertFalse(TextUtil.matches("Rice 25kg", "   "));
    }
}
