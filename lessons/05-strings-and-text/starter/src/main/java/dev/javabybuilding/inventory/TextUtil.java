package dev.javabybuilding.inventory;

/** Helpers for cleaning and comparing the text people type. */
public class TextUtil {

    /**
     * TODO 1: remove spaces at both ends and squeeze spaces between words to one.
     * clean("  Rice   25kg ") returns "Rice 25kg".
     * Hint: text.trim().split(" ") gives the words (extra spaces show up as empty words: skip those with isEmpty()).
     * Join the words with a single space using a StringBuilder.
     */
    public static String clean(String text) {
        return text;
    }

    /**
     * TODO 2: capital letter first, the rest small, for every word.
     * titleCase("cooking OIL 1L") returns "Cooking Oil 1l".
     * Hint: word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase(). Start from clean(text).
     */
    public static String titleCase(String text) {
        return text;
    }

    /** TODO 3: true when both texts are the same after cleaning, ignoring capital letters (equalsIgnoreCase). */
    public static boolean sameName(String a, String b) {
        return false;
    }

    /**
     * TODO 4: true when query (cleaned, ignoring capitals) appears anywhere inside name.
     * An empty query matches nothing. matches("Cooking Oil 1L", " OIL ") returns true.
     * Hint: toLowerCase() on both, then name.contains(...).
     */
    public static boolean matches(String name, String query) {
        return false;
    }
}
