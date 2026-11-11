package dev.javabybuilding.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.javabybuilding.api.ApiDesign.Endpoint;
import dev.javabybuilding.inventory.InMemoryProductRepository;
import dev.javabybuilding.inventory.InMemorySaleRepository;
import dev.javabybuilding.inventory.InMemoryStockMovementRepository;
import dev.javabybuilding.inventory.Inventory;
import dev.javabybuilding.inventory.ShopService;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** The design table is a promise. These tests keep the code and the table from drifting apart. */
class ApiDesignTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-03-01T01:30:00Z"), Inventory.SHOP_ZONE);

    private static ShopService newShop() {
        ShopService shop = new ShopService(new InMemoryProductRepository(), new InMemorySaleRepository(), new InMemoryStockMovementRepository(), CLOCK);
        shop.importFrom(Inventory.sample(new Inventory(CLOCK)));
        return shop;
    }

    @Test
    void theApiHasExactlyTheEndpointsOfTheDesignTable() {
        List<String> design = ApiDesign.ENDPOINTS.stream().map(Endpoint::signature).toList();
        assertEquals(design, new ShopApi(newShop()).routeTable());
    }

    @Test
    void everyEndpointAnswersWithItsDocumentedSuccessStatus() {
        for (Endpoint endpoint : ApiDesign.ENDPOINTS) {
            ShopApi api = new ShopApi(newShop()); // a fresh shop for each, so one call cannot spoil the next
            // Something to delete and something to look up: a product with no history, and sale number 1.
            api.handle(ApiRequest.of("POST", "/products", Map.of("name", "Salt 1kg", "price", "20.00", "reorderLevel", 5)));
            api.handle(ApiRequest.of("POST", "/sales", Map.of("product", "Rice 25kg", "quantity", 1)));
            ApiResponse response = api.handle(sampleRequest(endpoint));
            assertEquals(endpoint.success(), response.status(), endpoint.signature());
        }
    }

    /** One valid request for each endpoint of the table. */
    private static ApiRequest sampleRequest(Endpoint endpoint) {
        return switch (endpoint.signature()) {
            case "GET /products" -> ApiRequest.get("/products");
            case "GET /products/{name}" -> ApiRequest.get("/products/Rice%2025kg");
            case "POST /products" -> ApiRequest.of("POST", "/products", Map.of("name", "Pepper 50g", "price", "15.00", "reorderLevel", 5));
            case "PUT /products/{name}" -> ApiRequest.of("PUT", "/products/Rice%2025kg", Map.of("price", "1300.00", "reorderLevel", 10));
            case "DELETE /products/{name}" -> ApiRequest.of("DELETE", "/products/Salt%201kg", null);
            case "POST /products/{name}/stock" -> ApiRequest.of("POST", "/products/Sugar%201kg/stock", Map.of("quantity", 10));
            case "GET /sales" -> ApiRequest.get("/sales");
            case "GET /sales/{number}" -> ApiRequest.get("/sales/1");
            case "POST /sales" -> ApiRequest.of("POST", "/sales", Map.of("product", "Rice 25kg", "quantity", 1));
            case "GET /reports/stock" -> ApiRequest.get("/reports/stock");
            case "GET /reports/sales" -> ApiRequest.get("/reports/sales");
            default -> throw new IllegalArgumentException("Add a sample request for " + endpoint.signature());
        };
    }

    @Test
    void thePathsFollowTheRestNamingRules() {
        for (Endpoint endpoint : ApiDesign.ENDPOINTS) {
            for (String part : endpoint.path().split("/")) {
                if (!part.isEmpty() && !part.startsWith("{")) {
                    assertTrue(part.matches("[a-z]+"), endpoint.path() + ": use lower-case nouns in a path, not " + part);
                    assertTrue(List.of("get", "create", "delete", "update", "add", "remove", "set").stream().noneMatch(part::startsWith),
                            endpoint.path() + ": the method is the verb, so keep verbs out of the path");
                }
            }
        }
    }

    @Test
    void creatingEndpointsAnswer201AndAlwaysWhenTheyMakeANewThing() {
        List<String> created = ApiDesign.ENDPOINTS.stream().filter(e -> e.success() == 201).map(Endpoint::signature).toList();
        assertEquals(List.of("POST /products", "POST /sales"), created);
    }
}
