package dev.javabybuilding.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Products", description = "The catalogue and the shelves. Reading: admin and cashier. Changing: admin only.")
@RequestMapping("/products")
public class ProductController {

    private final ProductService products;

    public ProductController(ProductService products) {
        this.products = products;
    }

    /** The {@code @Pattern} on the parameter is checked by Spring too (an unknown status is a 400 with the problem named). */
    @Operation(summary = "List the products, optionally only those with status OK, LOW or OUT")
    @GetMapping
    public List<ProductResponse> list(@RequestParam(required = false)
                                      @Pattern(regexp = "(?i)OK|LOW|OUT", message = "must be OK, LOW or OUT") String status) {
        return products.list(status);
    }

    @Operation(summary = "Read one product")
    @GetMapping("/{name}")
    public ProductResponse one(@PathVariable String name) {
        return products.get(name);
    }

    @Operation(summary = "Create a product (it starts with 0 in stock)")
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse created = products.create(request);
        return ResponseEntity.created(UriComponentsBuilder.fromPath("/products/{name}").buildAndExpand(created.name()).encode().toUri())
                .body(created);
    }

    @Operation(summary = "Change a product's price and reorder level")
    @PutMapping("/{name}")
    public ProductResponse update(@PathVariable String name, @Valid @RequestBody UpdateProductRequest request) {
        return products.update(name, request);
    }

    @Operation(summary = "Delete a product that has no history")
    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String name) {
        products.delete(name);
    }

    @Operation(summary = "Receive a delivery: add units to the shelf")
    @PostMapping("/{name}/stock")
    public ProductResponse receiveStock(@PathVariable String name, @Valid @RequestBody ReceiveStockRequest request) {
        return products.receiveStock(name, request);
    }
}
