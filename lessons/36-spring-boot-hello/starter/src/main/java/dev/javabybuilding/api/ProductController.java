package dev.javabybuilding.api;

import dev.javabybuilding.inventory.Shop;
import dev.javabybuilding.inventory.UnknownProductException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * The first real endpoints: read the products. The constructor asks for a {@link Shop}; Spring sees that, finds the bean that
 * ShopConfig made, and passes it in. This is called dependency injection, and it is why the controller never says
 * {@code new Inventory()}: it does not know (or care) where the shop's data lives.
 */
@RestController
public class ProductController {

    private final Shop shop;

    public ProductController(Shop shop) { // Spring calls this constructor and supplies the Shop
        this.shop = shop;
    }

    @GetMapping("/products")
    public List<ProductResponse> list() {
        // TODO 4: every product of the shop as a ProductResponse (shop.products(), a stream, map(ProductResponse::from), toList()).
        return List.of();
    }

    @GetMapping("/products/{name}") // {name} is a variable part of the path, like Http.segments in lesson 33
    public ProductResponse one(@PathVariable String name) {
        // TODO 4 (continued): ProductResponse.from(shop.getProduct(name)). getProduct throws UnknownProductException for an unknown
        // name: catch it and throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage()), which Spring turns into a 404.
        throw new UnsupportedOperationException("TODO 4");
    }
}
