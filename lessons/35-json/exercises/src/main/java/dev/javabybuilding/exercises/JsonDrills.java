package dev.javabybuilding.exercises;

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
        // TODO 1: build the result in a StringBuilder: start with '"', walk through the characters (a for loop over text.toCharArray()),
        // append a backslash plus the right letter for the special ones, the character itself for the others, end with '"'.
        // Do the backslash first or you will escape your own escapes.
        return null;
    }

    /** A product as a JSON object: {"name":"Rice 25kg","price":1250.50,"quantity":12}. The price is written as given (a number, no quotes). */
    public static String productJson(String name, String price, int quantity) {
        // TODO 2: join the pieces with + and use quote(name) for the name.
        return null;
    }

    /** A JSON array from items that are already JSON: arrayOf(List.of("1", "2")) is [1,2]; an empty list gives []. */
    public static String arrayOf(List<String> jsonItems) {
        // TODO 3: "[" + String.join(",", jsonItems) + "]"
        return null;
    }

    /**
     * Are the braces {} and brackets [] balanced? Every opener needs its closer, in the right order. Characters inside a
     * string literal ("...") do not count, and a backslash inside a string protects the next character (\" does not end it).
     * isBalanced("{\"a\":[1,2]}") is true; isBalanced("{\"a\":[1,2}") and isBalanced("{") are false.
     */
    public static boolean isBalanced(String json) {
        // TODO 4: use a java.util.ArrayDeque<Character> as a stack. Keep a boolean "inString" and a boolean "escaped".
        // Outside strings: push '{' and '[' ; on '}' or ']' pop and compare (an empty stack means false). At the end the stack must be empty.
        return false;
    }

    /**
     * The value of a top-level text field in a flat JSON object, or null when it is not there:
     * stringField("{\"name\":\"Rice\",\"qty\":1}", "name") is "Rice". You may assume the value has no escaped quotes.
     */
    public static String stringField(String json, String field) {
        // TODO 5: look for the text "\"" + field + "\":\"" with indexOf; the value runs up to the next double quote.
        return null;
    }
}
