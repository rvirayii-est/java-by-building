package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The rules EVERY ProductRepository must follow, whatever it stores products in. Each implementation has a small
 * subclass that only says how to create one (see InMemoryProductRepositoryTest, JdbcProductRepositoryTest,
 * FileProductRepositoryTest). If a new implementation passes these tests, the rest of the app can use it.
 */
abstract class ProductRepositoryContract {

    /** A new, empty repository. */
    abstract ProductRepository create();

    ProductRepository repository;

    @BeforeEach
    void createRepository() {
        repository = create();
    }

    @Test
    void aSavedProductCanBeFoundAgainWithAllItsFields() {
        repository.add(new Product("Rice 25kg", Money.of("1250.50"), 10, 17));
        Product found = repository.findByName("Rice 25kg").orElseThrow();
        assertEquals("Rice 25kg", found.getName());
        assertEquals(Money.of("1250.50"), found.getPrice());
        assertEquals(10, found.getReorderLevel());
        assertEquals(17, found.getQuantity());
    }

    @Test
    void findingIgnoresCapitalsAndExtraSpaces() {
        repository.add(new Product("Rice 25kg", Money.of("1250.50"), 10));
        assertTrue(repository.findByName("  RICE   25kg ").isPresent());
    }

    @Test
    void anUnknownNameGivesAnEmptyOptional() {
        assertTrue(repository.findByName("salt").isEmpty());
    }

    @Test
    void theSameNameTwiceIsRefused() {
        repository.add(new Product("Rice 25kg", Money.of("1250.50"), 10));
        InvalidInputException e = assertThrows(InvalidInputException.class,
                () -> repository.add(new Product("rice 25KG", Money.of("1.00"), 1)));
        assertEquals("That product already exists: rice 25KG", e.getMessage());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void findAllKeepsTheOrderTheProductsWereAdded() {
        repository.add(new Product("Sugar 1kg", Money.of("75.00"), 10));
        repository.add(new Product("Rice 25kg", Money.of("1250.50"), 10));
        repository.add(new Product("Cooking Oil 1L", Money.of("98.00"), 10));
        assertEquals(List.of("Sugar 1kg", "Rice 25kg", "Cooking Oil 1L"), repository.findAll().stream().map(Product::getName).toList());
    }

    @Test
    void updateSavesPriceReorderLevelAndQuantity() {
        repository.add(new Product("Rice 25kg", Money.of("1250.50"), 10, 17));
        Product product = repository.findByName("Rice 25kg").orElseThrow();
        product.setPrice(Money.of("1300.00"));
        product.setReorderLevel(12);
        product.sell(5);
        repository.update(product);
        Product reloaded = repository.findByName("Rice 25kg").orElseThrow();
        assertEquals(Money.of("1300.00"), reloaded.getPrice());
        assertEquals(12, reloaded.getReorderLevel());
        assertEquals(12, reloaded.getQuantity());
    }

    @Test
    void changesToAProductAreNotSavedUntilUpdateIsCalled() {
        repository.add(new Product("Rice 25kg", Money.of("1250.50"), 10, 17));
        repository.findByName("Rice 25kg").orElseThrow().sell(5); // changed the copy only
        assertEquals(17, repository.findByName("Rice 25kg").orElseThrow().getQuantity());
    }

    @Test
    void updatingAProductThatWasNeverSavedIsAnError() {
        assertThrows(UnknownProductException.class, () -> repository.update(new Product("Ghost", Money.of("1.00"), 1)));
    }

    @Test
    void anApostropheOrQuoteInANameIsJustText() {
        repository.add(new Product("Nestle's \"Milo\", 200g", Money.of("12.00"), 1));
        assertEquals("Nestle's \"Milo\", 200g", repository.findAll().get(0).getName());
    }
}
