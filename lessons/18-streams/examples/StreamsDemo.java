// Run with:  java StreamsDemo.java
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class StreamsDemo {

    record Item(String name, int quantity, int priceCentavos) {
        int value() {
            return quantity * priceCentavos;
        }
    }

    public static void main(String[] args) {
        List<Item> items = List.of(
                new Item("Rice 25kg", 12, 125050),
                new Item("Cooking Oil 1L", 4, 9800),
                new Item("Sugar 1kg", 0, 7500),
                new Item("Sardines 155g", 60, 2400));

        // 1. The same question with a loop and with a stream.
        int loopTotal = 0;
        for (Item item : items) {
            loopTotal += item.value();
        }
        int streamTotal = items.stream().mapToInt(Item::value).sum();
        System.out.println("Loop total:   " + loopTotal);
        System.out.println("Stream total: " + streamTotal);

        // 2. filter keeps some items; map changes each item into something else.
        List<String> inStock = items.stream()
                .filter(item -> item.quantity() > 0)
                .map(Item::name)
                .toList();
        System.out.println("In stock: " + inStock);

        // 3. sorted + limit: the two most valuable items.
        List<String> top2 = items.stream()
                .sorted(Comparator.comparingInt(Item::value).reversed())
                .limit(2)
                .map(Item::name)
                .toList();
        System.out.println("Top 2 by value: " + top2);

        // 4. Collectors: group, count and join.
        Map<Boolean, Long> counts = items.stream().collect(Collectors.partitioningBy(item -> item.quantity() > 10, Collectors.counting()));
        System.out.println("More than 10 units: " + counts.get(true) + ", 10 or fewer: " + counts.get(false));
        Map<Integer, List<String>> byLength = items.stream()
                .collect(Collectors.groupingBy(item -> item.name().length(), TreeMap::new, Collectors.mapping(Item::name, Collectors.toList())));
        System.out.println("By name length: " + byLength);
        System.out.println("Joined: " + items.stream().map(Item::name).collect(Collectors.joining(" | ")));

        // 5. Optional: an answer that might not exist.
        Optional<Item> expensive = items.stream().filter(item -> item.priceCentavos() > 1_000_000).findFirst();
        System.out.println("Over PHP 10,000? " + expensive.map(Item::name).orElse("nothing"));
        System.out.println("Most stock: " + items.stream().max(Comparator.comparingInt(Item::quantity)).map(Item::name).orElse("?"));

        // 6. Streams do not change the list.
        System.out.println("Original size: " + items.size());
    }
}
