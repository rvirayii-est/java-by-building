package dev.javabybuilding.inventory;

/** Helpers for cleaning and comparing the text people type. */
public class TextUtil {

    /** Removes spaces at both ends and squeezes spaces between words to one: "  Rice   25kg " becomes "Rice 25kg". */
    public static String clean(String text) {
        StringBuilder result = new StringBuilder();
        for (String word : text.trim().split(" ")) {
            if (!word.isEmpty()) {
                if (result.length() > 0) {
                    result.append(' ');
                }
                result.append(word);
            }
        }
        return result.toString();
    }

    /** Capital letter first, the rest small, for every word: "cooking OIL 1L" becomes "Cooking Oil 1l". */
    public static String titleCase(String text) {
        StringBuilder result = new StringBuilder();
        for (String word : clean(text).split(" ")) {
            if (!word.isEmpty()) {
                if (result.length() > 0) {
                    result.append(' ');
                }
                result.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    /** True when both texts are the same after cleaning, ignoring capital letters. */
    public static boolean sameName(String a, String b) {
        return clean(a).equalsIgnoreCase(clean(b));
    }

    /** True when query (cleaned, ignoring capitals) appears anywhere inside name. An empty query matches nothing. */
    public static boolean matches(String name, String query) {
        String wanted = clean(query).toLowerCase();
        if (wanted.isEmpty()) {
            return false;
        }
        return name.toLowerCase().contains(wanted);
    }
}
