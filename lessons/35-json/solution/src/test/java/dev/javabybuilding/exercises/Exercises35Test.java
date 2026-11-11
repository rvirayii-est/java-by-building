package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class Exercises35Test {

    @Test
    void quoteWrapsTextInDoubleQuotes() {
        assertEquals("\"Rice 25kg\"", JsonDrills.quote("Rice 25kg"));
        assertEquals("\"\"", JsonDrills.quote(""));
    }

    @Test
    void quoteEscapesQuotesBackslashesAndControlCharacters() {
        assertEquals("\"say \\\"hi\\\"\"", JsonDrills.quote("say \"hi\""));
        assertEquals("\"C:\\\\shop\"", JsonDrills.quote("C:\\shop"));
        assertEquals("\"line1\\nline2\\ttab\\r\"", JsonDrills.quote("line1\nline2\ttab\r"));
    }

    @Test
    void productJsonMakesAnObject() {
        assertEquals("{\"name\":\"Rice 25kg\",\"price\":1250.50,\"quantity\":12}", JsonDrills.productJson("Rice 25kg", "1250.50", 12));
        assertEquals("{\"name\":\"5\\\" nails\",\"price\":3.00,\"quantity\":0}", JsonDrills.productJson("5\" nails", "3.00", 0));
    }

    @Test
    void arrayOfJoinsItemsWithCommas() {
        assertEquals("[1,2,3]", JsonDrills.arrayOf(List.of("1", "2", "3")));
        assertEquals("[]", JsonDrills.arrayOf(List.of()));
        assertEquals("[{\"a\":1}]", JsonDrills.arrayOf(List.of("{\"a\":1}")));
    }

    @Test
    void balancedJsonHasEveryOpenerClosed() {
        assertTrue(JsonDrills.isBalanced("{\"a\":[1,2]}"));
        assertTrue(JsonDrills.isBalanced("[]"));
        assertTrue(JsonDrills.isBalanced(""));
        assertFalse(JsonDrills.isBalanced("{\"a\":[1,2}"));
        assertFalse(JsonDrills.isBalanced("{"));
        assertFalse(JsonDrills.isBalanced("]"));
        assertFalse(JsonDrills.isBalanced("[{]}"));
    }

    @Test
    void bracketsInsideStringsDoNotCount() {
        assertTrue(JsonDrills.isBalanced("{\"name\":\"a } b ] c\"}"));
        assertTrue(JsonDrills.isBalanced("{\"name\":\"say \\\"{\\\"\"}")); // the quoted text is: say "{"
        assertFalse(JsonDrills.isBalanced("{\"name\":\"a\""));
    }

    @Test
    void stringFieldFindsAFlatTextValue() {
        assertEquals("Rice", JsonDrills.stringField("{\"name\":\"Rice\",\"qty\":1}", "name"));
        assertEquals("GCASH", JsonDrills.stringField("{\"qty\":1,\"payment\":\"GCASH\"}", "payment"));
        assertEquals("", JsonDrills.stringField("{\"note\":\"\"}", "note"));
    }

    @Test
    void stringFieldIsNullWhenThereIsNoSuchTextField() {
        assertNull(JsonDrills.stringField("{\"qty\":1}", "name"));
        assertNull(JsonDrills.stringField("{\"qty\":1}", "qty")); // a number, not a text field
    }
}
