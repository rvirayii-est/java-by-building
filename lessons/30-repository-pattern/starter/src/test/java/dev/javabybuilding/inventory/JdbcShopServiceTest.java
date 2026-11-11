package dev.javabybuilding.inventory;

import org.junit.jupiter.api.AfterEach;

class JdbcShopServiceTest extends ShopServiceContract {

    private Database database;

    @Override
    ShopService create(TestClock clock) {
        database = TestDb.fresh();
        return new ShopService(new JdbcProductRepository(database), new JdbcSaleRepository(database),
                new JdbcStockMovementRepository(database), clock);
    }

    @AfterEach
    void close() {
        database.close();
    }
}
