package dev.javabybuilding.inventory;

import java.util.List;

/**
 * Suggests what to buy from suppliers. This class was written test-first (lesson 21): every rule below began as a
 * failing test in ReorderAdvisorTest.
 *
 * The rules:
 * 1. A product with a reorder level of 0 is not tracked: never suggest anything for it.
 * 2. A product with MORE than its reorder level on the shelf is fine: suggest 0.
 * 3. Otherwise (the same rule as the LOW and OUT status) order enough to bring the shelf up to TWICE the reorder level.
 */
public class ReorderAdvisor {

    /** How many units to order for one product (0 means "nothing"). */
    public static int suggestedQuantity(Product product) {
        int level = product.getReorderLevel();
        if (level == 0 || product.getQuantity() > level) {
            return 0;
        }
        return 2 * level - product.getQuantity();
    }

    /** One suggestion for every product that needs ordering, in the order of the list. Products that are fine are left out. */
    public static List<ReorderSuggestion> suggestions(List<Product> products) {
        return products.stream()
                .filter(product -> suggestedQuantity(product) > 0)
                .map(product -> new ReorderSuggestion(product.getName(), suggestedQuantity(product)))
                .toList();
    }

    /** The suggestions as text, one line each, or "Nothing to reorder." when the list is empty. */
    public static String report(List<ReorderSuggestion> suggestions) {
        if (suggestions.isEmpty()) {
            return "Nothing to reorder.";
        }
        return String.join("\n", suggestions.stream().map(ReorderSuggestion::describe).toList());
    }
}
