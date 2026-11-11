package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Your tests for ReorderAdvisor. The first one is written for you: run it and watch it go RED (the method is empty).
 * Then follow the TODO numbers in the lesson: make it green, add rows and tests, refactor.
 */
class ReorderAdvisorTest {

    /** A product on the shelf: name, how many are in stock, and its reorder level. */
    private static Product product(String name, int quantity, int reorderLevel) {
        return new Product(name, Money.of("10.00"), reorderLevel, quantity);
    }

    @ParameterizedTest(name = "{0} in stock, reorder level {1} -> order {2}")
    @CsvSource({
        "12, 10, 0",   // more than the level: fine
        "4,  10, 16",  // the sample shop's cooking oil: LOW, bring it up to 20
        "0,  10, 20"   // sold out
        // TODO 2: add the boundary rows. What happens at exactly the level (10, 10)? At level 0? One above the level?
    })
    @DisplayName("suggestedQuantity follows the rule: bring the shelf up to twice the reorder level")
    void suggestedQuantityFollowsTheRule(int quantity, int reorderLevel, int expected) {
        Product product = product("Anything", quantity, reorderLevel);

        assertEquals(expected, ReorderAdvisor.suggestedQuantity(product));
    }

    @Test
    void todo3_suggestionsListOnlyProductsThatNeedOrdering() {
        fail("TODO 3: build a list of products (some fine, some LOW, some OUT) and assert the exact list of ReorderSuggestion records, in shelf order");
    }

    @Test
    void todo3b_anEmptyShelfHasNothingToOrder() {
        fail("TODO 3b: ReorderAdvisor.suggestions(List.of()) is empty");
    }

    @Test
    void todo4_reportHasOneLinePerSuggestionOrSaysThereIsNothingToDo() {
        fail("TODO 4: assert report(List.of()) equals \"Nothing to reorder.\" and the text for two suggestions (lines joined by \n)");
    }

    @Test
    void todo5_theInventoryGivesTheSameAnswerAsTheAdvisor() {
        fail("TODO 5: Inventory.sample().reorderSuggestions() equals ReorderAdvisor.suggestions(sample.products())");
    }
}
