package dev.javabybuilding.inventory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Counts things of any one type: names, customers, payment methods... The T is a type parameter: a placeholder that
 * the user of the class fills in. Counter<String> counts Strings; Counter<PaymentMethod> counts payment methods.
 * One class, written once, checked by the compiler for every type.
 */
public class Counter<T> {

    private final Map<T, Integer> counts = new LinkedHashMap<>(); // remembers the order things were first counted

    /** Counts one more of this item. */
    public void add(T item) {
        add(item, 1);
    }

    /** Counts amount more of this item. */
    public void add(T item, int amount) {
        counts.merge(item, amount, Integer::sum); // new item: store amount; seen before: add amount to what is there
    }

    /** How many of this item were counted (0 when never counted). */
    public int count(T item) {
        return counts.getOrDefault(item, 0);
    }

    /** All the counts added together. */
    public int total() {
        int total = 0;
        for (int count : counts.values()) {
            total += count;
        }
        return total;
    }

    /** The n items with the biggest counts, biggest first. Items with equal counts keep the order they were first counted. */
    public List<T> top(int n) {
        List<Map.Entry<T, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((a, b) -> b.getValue() - a.getValue()); // sort is stable: equal counts stay in first-counted order
        List<T> result = new ArrayList<>();
        for (int i = 0; i < n && i < entries.size(); i++) {
            result.add(entries.get(i).getKey());
        }
        return result;
    }

    /** A copy of the counts, so the caller cannot change this Counter by accident. */
    public Map<T, Integer> asMap() {
        return new LinkedHashMap<>(counts);
    }
}
