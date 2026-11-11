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

    /** The four products from the stock report, with the same numbers as lessons 03-06. (Done for you.) */
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

    /**
     * TODO 1: return the name as stored (for example "Sugar 1kg") when name matches a product ignoring capitals and
     * extra spaces, otherwise null. Loop: for (String stored : names) { if (TextUtil.sameName(stored, name)) ... }
     */
    public String find(String name) {
        return null;
    }

    /**
     * TODO 2: add a new product with 0 in stock. Return false (and change nothing) when the cleaned name is empty,
     * the product already exists (use find), priceCentavos is not above 0, or reorderLevel is below 0.
     * Otherwise: names.add(clean); prices.put(clean, priceCentavos); quantities.put(clean, 0);
     * reorderLevels.put(clean, reorderLevel); and return true.
     */
    public boolean addProduct(String name, int priceCentavos, int reorderLevel) {
        return false;
    }

    /** TODO 3: units in stock, or -1 when there is no such product. Use find, then quantities.get(stored). */
    public int quantityOf(String name) {
        return -2;
    }

    /** TODO 3 (continued): unit price in centavos, or -1 when there is no such product. */
    public int priceOf(String name) {
        return -2;
    }

    /**
     * TODO 4: add amount units. Return false (and change nothing) for an unknown product or an amount of 0 or less.
     * Hint: quantities.put(stored, quantities.get(stored) + amount);
     */
    public boolean receive(String name, int amount) {
        return false;
    }

    /**
     * TODO 5: sell amount units. Return -1 and change nothing when the product is unknown, amount is 0 or less,
     * or amount is more than the stock. Otherwise reduce the stock, add the sale total to salesTotal,
     * and return the sale total in centavos: PriceCalculator.lineTotal(price, amount).
     */
    public int sell(String name, int amount) {
        return -2;
    }

    public int size() {
        return names.size();
    }

    /** Everything sold so far, in centavos. */
    public int salesTotal() {
        return salesTotal;
    }

    /** Helper (done for you): one column of numbers, as an array in the order products were added. */
    private int[] column(HashMap<String, Integer> map) {
        int[] result = new int[names.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = map.get(names.get(i));
        }
        return result;
    }

    /**
     * TODO 6: the stock report text from lesson 03. names.toArray(new String[0]) turns the ArrayList into an array;
     * then call StockReport.build(nameArray, column(quantities), column(reorderLevels)).
     */
    public String report() {
        return "";
    }

    /** TODO 6 (continued): value of everything on the shelves: StockReport.totalValue(column(prices), column(quantities)). */
    public int totalValue() {
        return 0;
    }
}
