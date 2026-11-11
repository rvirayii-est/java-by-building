package dev.javabybuilding.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.javabybuilding.inventory.Inventory;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class ProductControllerTest {

    private final ProductController controller = new ProductController(Inventory.sample());

    @Test
    void listsTheFourSampleProducts() {
        List<ProductResponse> products = controller.list();
        assertEquals(List.of("Rice 25kg", "Cooking Oil 1L", "Sugar 1kg", "Sardines 155g"), products.stream().map(ProductResponse::name).toList());
    }

    @Test
    void findsOneProductByName() {
        ProductResponse rice = controller.one("rice 25kg");
        assertEquals("Rice 25kg", rice.name());
        assertEquals(12, rice.quantity());
        assertEquals("OK", rice.status());
    }

    @Test
    void anUnknownProductIsA404() {
        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> controller.one("salt"));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        assertEquals("Unknown product: salt", e.getReason());
    }
}
