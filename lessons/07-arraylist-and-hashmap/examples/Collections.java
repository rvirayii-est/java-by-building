// Run with:  java Collections.java
import java.util.ArrayList;
import java.util.HashMap;

public class Collections {
    public static void main(String[] args) {
        // ArrayList: a row of items that can grow and shrink. The <String> says what kind of item it holds.
        ArrayList<String> names = new ArrayList<>();
        names.add("Rice");
        names.add("Oil");
        names.add("Sugar");
        System.out.println("Size: " + names.size());
        System.out.println("First: " + names.get(0));
        System.out.println("Contains Oil? " + names.contains("Oil"));
        names.remove("Oil");
        System.out.println("After remove: " + names);

        for (String name : names) { // "for each" loop: no index needed
            System.out.println("  item: " + name);
        }

        // HashMap: look something up by a key. Here: name -> quantity.
        // Collections hold objects, so we write Integer (the object version of int).
        HashMap<String, Integer> stock = new HashMap<>();
        stock.put("Rice", 12);
        stock.put("Sugar", 0);
        stock.put("Rice", 9); // same key again: the old value is replaced
        System.out.println("Rice: " + stock.get("Rice"));
        System.out.println("Salt: " + stock.get("Salt"));                  // no such key: null
        System.out.println("Salt or 0: " + stock.getOrDefault("Salt", 0)); // a safe default
        System.out.println("Has Sugar? " + stock.containsKey("Sugar"));
        System.out.println("Number of keys: " + stock.size());

        // A HashMap does not promise any order, so we walk the ArrayList of names to get a fixed order.
        for (String name : names) {
            System.out.println(name + " -> " + stock.getOrDefault(name, 0));
        }

        // Counting with a HashMap: how many times was each item sold?
        ArrayList<String> sold = new ArrayList<>();
        sold.add("Rice");
        sold.add("Sugar");
        sold.add("Rice");
        HashMap<String, Integer> counts = new HashMap<>();
        for (String item : sold) {
            counts.put(item, counts.getOrDefault(item, 0) + 1);
        }
        System.out.println("Rice sold " + counts.get("Rice") + " times, Sugar " + counts.get("Sugar") + " time");
    }
}
