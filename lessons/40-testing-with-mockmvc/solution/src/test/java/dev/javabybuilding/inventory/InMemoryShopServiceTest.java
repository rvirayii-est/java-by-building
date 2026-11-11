package dev.javabybuilding.inventory;

class InMemoryShopServiceTest extends ShopServiceContract {

    @Override
    ShopService create(TestClock clock) {
        return new ShopService(new InMemoryProductRepository(), new InMemorySaleRepository(), new InMemoryStockMovementRepository(), clock);
    }
}
