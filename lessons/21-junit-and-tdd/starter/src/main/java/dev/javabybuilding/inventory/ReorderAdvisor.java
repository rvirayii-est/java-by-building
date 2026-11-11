package dev.javabybuilding.inventory;

import java.util.List;

/**
 * Suggests what to buy from suppliers. You build this class TEST-FIRST (lesson 21): write a failing test in
 * ReorderAdvisorTest, then the least code that passes it, then tidy up. The methods start empty on purpose.
 *
 * The rules:
 * 1. A product with a reorder level of 0 is not tracked: never suggest anything for it.
 * 2. A product with MORE than its reorder level on the shelf is fine: suggest 0.
 * 3. Otherwise (the same rule as the LOW and OUT status) order enough to bring the shelf up to TWICE the reorder level.
 */
public class ReorderAdvisor {

    /** How many units to order for one product (0 means "nothing"). */
    public static int suggestedQuantity(Product product) {
        // TODO 1 (green): apply the three rules, one test at a time. Start with the simplest thing that passes.
        return 0;
    }

    /** One suggestion for every product that needs ordering, in the order of the list. Products that are fine are left out. */
    public static List<ReorderSuggestion> suggestions(List<Product> products) {
        // TODO 3 (green, after you wrote the test): keep the products whose suggestedQuantity is above 0 and make a
        // new ReorderSuggestion(name, quantity) for each. A stream with filter and map does it in a few lines.
        return List.of();
    }

    /** The suggestions as text, one line each, or "Nothing to reorder." when the list is empty. */
    public static String report(List<ReorderSuggestion> suggestions) {
        // TODO 4 (green): "Nothing to reorder." for an empty list, otherwise each suggestion's describe() on its own line.
        return "";
    }
}
