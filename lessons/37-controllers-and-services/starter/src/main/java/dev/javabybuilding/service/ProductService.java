package dev.javabybuilding.service;

import dev.javabybuilding.api.CreateProductRequest;
import dev.javabybuilding.api.ProductAlreadyExistsException;
import dev.javabybuilding.api.ProductResponse;
import dev.javabybuilding.api.ReceiveStockRequest;
import dev.javabybuilding.api.UpdateProductRequest;
import dev.javabybuilding.inventory.InvalidInputException;
import dev.javabybuilding.inventory.ShopService;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * The use cases about products. A SERVICE sits between the web layer and the shop's rules: it knows nothing about HTTP
 * (no status codes, no headers), only about what the app can DO: list, find, create, change, delete, receive stock. It
 * turns the request records into calls on the {@link ShopService} and the answers into response records.
 *
 * <p>shortcut: every method locks on the shop because the database has ONE connection (lesson 29), which can talk to one
 * request at a time. Web servers run requests on many threads. Lesson 38 replaces the connection with a pool, so reading stops waiting.
 */
@Service
public class ProductService {

    private final ShopService shop;

    public ProductService(ShopService shop) {
        this.shop = shop;
    }

    /** All products, or only those with the status OK, LOW or OUT (any capitals). */
    public List<ProductResponse> list(String status) {
        if (status != null && !List.of("OK", "LOW", "OUT").contains(status.toUpperCase())) {
            throw new InvalidInputException("status must be OK, LOW or OUT");
        }
        synchronized (shop) {
            return shop.products().stream()
                    .filter(product -> status == null || product.status().equalsIgnoreCase(status))
                    .map(ProductResponse::from)
                    .toList();
        }
    }

    public ProductResponse get(String name) {
        synchronized (shop) {
            return ProductResponse.from(shop.getProduct(name));
        }
    }

    public ProductResponse create(CreateProductRequest request) {
        // TODO 2: name = Checks.required(request.name(), "name"). Then, inside synchronized (shop) { ... }: when shop.find(name) is not
        // null throw new ProductAlreadyExistsException(shop.find(name)) (a 409, not a 400); otherwise shop.addProduct(name,
        // request.price(), request.reorderLevel()) and return ProductResponse.from(shop.getProduct(name)).
        throw new UnsupportedOperationException("TODO 2");
    }

    public ProductResponse update(String name, UpdateProductRequest request) {
        // TODO 2 (continued): inside synchronized (shop): ProductResponse.from(shop.updateProduct(name, request.price(), request.reorderLevel())).
        throw new UnsupportedOperationException("TODO 2");
    }

    public void delete(String name) {
        synchronized (shop) {
            shop.deleteProduct(name);
        }
    }

    public ProductResponse receiveStock(String name, ReceiveStockRequest request) {
        synchronized (shop) {
            shop.receive(name, request.quantity());
            return ProductResponse.from(shop.getProduct(name));
        }
    }
}
