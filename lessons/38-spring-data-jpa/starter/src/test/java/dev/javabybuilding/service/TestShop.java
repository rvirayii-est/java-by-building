package dev.javabybuilding.service;

import dev.javabybuilding.inventory.InMemoryProductRepository;
import dev.javabybuilding.inventory.InMemorySaleRepository;
import dev.javabybuilding.inventory.InMemoryStockMovementRepository;
import dev.javabybuilding.inventory.Inventory;
import dev.javabybuilding.inventory.ShopService;
import java.time.Clock;
import java.time.Instant;

/** Test helper: a ShopService on in-memory repositories with the four sample products and a clock that stands still at 09:30 in Manila. */
public final class TestShop {

    public static final Clock CLOCK = Clock.fixed(Instant.parse("2026-03-01T01:30:00Z"), Inventory.SHOP_ZONE);

    private TestShop() {}

    public static ShopService sample() {
        ShopService shop = new ShopService(new InMemoryProductRepository(), new InMemorySaleRepository(), new InMemoryStockMovementRepository(), CLOCK);
        shop.importFrom(Inventory.sample(new Inventory(CLOCK)));
        return shop;
    }
}
