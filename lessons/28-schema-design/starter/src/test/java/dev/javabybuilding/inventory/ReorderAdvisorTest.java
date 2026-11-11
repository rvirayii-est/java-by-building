package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Written test-first (lesson 21). Read the tests as a specification of ReorderAdvisor: each name says a rule in plain
 * English, each body has three parts: Arrange (set up), Act (call the code), Assert (check the answer).
 */
class ReorderAdvisorTest {

    /** A product on the shelf: name, how many are in stock, and its reorder level. */
    private static Product product(String name, int quantity, int reorderLevel) {
        return new Product(name, Money.of("10.00"), reorderLevel, quantity);
    }

    @Nested
    @DisplayName("suggestedQuantity for one product")
    class SuggestedQuantity {

        @ParameterizedTest(name = "{0} in stock, reorder level {1} -> order {2}")
        @CsvSource({
            "12, 10, 0",   // more than the level: fine
            "11, 10, 0",   // one above the level: still fine
            "10, 10, 10",  // exactly at the level counts as LOW: bring it up to 20
            "4,  10, 16",  // the sample shop's cooking oil
            "1,  10, 19",
            "0,  10, 20",  // sold out
            "0,  0,  0",   // level 0 means "not tracked"
            "5,  0,  0",
            "3,  3,  3"
        })
        @DisplayName("follows the rule: bring the shelf up to twice the reorder level")
        void followsTheRule(int quantity, int reorderLevel, int expected) {
            Product product = product("Anything", quantity, reorderLevel);

            assertEquals(expected, ReorderAdvisor.suggestedQuantity(product));
        }

        @Test
        @DisplayName("a suggestion is never negative")
        void neverNegative() {
            assertTrue(ReorderAdvisor.suggestedQuantity(product("Plenty", 500, 10)) >= 0);
        }
    }

    @Nested
    @DisplayName("suggestions for a list of products")
    class Suggestions {

        private List<Product> shelf;

        @BeforeEach // runs before EVERY test in this nested class, so each test gets a fresh shelf
        void stockTheShelf() {
            shelf = List.of(
                    product("Rice 25kg", 12, 10),        // fine
                    product("Cooking Oil 1L", 4, 10),    // LOW
                    product("Sugar 1kg", 0, 10),         // OUT
                    product("Sardines 155g", 60, 20));   // fine
        }

        @Test
        @DisplayName("only products that need ordering are listed, in shelf order")
        void onlyProductsThatNeedOrdering() {
            List<ReorderSuggestion> suggestions = ReorderAdvisor.suggestions(shelf);

            assertEquals(List.of(new ReorderSuggestion("Cooking Oil 1L", 16), new ReorderSuggestion("Sugar 1kg", 20)), suggestions);
        }

        @Test
        @DisplayName("an empty shelf has nothing to order")
        void emptyShelf() {
            assertTrue(ReorderAdvisor.suggestions(List.of()).isEmpty());
        }

        @Test
        @DisplayName("the inventory gives the same answer as the advisor")
        void inventoryDelegates() {
            Inventory sample = Inventory.sample();

            assertEquals(ReorderAdvisor.suggestions(sample.products()), sample.reorderSuggestions());
        }

        @Test
        @DisplayName("assertAll reports every broken check, not just the first")
        void assertAllChecksEverything() {
            ReorderSuggestion first = ReorderAdvisor.suggestions(shelf).get(0);

            assertAll("the first suggestion",
                    () -> assertEquals("Cooking Oil 1L", first.productName()),
                    () -> assertEquals(16, first.quantity()),
                    () -> assertEquals("Reorder 16 x Cooking Oil 1L", first.describe()));
        }
    }

    @Nested
    @DisplayName("report text")
    class Report {

        static Stream<Arguments> reports() {
            return Stream.of(
                    Arguments.of(List.of(), "Nothing to reorder."),
                    Arguments.of(List.of(new ReorderSuggestion("Sugar 1kg", 20)), "Reorder 20 x Sugar 1kg"),
                    Arguments.of(List.of(new ReorderSuggestion("Cooking Oil 1L", 16), new ReorderSuggestion("Sugar 1kg", 20)),
                            "Reorder 16 x Cooking Oil 1L\nReorder 20 x Sugar 1kg"));
        }

        @ParameterizedTest(name = "{0} suggestion(s) -> text")
        @MethodSource("reports")
        @DisplayName("lists one line per suggestion, or says there is nothing to do")
        void listsOneLinePerSuggestion(List<ReorderSuggestion> suggestions, String expected) {
            assertEquals(expected, ReorderAdvisor.report(suggestions));
        }
    }
}
