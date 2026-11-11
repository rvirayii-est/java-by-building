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
        if (!path.startsWith("$")) {
            throw new IllegalArgumentException("Bad path: " + path);
        }
        Object current = json;
        int i = 1;
        while (i < path.length()) {
            char c = path.charAt(i);
            if (c == '.') {
                int end = i + 1;
                while (end < path.length() && path.charAt(end) != '.' && path.charAt(end) != '[') {
                    end++;
                }
                String field = path.substring(i + 1, end);
                if (!(current instanceof Map<?, ?> map) || !map.containsKey(field)) {
                    throw new IllegalArgumentException("No value at " + path);
                }
                current = map.get(field);
                i = end;
            } else if (c == '[') {
                int end = path.indexOf(']', i);
                if (end < 0) {
                    throw new IllegalArgumentException("Bad path: " + path);
                }
                int index;
                try {
                    index = Integer.parseInt(path.substring(i + 1, end));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Bad path: " + path);
                }
                if (!(current instanceof List<?> list) || index < 0 || index >= list.size()) {
                    throw new IllegalArgumentException("No value at " + path);
                }
                current = list.get(index);
                i = end + 1;
            } else {
                throw new IllegalArgumentException("Bad path: " + path);
            }
        }
        return current;
    }

    /**
     * A message for a failing status check, the kind that tells you at once what happened: "OK" when both are the same, otherwise
     * "Expected status 201 (Created) but was 404 (Not Found)". Words: 200 OK, 201 Created, 204 No Content, 400 Bad Request,
     * 404 Not Found, 405 Method Not Allowed, 409 Conflict, 415 Unsupported Media Type, 500 Internal Server Error; any other number "Unknown".
     */
    public static String statusMessage(int expected, int actual) {
        if (expected == actual) {
            return "OK";
        }
        return "Expected status " + expected + " (" + words(expected) + ") but was " + actual + " (" + words(actual) + ")";
    }

    private static String words(int status) {
        return switch (status) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 204 -> "No Content";
            case 400 -> "Bad Request";
            case 404 -> "Not Found";
            case 405 -> "Method Not Allowed";
            case 409 -> "Conflict";
            case 415 -> "Unsupported Media Type";
            case 500 -> "Internal Server Error";
            default -> "Unknown";
        };
    }

    /** The names from required that are NOT keys of the JSON object, in the order of required. A key with a null value is present. */
    public static List<String> missingFields(Map<String, Object> json, List<String> required) {
        return required.stream().filter(name -> !json.containsKey(name)).toList();
    }

    /** Are the rows in non-decreasing order of the text in their field? An empty list or one row is sorted. Values are Strings. */
    public static boolean isSortedBy(List<Map<String, Object>> rows, String field) {
        for (int i = 1; i < rows.size(); i++) {
            String previous = (String) rows.get(i - 1).get(field);
            String current = (String) rows.get(i).get(field);
            if (previous.compareTo(current) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Test data with defaults, so a test only writes what it is about: fixtureJson(null, null, null) is
     * {"name":"Test Product","price":10.00,"reorderLevel":5}; a null argument means "use the default".
     * fixtureJson("Salt", "20.50", 2) is {"name":"Salt","price":20.50,"reorderLevel":2}. The price is written as given (a number).
     */
    public static String fixtureJson(String name, String price, Integer reorderLevel) {
        return "{\"name\":\"" + (name == null ? "Test Product" : name) + "\",\"price\":" + (price == null ? "10.00" : price)
                + ",\"reorderLevel\":" + (reorderLevel == null ? 5 : reorderLevel) + "}";
    }
}
