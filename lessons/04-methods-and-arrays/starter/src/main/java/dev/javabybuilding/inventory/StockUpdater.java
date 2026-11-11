package dev.javabybuilding.inventory;

/** Small methods that find and change stock in the parallel arrays. */
public class StockUpdater {

    /**
     * TODO 1: return the position of name in names, or -1 when it is not there.
     * Loop over the array and compare with names[i].equals(name). (Never compare Strings with ==.)
     */
    public static int indexOf(String[] names, String name) {
        return -1;
    }

    /** TODO 2: add amount to quantities[index]. No return value: the array you were given is changed. */
    public static void receive(int[] quantities, int index, int amount) {
    }

    /**
     * TODO 3: if amount is more than quantities[index], change nothing and return false.
     * Otherwise subtract amount from quantities[index] and return true.
     */
    public static boolean sell(int[] quantities, int index, int amount) {
        return false;
    }
}
