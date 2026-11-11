package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class Exercises08Test {

    private final String[] options = {"Add product", "Record sale", "Quit"};

    @Test
    void numberedMenuHasOneLinePerOption() {
        assertEquals("1. Add product\n2. Record sale\n3. Quit", Exercises08.numberedMenu(options));
    }

    @Test
    void numberedMenuOfNoOptionsIsEmpty() {
        assertEquals("", Exercises08.numberedMenu(new String[0]));
    }

    @Test
    void optionAtCountsFromOne() {
        assertEquals("Add product", Exercises08.optionAt(options, 1));
        assertEquals("Quit", Exercises08.optionAt(options, 3));
    }

    @Test
    void optionAtRejectsNumbersOutsideTheMenu() {
        assertEquals("Invalid choice", Exercises08.optionAt(options, 0));
        assertEquals("Invalid choice", Exercises08.optionAt(options, 4));
        assertEquals("Invalid choice", Exercises08.optionAt(options, -1));
    }

    @Test
    void quitCommands() {
        assertTrue(Exercises08.isQuitCommand("q"));
        assertTrue(Exercises08.isQuitCommand(" QUIT "));
        assertTrue(Exercises08.isQuitCommand("Exit"));
    }

    @Test
    void otherTextIsNotAQuitCommand() {
        assertFalse(Exercises08.isQuitCommand("quitting"));
        assertFalse(Exercises08.isQuitCommand(""));
        assertFalse(Exercises08.isQuitCommand("3"));
    }
}
