package dev.javabybuilding.config;

import dev.javabybuilding.inventory.Inventory;
import dev.javabybuilding.inventory.ShopService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Runs once after start-up: an EMPTY database gets the four sample products, so a fresh install has something to show.
 * {@code shop.seed-sample=false} turns it off (the tests that need an empty shop do that). Test slices such as {@code @WebMvcTest}
 * and {@code @DataJpaTest} do not load it: they start only the part of the app they are about.
 */
@Component
public class SampleDataSeeder implements ApplicationRunner {

    private final ShopService shop;
    private final boolean seed;

    public SampleDataSeeder(ShopService shop, @Value("${shop.seed-sample:true}") boolean seed) {
        this.shop = shop;
        this.seed = seed;
    }

    @Override
    public void run(ApplicationArguments args) {
        synchronized (shop) {
            if (seed && shop.products().isEmpty()) {
                shop.importFrom(Inventory.sample());
            }
        }
    }
}
