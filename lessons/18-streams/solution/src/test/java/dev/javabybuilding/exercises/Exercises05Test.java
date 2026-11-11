package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class Exercises05Test {

    @Test
    void initialsAreCapitalLettersOfEachWord() {
        assertEquals("JDC", Exercises05.initials("juan dela cruz"));
        assertEquals("A", Exercises05.initials("ana"));
    }

    @Test
    void initialsOfEmptyTextIsEmpty() {
        assertEquals("", Exercises05.initials(""));
        assertEquals("", Exercises05.initials("   "));
    }

    @Test
    void countWordsIgnoresExtraSpaces() {
        assertEquals(3, Exercises05.countWords("  cooking  oil 1L "));
        assertEquals(0, Exercises05.countWords(""));
        assertEquals(1, Exercises05.countWords("rice"));
    }

    @Test
    void validSkuHasThreeLettersADashAndThreeDigits() {
        assertTrue(Exercises05.isValidSku("RIC-001"));
        assertTrue(Exercises05.isValidSku("OIL-942"));
    }

    @Test
    void invalidSkusAreRejected() {
        assertFalse(Exercises05.isValidSku("ric-001"));
        assertFalse(Exercises05.isValidSku("RIC001"));
        assertFalse(Exercises05.isValidSku("RICE-001"));
        assertFalse(Exercises05.isValidSku("RIC-0A1"));
        assertFalse(Exercises05.isValidSku(""));
    }
}
