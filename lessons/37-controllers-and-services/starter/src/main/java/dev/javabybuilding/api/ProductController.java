package dev.javabybuilding.api;

import dev.javabybuilding.inventory.ShopException;
import dev.javabybuilding.service.ProductService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * The WEB layer for products: it knows about HTTP (paths, methods, status codes, headers, JSON bodies) and about nothing else.
 * Every method does the same three things: take what Spring parsed from the request, call the service, shape the answer.
 * The rules are not here; they are in the service and in the shop core.
 *
 * <p>{@code @RequestMapping("/products")} puts that path in front of every method's own path.
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService products;

    public ProductController(ProductService products) {
        this.products = products;
    }

    @GetMapping
    public List<ProductResponse> list(@RequestParam(required = false) String status) {
        try {
            return products.list(status);
        } catch (ShopException e) {
            throw Errors.http(e);
        }
    }

    @GetMapping("/{name}")
    public ProductResponse one(@PathVariable String name) {
        try {
            return products.get(name);
        } catch (ShopException e) {
            throw Errors.http(e);
        }
    }

    /** {@code @RequestBody}: Spring (Jackson) turns the JSON of the request into a CreateProductRequest before this method runs. */
    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody CreateProductRequest request) {
        // TODO 4: call products.create(request) inside try { } catch (ShopException e) { throw Errors.http(e); } and answer
        // ResponseEntity.created(location).body(created), where location is
        // UriComponentsBuilder.fromPath("/products/{name}").buildAndExpand(created.name()).encode().toUri()  (a 201 with a Location header).
        throw new UnsupportedOperationException("TODO 4");
    }

    @PutMapping("/{name}")
    public ProductResponse update(@PathVariable String name, @RequestBody UpdateProductRequest request) {
        try {
            return products.update(name, request);
        } catch (ShopException e) {
            throw Errors.http(e);
        }
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204: it worked and there is nothing to send back
    public void delete(@PathVariable String name) {
        // TODO 4 (continued): products.delete(name), with the same try / catch (ShopException e) { throw Errors.http(e); }.
        throw new UnsupportedOperationException("TODO 4");
    }

    @PostMapping("/{name}/stock")
    public ProductResponse receiveStock(@PathVariable String name, @RequestBody ReceiveStockRequest request) {
        try {
            return products.receiveStock(name, request);
        } catch (ShopException e) {
            throw Errors.http(e);
        }
    }
}
