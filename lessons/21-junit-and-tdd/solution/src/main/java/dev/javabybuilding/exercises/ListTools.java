package dev.javabybuilding.exercises;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/** Lesson 17, exercise 2: generic methods and collections. */
public class ListTools {

    /** The items without repeats, in the order each was first seen: [a, b, a, c, b] gives [a, b, c]. */
    public static <T> List<T> distinct(List<T> items) {
        return new ArrayList<>(new LinkedHashSet<>(items)); // a Set drops repeats; LinkedHashSet keeps the order
    }

    /** How many times each item appears: [a, b, a] gives {a=2, b=1} (in first-seen order). */
    public static <T> Map<T, Integer> countAll(List<T> items) {
        Map<T, Integer> counts = new LinkedHashMap<>();
        for (T item : items) {
            counts.merge(item, 1, Integer::sum);
        }
        return counts;
    }

    /**
     * The biggest item. "T extends Comparable<T>" means T must know how to compare itself (String, Integer, ...).
     *
     * @throws IllegalArgumentException when the list is empty
     */
    public static <T extends Comparable<T>> T largest(List<T> items) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("The list is empty.");
        }
        T best = items.get(0);
        for (T item : items) {
            if (item.compareTo(best) > 0) {
                best = item;
            }
        }
        return best;
    }

    /** The words shortest first; words of the same length are sorted A to Z. The original list is not changed. */
    public static List<String> shortestFirst(List<String> words) {
        List<String> sorted = new ArrayList<>(words);
        sorted.sort(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder()));
        return sorted;
    }
}
