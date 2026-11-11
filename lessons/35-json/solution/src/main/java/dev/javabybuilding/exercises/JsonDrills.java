package dev.javabybuilding.exercises;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Lesson 35 drills: write a little JSON BY HAND, with no library, to feel why we use Jackson. Complete each TODO until
 * Exercises35Test is green.
 */
public class JsonDrills {

    /**
     * A JSON string literal: the text in double quotes, with these characters escaped by a backslash: the double quote
     * ("), the backslash (\), and the new line, tab and carriage return written as \n, \t and \r.
     * quote("Rice 25kg") is "Rice 25kg" with the quotes included; quote("say \"hi\"") is "say \"hi\"" in JSON.
     */
    public static String quote(String text) {
        StringBuilder out = new StringBuilder("\"");
        for (char c : text.toCharArray()) {
            switch (c) {
                case '"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\n' -> out.append("\\n");
                case '\t' -> out.append("\\t");
                case '\r' -> out.append("\\r");
                default -> out.append(c);
            }
        }
        return out.append('"').toString();
    }

    /** A product as a JSON object: {"name":"Rice 25kg","price":1250.50,"quantity":12}. The price is written as given (a number, no quotes). */
    public static String productJson(String name, String price, int quantity) {
        return "{\"name\":" + quote(name) + ",\"price\":" + price + ",\"quantity\":" + quantity + "}";
    }

    /** A JSON array from items that are already JSON: arrayOf(List.of("1", "2")) is [1,2]; an empty list gives []. */
    public static String arrayOf(List<String> jsonItems) {
        return "[" + String.join(",", jsonItems) + "]";
    }

    /**
     * Are the braces {} and brackets [] balanced? Every opener needs its closer, in the right order. Characters inside a
     * string literal ("...") do not count, and a backslash inside a string protects the next character (\" does not end it).
     * isBalanced("{\"a\":[1,2]}") is true; isBalanced("{\"a\":[1,2}") and isBalanced("{") are false.
     */
    public static boolean isBalanced(String json) {
        Deque<Character> open = new ArrayDeque<>();
        boolean inString = false;
        boolean escaped = false;
        for (char c : json.toCharArray()) {
            if (inString) {
                if (escaped) {
                    escaped = false;
                } else if (c == '\\') {
                    escaped = true;
                } else if (c == '"') {
                    inString = false;
                }
            } else if (c == '"') {
                inString = true;
            } else if (c == '{' || c == '[') {
                open.push(c);
            } else if (c == '}' || c == ']') {
                if (open.isEmpty()) {
                    return false;
                }
                char opener = open.pop();
                if ((c == '}' && opener != '{') || (c == ']' && opener != '[')) {
                    return false;
                }
            }
        }
        return open.isEmpty() && !inString;
    }

    /**
     * The value of a top-level text field in a flat JSON object, or null when it is not there:
     * stringField("{\"name\":\"Rice\",\"qty\":1}", "name") is "Rice". You may assume the value has no escaped quotes.
     */
    public static String stringField(String json, String field) {
        String marker = "\"" + field + "\":\"";
        int start = json.indexOf(marker);
        if (start < 0) {
            return null;
        }
        int valueStart = start + marker.length();
        return json.substring(valueStart, json.indexOf('"', valueStart));
    }
}
