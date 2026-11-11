package dev.javabybuilding.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.javabybuilding.api.CreateProductRequest;
import dev.javabybuilding.api.ProductAlreadyExistsException;
import dev.javabybuilding.api.ProductResponse;
import dev.javabybuilding.api.ReceiveStockRequest;
import dev.javabybuilding.api.UpdateProductRequest;
import dev.javabybuilding.inventory.InvalidInputException;
import dev.javabybuilding.inventory.Money;
import dev.javabybuilding.inventory.ProductInUseException;
import dev.javabybuilding.inventory.UnknownProductException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

/** A service is plain Java: build it with a shop on in-memory repositories and call it. No Spring, no server, no database. */
class ProductServiceTest {

    private final ProductService service = new ProductService(TestShop.sample());

    @Test
    void listsAllProductsOrOnlyThoseWithAStatus() {
        assertEquals(4, service.list(null).size());
        assertEquals(List.of("Cooking Oil 1L"), service.list("low").stream().map(ProductResponse::name).toList());
        assertEquals(List.of("Rice 25kg", "Sardines 155g"), service.list("OK").stream().map(ProductResponse::name).toList());
    }

    @Test
    void anUnknownStatusIsRefused() {
        assertEquals("status must be OK, LOW or OUT", assertThrows(InvalidInputException.class, () -> service.list("HALF")).getMessage());
    }

    @Test
    void getFindsAProductIgnoringCapitalsOrFailsWithUnknownProduct() {
        assertEquals(12, service.get("  rice 25KG").quantity());
        assertThrows(UnknownProductException.class, () -> service.get("salt"));
    }

    @Test
    void createStartsWithNoStock() {
        ProductResponse created = service.create(new CreateProductRequest("Salt 1kg", new BigDecimal("20"), 5));
        assertEquals(new ProductResponse("Salt 1kg", Money.of("20.00"), 0, 5, "OUT", Money.of("0.00")), created);
        assertEquals(5, service.list(null).size());
    }

    @Test
    void createRefusesATakenNameWithAConflictNotABadRequest() {
        assertThrows(ProductAlreadyExistsException.class, () -> service.create(new CreateProductRequest("rice 25kg", new BigDecimal("1"), 1)));
    }

    @Test
    void createRefusesMissingAndBadFields() {
        assertEquals("Missing or empty field: name", assertThrows(InvalidInputException.class,
                () -> service.create(new CreateProductRequest(" ", new BigDecimal("1"), 1))).getMessage());
        assertThrows(InvalidInputException.class, () -> service.create(new CreateProductRequest("Salt", null, 1)));
        assertThrows(InvalidInputException.class, () -> service.create(new CreateProductRequest("Salt", new BigDecimal("1"), -1)));
        assertEquals(4, service.list(null).size());
    }

    @Test
    void updateChangesPriceAndReorderLevel() {
        ProductResponse updated = service.update("Rice 25kg", new UpdateProductRequest(new BigDecimal("1300.00"), 20));
        assertEquals(Money.of("1300.00"), updated.price());
        assertEquals("LOW", updated.status());
        assertThrows(UnknownProductException.class, () -> service.update("salt", new UpdateProductRequest(new BigDecimal("1"), 1)));
    }

    @Test
    void deleteWorksOnlyForAProductWithNoHistory() {
        service.create(new CreateProductRequest("Salt 1kg", new BigDecimal("20"), 5));
        service.delete("Salt 1kg");
        assertThrows(UnknownProductException.class, () -> service.get("Salt 1kg"));
        assertThrows(ProductInUseException.class, () -> service.delete("Rice 25kg"));
    }

    @Test
    void receivingStockAddsToTheShelf() {
        assertEquals(24, service.receiveStock("Sugar 1kg", new ReceiveStockRequest(24)).quantity());
        assertThrows(InvalidInputException.class, () -> service.receiveStock("Sugar 1kg", new ReceiveStockRequest(0)));
        assertThrows(UnknownProductException.class, () -> service.receiveStock("salt", new ReceiveStockRequest(5)));
    }
}
