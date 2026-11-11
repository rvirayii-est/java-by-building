package dev.javabybuilding.config;

import dev.javabybuilding.inventory.Database;
import dev.javabybuilding.inventory.Inventory;
import dev.javabybuilding.inventory.JdbcProductRepository;
import dev.javabybuilding.inventory.JdbcSaleRepository;
import dev.javabybuilding.inventory.JdbcStockMovementRepository;
import dev.javabybuilding.inventory.Migrator;
import dev.javabybuilding.inventory.ShopService;
import java.nio.file.Path;
import java.time.Clock;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Builds the shop from the SAME classes as lessons 29-32: the {@link Database}, the {@link Migrator}, the three JDBC
 * repositories and the {@link ShopService}. Nothing of them was rewritten; this class only says how to wire them, which is
 * what {@code Main.startShop} did in lesson 32. Spring calls each {@code @Bean} method once, in the right order, because a
 * method that takes a parameter (a {@code Database}) shows what it needs.
 */
@Configuration
public class ShopConfig {

    /**
     * The database. {@code shop.database.mode=file} (the default) keeps it in the folder {@code shop.data-dir}; {@code memory}
     * uses a database that disappears when the app stops (the tests use this). Spring closes it at shut-down: {@code Database}
     * has a {@code close()} method, and a bean's close() is called automatically.
     */
    @Bean
    public Database database(@Value("${shop.database.mode:file}") String mode, @Value("${shop.data-dir:shop-data}") String dataDir) {
        Database database = mode.equals("memory")
                ? Database.openMemory("shop-" + UUID.randomUUID()) // a unique name: each app gets its own memory database
                : Database.openFile(Path.of(dataDir, "shop"));
        Migrator.standard(database).migrate(); // lesson 32: bring the tables up to date
        return database;
    }

    @Bean
    public Clock clock() {
        return Clock.system(Inventory.SHOP_ZONE);
    }

    @Bean
    public ShopService shopService(Database database, Clock clock) {
        // TODO 1: build the shop exactly as Main.startShop did in lesson 32: a ShopService on new JdbcProductRepository(database),
        // new JdbcSaleRepository(database) and new JdbcStockMovementRepository(database), with the clock, and the database as the
        // last argument (it is the Transactor, lesson 31). Spring has already created `database` and `clock` and passes them in.
        return null;
    }

    /** Runs once after start-up: an EMPTY database gets the four sample products, so a fresh install has something to show. */
    @Bean
    public ApplicationRunner seedSampleShop(ShopService shop, @Value("${shop.seed-sample:true}") boolean seed) {
        return args -> {
            synchronized (shop) {
                if (seed && shop.products().isEmpty()) {
                    shop.importFrom(Inventory.sample());
                }
            }
        };
    }
}
