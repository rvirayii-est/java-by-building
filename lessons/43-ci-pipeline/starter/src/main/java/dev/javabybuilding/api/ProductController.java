package dev.javabybuilding.api;

import dev.javabybuilding.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
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
 * The WEB layer for products. Compare with lesson 37: every {@code try { ... } catch (ShopException e) { throw Errors.http(e); }}
 * is gone. A method says what it does when things go RIGHT; what happens when they go wrong is decided in ONE place,
 * {@link ApiExceptionHandler}. And {@code @Valid} makes Spring check the annotations of the request record BEFORE the method runs.
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService products;

    public ProductController(ProductService products) {
        this.products = products;
    }

    /** The {@code @Pattern} on the parameter is checked by Spring too (an unknown status is a 400 with the problem named). */
    @GetMapping
    public List<ProductResponse> list(@RequestParam(required = false)
                                      @Pattern(regexp = "(?i)OK|LOW|OUT", message = "must be OK, LOW or OUT") String status) {
        return products.list(status);
    }

    @GetMapping("/{name}")
    public ProductResponse one(@PathVariable String name) {
        return products.get(name);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse created = products.create(request);
        return ResponseEntity.created(UriComponentsBuilder.fromPath("/products/{name}").buildAndExpand(created.name()).encode().toUri())
                .body(created);
    }

    @PutMapping("/{name}")
    public ProductResponse update(@PathVariable String name, @Valid @RequestBody UpdateProductRequest request) {
        return products.update(name, request);
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String name) {
        products.delete(name);
    }

    @PostMapping("/{name}/stock")
    public ProductResponse receiveStock(@PathVariable String name, @Valid @RequestBody ReceiveStockRequest request) {
        return products.receiveStock(name, request);
    }
}
