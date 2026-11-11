package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class Exercises40Test {

    private static Map<String, Object> answer() {
        Map<String, Object> nothing = new HashMap<>();
        nothing.put("note", null);
        return Map.of(
                "status", 400,
                "message", "The request has invalid fields.",
                "problems", List.of(Map.of("field", "name", "message", "is required"), Map.of("field", "price", "message", "must be at least 0.01")),
                "extra", nothing);
    }

    @Test
    void jsonPathReadsFieldsAndIndexes() {
        assertEquals(400, TestingDrills.jsonPath(answer(), "$.status"));
        assertEquals("The request has invalid fields.", TestingDrills.jsonPath(answer(), "$.message"));
        assertEquals("price", TestingDrills.jsonPath(answer(), "$.problems[1].field"));
        assertEquals("is required", TestingDrills.jsonPath(answer(), "$.problems[0].message"));
    }

    @Test
    void jsonPathDollarIsTheWholeThingAndAnArrayCanBeTheRoot() {
        Map<String, Object> whole = answer();
        assertEquals(whole, TestingDrills.jsonPath(whole, "$"));
        assertEquals("b", TestingDrills.jsonPath(List.of("a", "b"), "$[1]"));
    }

    @Test
    void aFieldWithTheValueNullIsFine() {
        assertNull(TestingDrills.jsonPath(answer(), "$.extra.note"));
    }

    @Test
    void jsonPathRefusesMissingThingsAndBadPaths() {
        IllegalArgumentException missing = assertThrows(IllegalArgumentException.class, () -> TestingDrills.jsonPath(answer(), "$.nothing"));
        assertEquals("No value at $.nothing", missing.getMessage());
        assertThrows(IllegalArgumentException.class, () -> TestingDrills.jsonPath(answer(), "$.problems[2].field"));
        assertThrows(IllegalArgumentException.class, () -> TestingDrills.jsonPath(answer(), "$.status.deeper"));
        assertThrows(IllegalArgumentException.class, () -> TestingDrills.jsonPath(answer(), "$.message[0]"));
        assertThrows(IllegalArgumentException.class, () -> TestingDrills.jsonPath(answer(), "status"));
        assertThrows(IllegalArgumentException.class, () -> TestingDrills.jsonPath(answer(), "$.problems[x]"));
    }

    @Test
    void statusMessageSaysOkOrExplainsTheDifference() {
        assertEquals("OK", TestingDrills.statusMessage(201, 201));
        assertEquals("Expected status 201 (Created) but was 404 (Not Found)", TestingDrills.statusMessage(201, 404));
        assertEquals("Expected status 409 (Conflict) but was 500 (Internal Server Error)", TestingDrills.statusMessage(409, 500));
        assertEquals("Expected status 200 (OK) but was 418 (Unknown)", TestingDrills.statusMessage(200, 418));
    }

    @Test
    void missingFieldsListsWhatIsNotThereInTheOrderAsked() {
        Map<String, Object> json = new HashMap<>();
        json.put("status", 404);
        json.put("gift", null);
        assertEquals(List.of("message", "path"), TestingDrills.missingFields(json, List.of("status", "message", "gift", "path")));
        assertEquals(List.of(), TestingDrills.missingFields(json, List.of("status", "gift")));
        assertEquals(List.of(), TestingDrills.missingFields(json, List.of()));
    }

    @Test
    void isSortedByChecksTheOrderOfOneField() {
        assertTrue(TestingDrills.isSortedBy(List.of(Map.of("field", "customerPhone"), Map.of("field", "discount"), Map.of("field", "quantity")), "field"));
        assertTrue(TestingDrills.isSortedBy(List.of(Map.of("field", "a"), Map.of("field", "a")), "field"));
        assertFalse(TestingDrills.isSortedBy(List.of(Map.of("field", "price"), Map.of("field", "name")), "field"));
        assertTrue(TestingDrills.isSortedBy(List.of(), "field"));
        assertTrue(TestingDrills.isSortedBy(List.of(Map.of("field", "x")), "field"));
    }

    @Test
    void fixtureJsonUsesDefaultsForNulls() {
        assertEquals("{\"name\":\"Test Product\",\"price\":10.00,\"reorderLevel\":5}", TestingDrills.fixtureJson(null, null, null));
        assertEquals("{\"name\":\"Salt\",\"price\":20.50,\"reorderLevel\":2}", TestingDrills.fixtureJson("Salt", "20.50", 2));
        assertEquals("{\"name\":\"Test Product\",\"price\":0,\"reorderLevel\":0}", TestingDrills.fixtureJson(null, "0", 0));
    }
}
