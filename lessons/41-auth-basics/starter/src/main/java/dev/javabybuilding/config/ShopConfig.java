package dev.javabybuilding.config;

import dev.javabybuilding.inventory.Inventory;
import dev.javabybuilding.inventory.ProductRepository;
import dev.javabybuilding.inventory.SaleRepository;
import dev.javabybuilding.inventory.ShopService;
import dev.javabybuilding.inventory.StockMovementRepository;
import dev.javabybuilding.inventory.Transactor;
import dev.javabybuilding.persistence.SpringTransactor;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Lesson 37 built the Database, the Migrator and the three JDBC repositories here by hand. Now Spring Boot provides them:
 * <ul>
 *   <li>the DataSource and its connection pool (HikariCP) come from {@code spring.datasource.*} in application.properties;</li>
 *   <li>Flyway runs the files in db/migration at start-up (the Migrator's job);</li>
 *   <li>the three repositories are the JPA classes in dev.javabybuilding.persistence, found by component scanning.</li>
 * </ul>
 * What is left is the part that is ours: tie the repositories to the ShopService, which did not change since lesson 30.
 */
@Configuration
public class ShopConfig {

    @Bean
    public Clock clock() {
        return Clock.system(Inventory.SHOP_ZONE);
    }

    /** Lesson 31's Transactor, backed by Spring's transaction manager (which Spring Boot creates for the JPA set-up). */
    @Bean
    public Transactor transactor(PlatformTransactionManager transactionManager) {
        return new SpringTransactor(transactionManager);
    }

    @Bean
    public ShopService shopService(ProductRepository products, SaleRepository sales, StockMovementRepository movements, Clock clock,
                                   Transactor transactor) {
        return new ShopService(products, sales, movements, clock, transactor);
    }
}
