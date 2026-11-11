// Run with:  java GenericsDemo.java
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class GenericsDemo {

    /** A generic class: T is filled in by whoever uses it. */
    static class Box<T> {
        private final T item;

        Box(T item) {
            this.item = item;
        }

        T get() {
            return item;
        }
    }

    /** A generic method: works for any type that can compare itself. */
    static <T extends Comparable<T>> T biggest(List<T> items) {
        T best = items.get(0);
        for (T item : items) {
            if (item.compareTo(best) > 0) {
                best = item;
            }
        }
        return best;
    }

    record Item(String name, int quantity) {}

    static List<String> names(List<Item> items) {
        List<String> names = new ArrayList<>();
        for (Item item : items) {
            names.add(item.name());
        }
        return names;
    }

    public static void main(String[] args) {
        // 1. Generics: the type in <> is checked by the compiler.
        Box<String> label = new Box<>("Rice 25kg");
        Box<Integer> count = new Box<>(12);
        System.out.println(label.get().toUpperCase() + " x " + (count.get() + 1)); // no casting needed
        System.out.println(biggest(List.of(12, 40, 7)) + " " + biggest(List.of("fig", "pear", "apple")));

        // 2. List: ordered, repeats allowed.
        List<String> names = new ArrayList<>(List.of("sugar", "rice", "oil", "rice"));
        System.out.println("List: " + names + " size " + names.size());

        // 3. Set: no repeats.
        Set<String> unique = new HashSet<>(names);
        System.out.println("Set size: " + unique.size());

        // 4. Map: look a value up by its key. LinkedHashMap keeps the order things were put in.
        Map<String, Integer> stock = new LinkedHashMap<>();
        stock.put("rice", 12);
        stock.put("oil", 4);
        stock.merge("rice", 8, Integer::sum); // rice was 12, now 20
        System.out.println("Map: " + stock + ", rice is " + stock.get("rice") + ", salt is " + stock.getOrDefault("salt", 0));

        // 5. TreeMap keeps its keys sorted.
        System.out.println("Sorted keys: " + new TreeMap<>(stock).keySet());

        // 6. Comparator: how to sort things that have no natural order.
        List<Item> items = new ArrayList<>(List.of(new Item("rice", 12), new Item("oil", 4), new Item("sardines", 60)));
        items.sort(Comparator.comparingInt(Item::quantity));
        System.out.println("By quantity: " + names(items));
        items.sort(Comparator.comparing(Item::name).reversed());
        System.out.println("Z to A: " + names(items));
    }
}
