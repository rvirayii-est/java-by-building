package dev.javabybuilding.inventory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The shop's stock, kept in an ArrayList (the order products were added) and HashMaps (look up by name).
 * One Inventory object remembers its own data; create one with new Inventory(). Classes are explained in week 3.
 * Money is whole centavos (int), as always.
 */
public class Inventory {

    private final ArrayList<String> names = new ArrayList<>();
    private final HashMap<String, Integer> prices = new HashMap<>();
    private final HashMap<String, Integer> quantities = new HashMap<>();
    private final HashMap<String, Integer> reorderLevels = new HashMap<>();
    private int salesTotal = 0; // shortcut: an int of centavos overflows near PHP 21 million, fine until week 5 (BigDecimal)

    /** The four products from the stock report, with the same numbers as lessons 03-06. */
    public static Inventory sample() {
        Inventory inventory = new Inventory();
        inventory.addProduct("Rice 25kg", 125050, 10);
        inventory.addProduct("Cooking Oil 1L", 9800, 10);
        inventory.addProduct("Sugar 1kg", 7500, 10);
        inventory.addProduct("Sardines 155g", 2400, 20);
        inventory.receive("Rice 25kg", 12);
        inventory.receive("Cooking Oil 1L", 4);
        inventory.receive("Sardines 155g", 60);
        return inventory;
    }

    /** The name as stored (for example "Sugar 1kg") when name matches a product ignoring capitals and spaces, else null. */
    public String find(String name) {
        for (String stored : names) {
            if (TextUtil.sameName(stored, name)) {
                return stored;
            }
        }
        return null;
    }

    /**
     * Adds a new product with 0 in stock. Returns false (and changes nothing) when the name is blank, the product
     * already exists, the price is not above 0, or the reorder level is below 0.
     */
    public boolean addProduct(String name, int priceCentavos, int reorderLevel) {
        String clean = TextUtil.clean(name);
        if (clean.isEmpty() || find(clean) != null || priceCentavos <= 0 || reorderLevel < 0) {
            return false;
        }
        names.add(clean);
        prices.put(clean, priceCentavos);
        quantities.put(clean, 0);
        reorderLevels.put(clean, reorderLevel);
        return true;
    }

    /** Units in stock, or -1 when there is no such product. */
    public int quantityOf(String name) {
        String stored = find(name);
        if (stored == null) {
            return -1;
        }
        return quantities.get(stored);
    }

    /** Unit price in centavos, or -1 when there is no such product. */
    public int priceOf(String name) {
        String stored = find(name);
        if (stored == null) {
            return -1;
        }
        return prices.get(stored);
    }

    /** Adds amount units. Returns false (and changes nothing) for an unknown product or an amount of 0 or less. */
    public boolean receive(String name, int amount) {
        String stored = find(name);
        if (stored == null || amount <= 0) {
            return false;
        }
        quantities.put(stored, quantities.get(stored) + amount);
        return true;
    }

    /**
     * Sells amount units. Returns the sale total in centavos (unit price x amount), or -1 and changes nothing when the
     * product is unknown, the amount is 0 or less, or there is not enough stock.
     */
    public int sell(String name, int amount) {
        String stored = find(name);
        if (stored == null || amount <= 0 || amount > quantities.get(stored)) {
            return -1;
        }
        quantities.put(stored, quantities.get(stored) - amount);
        int total = PriceCalculator.lineTotal(prices.get(stored), amount);
        salesTotal += total;
        return total;
    }

    public int size() {
        return names.size();
    }

    /** Everything sold so far, in centavos. */
    public int salesTotal() {
        return salesTotal;
    }

    /** One column of numbers (prices, quantities or reorder levels) as an array, in the order products were added. */
    private int[] column(HashMap<String, Integer> map) {
        int[] result = new int[names.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = map.get(names.get(i));
        }
        return result;
    }

    /** The stock report text (same format as lesson 03). */
    public String report() {
        String[] nameArray = names.toArray(new String[0]);
        return StockReport.build(nameArray, column(quantities), column(reorderLevels));
    }

    /** Value of everything on the shelves, in centavos. */
    public int totalValue() {
        return StockReport.totalValue(column(prices), column(quantities));
    }
}
