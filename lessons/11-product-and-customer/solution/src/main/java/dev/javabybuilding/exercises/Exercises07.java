package dev.javabybuilding.exercises;

import java.util.ArrayList;
import java.util.HashMap;

public class Exercises07 {

    /** A NEW list without the blank items (empty, or only spaces). removeBlank(["Rice", "  ", "", "Oil"]) is ["Rice", "Oil"]. */
    public static ArrayList<String> removeBlank(ArrayList<String> items) {
        ArrayList<String> result = new ArrayList<>();
        for (String item : items) {
            if (!item.trim().isEmpty()) {
                result.add(item);
            }
        }
        return result;
    }

    /** Counts how many times each item appears. countOccurrences(["Rice", "Oil", "Rice"]) is {"Rice"=2, "Oil"=1}. */
    public static HashMap<String, Integer> countOccurrences(ArrayList<String> items) {
        HashMap<String, Integer> counts = new HashMap<>();
        for (String item : items) {
            counts.put(item, counts.getOrDefault(item, 0) + 1);
        }
        return counts;
    }

    /** The item that appears most often (ties: the one that appears first), or null for an empty list. */
    public static String mostCommon(ArrayList<String> items) {
        HashMap<String, Integer> counts = countOccurrences(items);
        String best = null;
        for (String item : items) { // walking the LIST (not the map) keeps the first-appearance order
            if (best == null || counts.get(item) > counts.get(best)) {
                best = item;
            }
        }
        return best;
    }
}
