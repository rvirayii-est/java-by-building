package dev.javabybuilding.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.javabybuilding.inventory.InsufficientStockException;
import dev.javabybuilding.inventory.InvalidInputException;
import dev.javabybuilding.inventory.ProductInUseException;
import dev.javabybuilding.inventory.ShopService;
import dev.javabybuilding.inventory.UnknownProductException;
import dev.javabybuilding.service.ProductService;
import dev.javabybuilding.service.ReportService;
import dev.javabybuilding.service.SaleService;
import dev.javabybuilding.service.TestShop;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * The web layer without a web server. Since lesson 39 a controller method does not catch anything: a problem simply comes out as an
 * exception (the ApiExceptionHandler, tested separately, turns it into an answer). So a controller test checks the happy path and
 * that the shop's exceptions are NOT swallowed on the way.
 */
class ControllersTest {

    private final ShopService shop = TestShop.sample();
    private final ProductController products = new ProductController(new ProductService(shop));
    private final SaleController sales = new SaleController(new SaleService(shop));
    private final ReportController reports = new ReportController(new ReportService(shop));

    @Test
    void creatingAProductAnswers201WithTheEncodedLocation() {
        ResponseEntity<ProductResponse> response = products.create(new CreateProductRequest("Salt 1kg", new BigDecimal("20.00"), 5));
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/products/Salt%201kg", response.getHeaders().getLocation().toString());
        assertEquals("Salt 1kg", response.getBody().name());
    }

    @Test
    void aSaleAnswers201WithItsLocation() {
        ResponseEntity<SaleResponse> response = sales.create(new CreateSaleRequest("Rice 25kg", 1, null, null, null, null));
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/sales/1", response.getHeaders().getLocation().toString());
        assertEquals(1, response.getBody().number());
    }

    @Test
    void theShopsProblemsComeOutAsTheirOwnExceptions() {
        assertThrows(UnknownProductException.class, () -> products.one("salt"));
        assertThrows(ProductAlreadyExistsException.class, () -> products.create(new CreateProductRequest("Rice 25kg", new BigDecimal("1"), 1)));
        assertThrows(InvalidInputException.class, () -> products.create(new CreateProductRequest("Salt", new BigDecimal("0"), 1)));
        assertThrows(UnknownProductException.class, () -> products.update("salt", new UpdateProductRequest(new BigDecimal("1"), 1)));
        assertThrows(ProductInUseException.class, () -> products.delete("Rice 25kg"));
        assertThrows(UnknownProductException.class, () -> products.receiveStock("salt", new ReceiveStockRequest(5)));
        assertThrows(InsufficientStockException.class, () -> sales.create(new CreateSaleRequest("Rice 25kg", 13, null, null, null, null)));
        assertThrows(UnknownSaleException.class, () -> sales.one(9));
    }

    @Test
    void readingGoesStraightThrough() {
        assertEquals(4, products.list(null).size());
        assertEquals(4, reports.stock().products().size());
        assertEquals(0, reports.sales().salesCount());
        assertEquals(0, sales.list(null).size());
    }
}
