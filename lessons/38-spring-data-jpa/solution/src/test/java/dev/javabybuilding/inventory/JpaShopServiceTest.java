package dev.javabybuilding.inventory;

import dev.javabybuilding.persistence.CustomerJpaRepository;
import dev.javabybuilding.persistence.JpaProductRepository;
import dev.javabybuilding.persistence.JpaSaleRepository;
import dev.javabybuilding.persistence.JpaStockMovementRepository;
import dev.javabybuilding.persistence.ProductJpaRepository;
import dev.javabybuilding.persistence.SaleJpaRepository;
import dev.javabybuilding.persistence.SpringTransactor;
import dev.javabybuilding.persistence.StockMovementJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;

/** ShopService must behave exactly like Inventory, also on JPA: the whole ShopServiceContract of lesson 30 runs on the database. */
@ShopDataJpaTest
class JpaShopServiceTest extends ShopServiceContract {

    @Autowired ProductJpaRepository productJpa;
    @Autowired SaleJpaRepository saleJpa;
    @Autowired StockMovementJpaRepository movementJpa;
    @Autowired CustomerJpaRepository customerJpa;
    @Autowired PlatformTransactionManager transactionManager;

    @Override
    ShopService create(TestClock clock) {
        return new ShopService(new JpaProductRepository(productJpa), new JpaSaleRepository(saleJpa, customerJpa, productJpa),
                new JpaStockMovementRepository(movementJpa, productJpa), clock, new SpringTransactor(transactionManager));
    }
}
