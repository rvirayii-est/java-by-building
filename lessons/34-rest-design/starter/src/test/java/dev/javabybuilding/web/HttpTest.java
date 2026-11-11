package dev.javabybuilding.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class HttpTest {

    @Test
    void reasonGivesTheStandardWords() {
        assertEquals("OK", Http.reason(200));
        assertEquals("Created", Http.reason(201));
        assertEquals("Not Found", Http.reason(404));
        assertEquals("Method Not Allowed", Http.reason(405));
        assertEquals("Conflict", Http.reason(409));
        assertEquals("Unknown", Http.reason(799));
    }

    @Test
    void segmentsCutsAPathIntoDecodedParts() {
        assertEquals(List.of("products", "Rice 25kg"), Http.segments("/products/Rice%2025kg"));
        assertEquals(List.of("products"), Http.segments("/products/"));
        assertEquals(List.of("a", "b"), Http.segments("//a///b"));
    }

    @Test
    void segmentsOfTheRootAreEmpty() {
        assertTrue(Http.segments("/").isEmpty());
        assertTrue(Http.segments("").isEmpty());
    }

    @Test
    void aPlusInAPathIsARealPlus() {
        assertEquals(List.of("products", "A+B"), Http.segments("/products/A+B"));
    }

    @Test
    void queryReadsPairsInOrder() {
        Map<String, String> values = Http.query("status=LOW&name=Rice%2025kg");
        assertEquals(List.of("status", "name"), List.copyOf(values.keySet()));
        assertEquals("LOW", values.get("status"));
        assertEquals("Rice 25kg", values.get("name"));
    }

    @Test
    void aPlusInAQueryIsASpace() {
        assertEquals("Rice 25kg", Http.query("product=Rice+25kg").get("product"));
    }

    @Test
    void queryHandlesNothingAndKeysWithoutValues() {
        assertTrue(Http.query(null).isEmpty());
        assertTrue(Http.query("").isEmpty());
        assertEquals("", Http.query("verbose").get("verbose"));
    }

    @Test
    void queryKeepsAnEqualsSignInsideTheValue() {
        assertEquals("a=b", Http.query("x=a=b").get("x"));
    }
}
