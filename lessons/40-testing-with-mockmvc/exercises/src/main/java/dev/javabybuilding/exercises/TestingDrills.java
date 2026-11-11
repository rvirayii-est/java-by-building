package dev.javabybuilding.exercises;

import java.util.List;
import java.util.Map;

/**
 * Lesson 40 drills: small helpers of the kind every API test suite grows (a mini JSON path, readable failure messages, test data
 * with defaults), written by hand so you know what {@code jsonPath("$.problems[0].field")} does. Complete each TODO until
 * Exercises40Test is green.
 */
public class TestingDrills {

    /**
     * Reads a value out of parsed JSON (a Map for an object, a List for an array, or text, a number, a boolean, null) with a path like
     * MockMvc's jsonPath: "$" is the whole thing, ".name" takes a field of an object, "[2]" takes an item of an array. They chain:
     * jsonPath(json, "$.problems[1].field").
     *
     * @throws IllegalArgumentException "No value at <path>" when a field or an index does not exist (a field that exists with the
     *         value null is fine and gives null); "Bad path" when the path does not start with $
     */
    public static Object jsonPath(Object json, String path) {
        // TODO 1: check path.startsWith("$"). Walk along the rest with an index: on '.' read the field name up to the next '.' or '['
        // and take it from the current Map (containsKey, else throw); on '[' read the number up to ']' and take it from the current List
        // (check the range, else throw). The current object must be a Map for '.' and a List for '[', else throw too.
        return null;
    }

    /**
     * A message for a failing status check, the kind that tells you at once what happened: "OK" when both are the same, otherwise
     * "Expected status 201 (Created) but was 404 (Not Found)". Words: 200 OK, 201 Created, 204 No Content, 400 Bad Request,
     * 404 Not Found, 405 Method Not Allowed, 409 Conflict, 415 Unsupported Media Type, 500 Internal Server Error; any other number "Unknown".
     */
    public static String statusMessage(int expected, int actual) {
        // TODO 2: a helper switch that gives the words for a status, then compare.
        return null;
    }

    /** The names from required that are NOT keys of the JSON object, in the order of required. A key with a null value is present. */
    public static List<String> missingFields(Map<String, Object> json, List<String> required) {
        // TODO 3: required.stream().filter(name -> !json.containsKey(name)).toList()
        return null;
    }

    /** Are the rows in non-decreasing order of the text in their field? An empty list or one row is sorted. Values are Strings. */
    public static boolean isSortedBy(List<Map<String, Object>> rows, String field) {
        // TODO 4: compare each row's value with the previous one using compareTo.
        return false;
    }

    /**
     * Test data with defaults, so a test only writes what it is about: fixtureJson(null, null, null) is
     * {"name":"Test Product","price":10.00,"reorderLevel":5}; a null argument means "use the default".
     * fixtureJson("Salt", "20.50", 2) is {"name":"Salt","price":20.50,"reorderLevel":2}. The price is written as given (a number).
     */
    public static String fixtureJson(String name, String price, Integer reorderLevel) {
        // TODO 5: pick each default with name == null ? "Test Product" : name, and so on, then join the pieces.
        return null;
    }
}
