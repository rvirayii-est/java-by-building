package dev.javabybuilding.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.javabybuilding.inventory.ShopService;
import dev.javabybuilding.service.ProductService;
import dev.javabybuilding.service.ReportService;
import dev.javabybuilding.service.SaleService;
import dev.javabybuilding.service.TestShop;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

/** The web layer without a web server: call the controller methods and look at the status codes they choose. */
class ControllersTest {

    private final ShopService shop = TestShop.sample();
    private final ProductController products = new ProductController(new ProductService(shop));
    private final SaleController sales = new SaleController(new SaleService(shop));
    private final ReportController reports = new ReportController(new ReportService(shop));

    private static HttpStatus statusOf(Runnable call) {
        return (HttpStatus) assertThrows(ResponseStatusException.class, call::run).getStatusCode();
    }

    @Test
    void creatingAProductAnswers201WithTheEncodedLocation() {
        ResponseEntity<ProductResponse> response = products.create(new CreateProductRequest("Salt 1kg", new BigDecimal("20.00"), 5));
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/products/Salt%201kg", response.getHeaders().getLocation().toString());
        assertEquals("Salt 1kg", response.getBody().name());
    }

    @Test
    void theShopsProblemsBecomeTheRightStatuses() {
        assertEquals(HttpStatus.NOT_FOUND, statusOf(() -> products.one("salt")));
        assertEquals(HttpStatus.CONFLICT, statusOf(() -> products.create(new CreateProductRequest("Rice 25kg", new BigDecimal("1"), 1))));
        assertEquals(HttpStatus.BAD_REQUEST, statusOf(() -> products.create(new CreateProductRequest("Salt", new BigDecimal("0"), 1))));
        assertEquals(HttpStatus.BAD_REQUEST, statusOf(() -> products.list("HALF")));
        assertEquals(HttpStatus.NOT_FOUND, statusOf(() -> products.update("salt", new UpdateProductRequest(new BigDecimal("1"), 1))));
        assertEquals(HttpStatus.CONFLICT, statusOf(() -> products.delete("Rice 25kg")));
        assertEquals(HttpStatus.NOT_FOUND, statusOf(() -> products.receiveStock("salt", new ReceiveStockRequest(5))));
        assertEquals(HttpStatus.CONFLICT, statusOf(() -> sales.create(new CreateSaleRequest("Rice 25kg", 13, null, null, null, null))));
        assertEquals(HttpStatus.NOT_FOUND, statusOf(() -> sales.one(9)));
    }

    @Test
    void theMessageOfTheProblemTravelsWithTheStatus() {
        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> products.one("salt"));
        assertEquals("Unknown product: salt", e.getReason());
    }

    @Test
    void aSaleAnswers201WithItsLocation() {
        ResponseEntity<SaleResponse> response = sales.create(new CreateSaleRequest("Rice 25kg", 1, null, null, null, null));
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/sales/1", response.getHeaders().getLocation().toString());
        assertEquals(1, response.getBody().number());
    }

    @Test
    void readingGoesStraightThrough() {
        assertEquals(4, products.list(null).size());
        assertEquals(4, reports.stock().products().size());
        assertEquals(0, reports.sales().salesCount());
        assertEquals(0, sales.list(null).size());
    }
}
