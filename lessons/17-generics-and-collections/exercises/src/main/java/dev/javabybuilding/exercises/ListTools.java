package dev.javabybuilding.exercises;

import java.util.List;
import java.util.Map;

/** Lesson 17, exercise 2: generic methods and collections. */
public class ListTools {

    /**
     * TODO: the items without repeats, in the order each was first seen: [a, b, a, c, b] gives [a, b, c].
     * Hint: a LinkedHashSet drops repeats and remembers order; new ArrayList<>(thatSet) turns it back into a list.
     */
    public static <T> List<T> distinct(List<T> items) {
        return null;
    }

    /**
     * TODO: how many times each item appears: [a, b, a] gives {a=2, b=1}, in first-seen order.
     * Hint: a LinkedHashMap, and map.merge(item, 1, Integer::sum).
     */
    public static <T> Map<T, Integer> countAll(List<T> items) {
        return null;
    }

    /**
     * TODO: the biggest item (use item.compareTo(best) > 0). "T extends Comparable<T>" lets you call compareTo.
     * Throw new IllegalArgumentException("The list is empty.") for an empty list.
     */
    public static <T extends Comparable<T>> T largest(List<T> items) {
        return null;
    }

    /**
     * TODO: a NEW list with the words shortest first; words of the same length go A to Z. The original must not change.
     * Hint: copy the list, then sort(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder())).
     */
    public static List<String> shortestFirst(List<String> words) {
        return null;
    }
}
