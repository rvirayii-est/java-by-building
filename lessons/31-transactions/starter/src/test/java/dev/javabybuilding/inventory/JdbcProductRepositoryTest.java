package dev.javabybuilding.inventory;

import org.junit.jupiter.api.AfterEach;

class JdbcProductRepositoryTest extends ProductRepositoryContract {

    private Database database;

    @Override
    ProductRepository create() {
        database = TestDb.fresh();
        return new JdbcProductRepository(database);
    }

    @AfterEach
    void close() {
        database.close();
    }
}
