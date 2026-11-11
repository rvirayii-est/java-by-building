package dev.javabybuilding.inventory;

class InMemorySalesRepositoryTest extends SalesRepositoryContract {

    @Override
    Repos create() {
        return new Repos(new InMemoryProductRepository(), new InMemorySaleRepository(), new InMemoryStockMovementRepository());
    }
}
