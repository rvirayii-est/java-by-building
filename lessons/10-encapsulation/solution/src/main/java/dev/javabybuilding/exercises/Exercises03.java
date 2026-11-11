package dev.javabybuilding.exercises;

public class Exercises03 {

    /** How many products are at or below the reorder level? countLowStock({3, 50, 10}, 10) returns 2. */
    public static int countLowStock(int[] quantities, int reorderLevel) {
        int count = 0;
        for (int quantity : quantities) {
            if (quantity <= reorderLevel) {
                count++;
            }
        }
        return count;
    }

    /** Total number of units on the shelves. */
    public static int totalUnits(int[] quantities) {
        int total = 0;
        for (int quantity : quantities) {
            total += quantity;
        }
        return total;
    }
}
