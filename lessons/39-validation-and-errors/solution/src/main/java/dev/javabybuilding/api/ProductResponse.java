package dev.javabybuilding.api;

import dev.javabybuilding.inventory.Product;
import java.math.BigDecimal;

/**
 * What the API tells a client about one product. It is a separate small record, NOT the {@link Product} class itself:
 * the API's shape is a promise to every client, and it must not change just because someone adds a field to the domain
 * class (see lesson 35 for what goes wrong when you send the domain object itself). Records like this one, whose only job
 * is to carry data across the wire, are called DTOs (data transfer objects).
 */
public record ProductResponse(String name, BigDecimal price, int quantity, int reorderLevel, String status, BigDecimal stockValue) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getName(), product.getPrice(), product.getQuantity(), product.getReorderLevel(),
                product.status(), product.stockValue());
    }
}
