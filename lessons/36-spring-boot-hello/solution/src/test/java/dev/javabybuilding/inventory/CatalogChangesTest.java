package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Lesson 34 added three things to the shop core: ProductRepository.delete, ShopService.updateProduct and
 * ShopService.deleteProduct. They are tested on the in-memory repositories AND on the real database, because a
 * repository must behave the same everywhere (lesson 30).
 */
class CatalogChangesTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-03-01T01:30:00Z"), Inventory.SHOP_ZONE);

    private Database database;

    @AfterEach
    void close() {
        if (database != null) {
            database.close();
        }
    }

    private ShopService inMemory() {
        return new ShopService(new InMemoryProductRepository(), new InMemorySaleRepository(), new InMemoryStockMovementRepository(), CLOCK);
    }

    private ShopService onDatabase() {
        database = TestDb.fresh();
        return new ShopService(new JdbcProductRepository(database), new JdbcSaleRepository(database),
                new JdbcStockMovementRepository(database), CLOCK, database);
    }

    @Test
    void inMemoryUpdateChangesPriceAndReorderLevelOnly() {
        check(inMemory());
    }

    @Test
    void databaseUpdateChangesPriceAndReorderLevelOnly() {
        check(onDatabase());
    }

    private void check(ShopService shop) {
        shop.importFrom(Inventory.sample(new Inventory(CLOCK)));
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
    void inMemoryDeleteRemovesAProductWithNoHistory() {
        checkDelete(inMemory());
    }

    @Test
    void databaseDeleteRemovesAProductWithNoHistory() {
        checkDelete(onDatabase());
    }

    private void checkDelete(ShopService shop) {
        shop.importFrom(Inventory.sample(new Inventory(CLOCK)));
        shop.addProduct("Salt 1kg", Money.of("20.00"), 5);
        shop.deleteProduct("  SALT 1kg ");
        assertThrows(UnknownProductException.class, () -> shop.getProduct("Salt 1kg"));
        assertEquals(4, shop.products().size());
        assertThrows(UnknownProductException.class, () -> shop.deleteProduct("Salt 1kg"));
    }

    @Test
    void aProductWithStockHistoryCannotBeDeleted() {
        ShopService shop = inMemory();
        shop.importFrom(Inventory.sample(new Inventory(CLOCK)));
        ProductInUseException e = assertThrows(ProductInUseException.class, () -> shop.deleteProduct("Rice 25kg"));
        assertEquals("Cannot delete Rice 25kg: it already has stock movements or sales.", e.getMessage());
        assertEquals(4, shop.products().size());
    }

    @Test
    void aSoldProductCannotBeDeletedEvenOnTheDatabase() {
        ShopService shop = onDatabase();
        shop.importFrom(Inventory.sample(new Inventory(CLOCK)));
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
        database = TestDb.fresh();
        ProductRepository jdbc = new JdbcProductRepository(database);
        jdbc.add(new Product("Rice 25kg", Money.of("1250.50"), 10));
        jdbc.delete("rice 25kg");
        assertTrue(jdbc.findAll().isEmpty());
        assertThrows(UnknownProductException.class, () -> jdbc.delete("Rice 25kg"));
    }
}
