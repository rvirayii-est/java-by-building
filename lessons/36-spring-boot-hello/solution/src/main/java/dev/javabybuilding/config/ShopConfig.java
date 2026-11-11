package dev.javabybuilding.config;

import dev.javabybuilding.inventory.Inventory;
import dev.javabybuilding.inventory.Shop;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Tells Spring how to build the objects the app needs. A method marked {@code @Bean} is a recipe: Spring calls it ONCE and keeps
 * the result (a "bean") to hand to every class that asks for a {@link Shop}.
 *
 * <p>For now the shop is the in-memory {@link Inventory} of lessons 12-20 with its four sample products: nothing is saved
 * when the app stops. Lesson 37 swaps this one recipe for the database-backed shop of lessons 29-32, and nothing else changes.
 */
@Configuration
public class ShopConfig {

    @Bean
    public Shop shop() {
        return Inventory.sample();
    }
}
