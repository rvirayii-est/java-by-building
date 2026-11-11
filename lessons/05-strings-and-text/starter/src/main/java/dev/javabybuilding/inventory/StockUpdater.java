package dev.javabybuilding.inventory;

/** Small methods that find and change stock in the parallel arrays. */
public class StockUpdater {

    /**
     * Position of name in names, or -1 when it is not there.
     * TODO 5 (after TextUtil works): compare with TextUtil.sameName(names[i], name) instead of equals,
     * so that "  sugar 1KG " finds "Sugar 1kg".
     */
    public static int indexOf(String[] names, String name) {
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    /** Adds amount to quantities[index]. The array you passed in is changed. */
    public static void receive(int[] quantities, int index, int amount) {
        quantities[index] += amount;
    }

    /** Takes amount off quantities[index] and returns true. If there is not enough, changes nothing and returns false. */
    public static boolean sell(int[] quantities, int index, int amount) {
        if (amount > quantities[index]) {
            return false;
        }
        quantities[index] -= amount;
        return true;
    }
}
