package dev.javabybuilding.inventory;

public class StockChecker {

    /** "OUT" when nothing is left, "LOW" at or below the reorder level, otherwise "OK". */
    public static String status(int quantity, int reorderLevel) {
        if (quantity <= 0) {
            return "OUT";
        } else if (quantity <= reorderLevel) {
            return "LOW";
        } else {
            return "OK";
        }
    }
}
