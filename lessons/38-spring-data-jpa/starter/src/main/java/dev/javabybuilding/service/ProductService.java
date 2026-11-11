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
 * <p>shortcut: the methods that CHANGE something take a lock on the shop, so two changes in this program never overlap. That
 * keeps two sales from selling the last bag of rice twice. Reading needs no lock: the connection pool (HikariCP) gives each
 * request its own connection. Several copies of the app at once would need the database to do the locking (a row lock or a
 * version column); that is beyond this course.
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
        return shop.products().stream()
                .filter(product -> status == null || product.status().equalsIgnoreCase(status))
                .map(ProductResponse::from)
                .toList();
    }

    public ProductResponse get(String name) {
        return ProductResponse.from(shop.getProduct(name));
    }

    public ProductResponse create(CreateProductRequest request) {
        String name = Checks.required(request.name(), "name");
        synchronized (shop) {
            if (shop.find(name) != null) {
                throw new ProductAlreadyExistsException(shop.find(name)); // a clash with what exists: 409
            }
            shop.addProduct(name, request.price(), request.reorderLevel());
            return ProductResponse.from(shop.getProduct(name));
        }
    }

    public ProductResponse update(String name, UpdateProductRequest request) {
        synchronized (shop) {
            return ProductResponse.from(shop.updateProduct(name, request.price(), request.reorderLevel()));
        }
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
