package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Lesson 17: the Map inside Inventory, sorting with Comparators, Counter and Set in use. */
class InventoryCollectionsTest {

    private final Inventory inventory = Inventory.sample();

    private static List<String> names(List<Product> products) {
        List<String> names = new ArrayList<>();
        for (Product product : products) {
            names.add(product.getName());
        }
        return names;
    }

    @Test
    void lookupStillIgnoresCapitalsAndSpacesNowThatProductsLiveInAMap() {
        assertEquals("Sugar 1kg", inventory.find("  sUGAr   1KG "));
        assertEquals(12, inventory.quantityOf("rice   25KG"));
    }

    @Test
    void productsKeepTheOrderTheyWereAdded() {
        assertEquals(List.of("Rice 25kg", "Cooking Oil 1L", "Sugar 1kg", "Sardines 155g"), names(inventory.products()));
    }

    @Test
    void sortByNameGivesAToZ() {
        assertEquals(List.of("Cooking Oil 1L", "Rice 25kg", "Sardines 155g", "Sugar 1kg"),
                names(inventory.sortedProducts(ProductOrder.BY_NAME)));
    }

    @Test
    void sortByQuantityPutsTheEmptiestShelfFirst() {
        // quantities: rice 12, oil 4, sugar 0, sardines 60
        assertEquals(List.of("Sugar 1kg", "Cooking Oil 1L", "Rice 25kg", "Sardines 155g"),
                names(inventory.sortedProducts(ProductOrder.BY_QUANTITY)));
    }

    @Test
    void sortByStockValuePutsTheMostValuableFirst() {
        // values: rice 12 x 1250.50, sardines 60 x 24, oil 4 x 98, sugar 0
        assertEquals(List.of("Rice 25kg", "Sardines 155g", "Cooking Oil 1L", "Sugar 1kg"),
                names(inventory.sortedProducts(ProductOrder.BY_STOCK_VALUE_DESC)));
    }

    @Test
    void sortingMakesACopyAndLeavesTheInventoryOrderAlone() {
        inventory.sortedProducts(ProductOrder.BY_NAME);
        assertEquals("Rice 25kg", inventory.products().get(0).getName());
    }

    @Test
    void anyComparatorWorksIncludingOneWrittenOnTheSpot() {
        List<Product> byShortName = inventory.sortedProducts((a, b) -> a.getName().length() - b.getName().length());
        // Rice 25kg and Sugar 1kg tie at 9 letters; sorting is stable, so they keep their original order
        assertEquals(List.of("Rice 25kg", "Sugar 1kg", "Sardines 155g", "Cooking Oil 1L"), names(byShortName));
    }

    @Test
    void bestSellersAreCountedFromTheStockLog() {
        inventory.sell("Sardines 155g", 30);
        inventory.sell("Rice 25kg", 5);
        inventory.sell("Sardines 155g", 10);
        inventory.sell("Cooking Oil 1L", 4);
        assertEquals(40, inventory.unitsSoldByProduct().count("Sardines 155g"));
        assertEquals(List.of("Sardines 155g", "Rice 25kg"), inventory.bestSellers(2));
        assertEquals(49, inventory.unitsSoldByProduct().total());
    }

    @Test
    void nothingSoldMeansNoBestSellers() {
        assertEquals(List.of(), inventory.bestSellers(3));
    }

    @Test
    void buyerNamesAreUniqueSortedAndLeaveOutTheWalkIn() {
        Customer zia = new Customer("Zia Cruz", "0917");
        Customer ana = new Customer("Ana Reyes", "0918");
        inventory.sell("Sardines 155g", 1, zia);
        inventory.sell("Sardines 155g", 1, ana);
        inventory.sell("Sardines 155g", 1, zia);
        inventory.sell("Sardines 155g", 1); // walk-in
        assertEquals(Set.of("Ana Reyes", "Zia Cruz"), inventory.buyerNames());
        assertEquals(List.of("Ana Reyes", "Zia Cruz"), List.copyOf(inventory.buyerNames()));
    }

    @Test
    void theProductListIsAReadOnlySnapshot() {
        assertThrows(UnsupportedOperationException.class, () -> inventory.products().add(null)); // the copy is read-only
    }
}
