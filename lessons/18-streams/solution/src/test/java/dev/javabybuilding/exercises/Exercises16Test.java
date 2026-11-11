package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class Exercises16Test {

    private final List<String> typed = List.of("25", "abc", " 40 ", "-3", "130", "");

    @Test
    void aValidAgeIsParsed() {
        assertEquals(25, Ages.parseAge("25"));
        assertEquals(40, Ages.parseAge("  40 "));
        assertEquals(0, Ages.parseAge("0"));
        assertEquals(120, Ages.parseAge("120"));
    }

    @Test
    void textThatIsNotANumberThrowsWithTheOriginalProblemAsTheCause() {
        InvalidAgeException e = assertThrows(InvalidAgeException.class, () -> Ages.parseAge("abc"));
        assertEquals("Age must be a number: abc", e.getMessage());
        assertInstanceOf(NumberFormatException.class, e.getCause());
    }

    @Test
    void anAgeOutsideZeroToOneTwentyThrows() {
        InvalidAgeException tooBig = assertThrows(InvalidAgeException.class, () -> Ages.parseAge("130"));
        assertEquals("Age must be from 0 to 120: 130", tooBig.getMessage());
        assertThrows(InvalidAgeException.class, () -> Ages.parseAge("-3"));
    }

    @Test
    void parseAgeOrDefaultNeverThrows() {
        assertEquals(25, Ages.parseAgeOrDefault("25", -1));
        assertEquals(-1, Ages.parseAgeOrDefault("abc", -1));
        assertEquals(18, Ages.parseAgeOrDefault("200", 18));
    }

    @Test
    void countInvalidCountsTheBadTexts() {
        assertEquals(4, Ages.countInvalid(typed)); // "abc", "-3", "130" and ""
        assertEquals(0, Ages.countInvalid(List.of()));
    }

    @Test
    void sumValidSkipsTheBadTextsAndKeepsGoing() {
        assertEquals(65, Ages.sumValid(typed)); // 25 + 40
    }
}
