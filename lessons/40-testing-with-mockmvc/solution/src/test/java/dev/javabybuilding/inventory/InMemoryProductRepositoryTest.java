package dev.javabybuilding.inventory;

class InMemoryProductRepositoryTest extends ProductRepositoryContract {

    @Override
    ProductRepository create() {
        return new InMemoryProductRepository();
    }
}
