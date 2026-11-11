package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import org.junit.jupiter.api.Test;

/**
 * Lesson 34 added ProductRepository.delete, ShopService.updateProduct and ShopService.deleteProduct. Here they are tested on the
 * in-memory repositories (fast). The JPA repositories pass the same rules in ProductRepositoryContract, and the whole path through
 * the database is exercised by the API tests.
 */
class CatalogChangesTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-03-01T01:30:00Z"), Inventory.SHOP_ZONE);

    private ShopService shop() {
        ShopService shop = new ShopService(new InMemoryProductRepository(), new InMemorySaleRepository(), new InMemoryStockMovementRepository(), CLOCK);
        shop.importFrom(Inventory.sample(new Inventory(CLOCK)));
        return shop;
    }

    @Test
    void updateChangesPriceAndReorderLevelOnly() {
        ShopService shop = shop();
        Product changed = shop.updateProduct("rice 25kg", Money.of("1300.00"), 15);
        assertEquals(Money.of("1300.00"), changed.getPrice());
        assertEquals(15, shop.getProduct("Rice 25kg").getReorderLevel());
        assertEquals(12, shop.quantityOf("Rice 25kg")); // the stock is not touched by an update
        assertThrows(UnknownProductException.class, () -> shop.updateProduct("salt", Money.of("5.00"), 1));
        assertThrows(InvalidInputException.class, () -> shop.updateProduct("Rice 25kg", Money.of("0.00"), 1));
        assertThrows(InvalidInputException.class, () -> shop.updateProduct("Rice 25kg", Money.of("5.00"), -1));
        assertEquals(Money.of("1300.00"), shop.getProduct("Rice 25kg").getPrice()); // refused updates changed nothing
        assertEquals(15, shop.getProduct("Rice 25kg").getReorderLevel());
    }

    @Test
    void deleteRemovesAProductWithNoHistory() {
        ShopService shop = shop();
        shop.addProduct("Salt 1kg", Money.of("20.00"), 5);
        shop.deleteProduct("  SALT 1kg ");
        assertThrows(UnknownProductException.class, () -> shop.getProduct("Salt 1kg"));
        assertEquals(4, shop.products().size());
        assertThrows(UnknownProductException.class, () -> shop.deleteProduct("Salt 1kg"));
    }

    @Test
    void aProductWithStockHistoryCannotBeDeleted() {
        ShopService shop = shop();
        ProductInUseException e = assertThrows(ProductInUseException.class, () -> shop.deleteProduct("Rice 25kg"));
        assertEquals("Cannot delete Rice 25kg: it already has stock movements or sales.", e.getMessage());
        assertEquals(4, shop.products().size());
    }

    @Test
    void aSoldProductCannotBeDeleted() {
        ShopService shop = shop();
        shop.sell("Sardines 155g", 1);
        assertThrows(ProductInUseException.class, () -> shop.deleteProduct("Sardines 155g"));
        assertTrue(shop.audit().isEmpty());
    }

    @Test
    void repositoriesDeleteByNameIgnoringCapitalsAndRefuseUnknownNames() {
        ProductRepository repository = new InMemoryProductRepository();
        repository.add(new Product("Rice 25kg", Money.of("1250.50"), 10));
        repository.delete("  rice 25KG");
        assertTrue(repository.findAll().isEmpty());
        assertThrows(UnknownProductException.class, () -> repository.delete("Rice 25kg"));
    }
}
