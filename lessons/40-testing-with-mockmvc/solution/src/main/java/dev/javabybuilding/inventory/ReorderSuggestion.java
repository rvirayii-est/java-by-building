package dev.javabybuilding.inventory;

/** "Order 16 more of Cooking Oil 1L." */
public record ReorderSuggestion(String productName, int quantity) {

    /** For example "Reorder 16 x Cooking Oil 1L". */
    public String describe() {
        return "Reorder " + quantity + " x " + productName;
    }
}
