package dev.javabybuilding.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.javabybuilding.api.ShopApi;
import dev.javabybuilding.inventory.InMemoryProductRepository;
import dev.javabybuilding.inventory.InMemorySaleRepository;
import dev.javabybuilding.inventory.InMemoryStockMovementRepository;
import dev.javabybuilding.inventory.Inventory;
import dev.javabybuilding.inventory.ShopService;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** The whole stack except the browser: real HTTP, real JSON, the real ShopApi, an in-memory shop. Port 0 = a free port. */
class JsonServerTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-03-01T01:30:00Z"), Inventory.SHOP_ZONE);

    private JsonServer server;
    private ShopService shop;
    private final HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    void start() throws IOException {
        shop = new ShopService(new InMemoryProductRepository(), new InMemorySaleRepository(), new InMemoryStockMovementRepository(), CLOCK);
        shop.importFrom(Inventory.sample(new Inventory(CLOCK)));
        server = new JsonServer(new ShopApi(shop), 0);
        server.start();
    }

    @AfterEach
    void stop() {
        server.close();
    }

    private HttpResponse<String> send(String method, String path, String json) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create("http://localhost:" + server.port() + path));
        if (json != null) {
            request.header("Content-Type", "application/json");
        }
        request.method(method, json == null ? BodyPublishers.noBody() : BodyPublishers.ofString(json));
        return client.send(request.build(), BodyHandlers.ofString());
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asObject(HttpResponse<String> response) {
        return Json.readObject(response.body());
    }

    @Test
    void listingProductsGivesAJsonArray() throws Exception {
        HttpResponse<String> response = send("GET", "/products", null);
        assertEquals(200, response.statusCode());
        assertEquals("application/json", response.headers().firstValue("Content-Type").orElseThrow());
        List<?> products = Json.read(response.body(), List.class);
        assertEquals(4, products.size());
        assertTrue(response.body().startsWith("[{\"name\":\"Rice 25kg\",\"price\":1250.50,\"quantity\":12,"), response.body());
    }

    @Test
    void theStatusQueryFilters() throws Exception {
        List<?> low = Json.read(send("GET", "/products?status=LOW", null).body(), List.class);
        assertEquals(1, low.size());
    }

    @Test
    void creatingAProductIs201WithALocationAndTheNewProductInTheBody() throws Exception {
        HttpResponse<String> response = send("POST", "/products", "{\"name\":\"Salt 1kg\",\"price\":20.00,\"reorderLevel\":5}");
        assertEquals(201, response.statusCode());
        assertEquals("/products/Salt%201kg", response.headers().firstValue("Location").orElseThrow());
        assertEquals("{\"name\":\"Salt 1kg\",\"price\":20.00,\"quantity\":0,\"reorderLevel\":5,\"status\":\"OUT\",\"stockValue\":0.00}", response.body());
        assertEquals(200, send("GET", "/products/Salt%201kg", null).statusCode());
    }

    @Test
    void aSaleIs201AndTakesStockOff() throws Exception {
        HttpResponse<String> response = send("POST", "/sales", "{\"product\":\"Rice 25kg\",\"quantity\":2,\"payment\":\"GCASH\",\"discount\":\"percent:10\"}");
        assertEquals(201, response.statusCode());
        Map<String, Object> sale = asObject(response);
        assertEquals(1, sale.get("number"));
        assertEquals("GCASH", sale.get("payment"));
        assertEquals(new java.math.BigDecimal("2250.90"), sale.get("total"));
        assertEquals(10, shop.quantityOf("Rice 25kg"));
    }

    @Test
    void stockCanBeReceivedAndReportsAreJson() throws Exception {
        assertEquals(200, send("POST", "/products/Sugar%201kg/stock", "{\"quantity\":24}").statusCode());
        Map<String, Object> report = asObject(send("GET", "/reports/stock", null));
        assertEquals(new java.math.BigDecimal("18638.00"), report.get("totalValue")); // 16838.00 + 24 x 75.00
        assertEquals(200, send("GET", "/reports/sales", null).statusCode());
    }

    @Test
    void errorsHaveOneJsonShape() throws Exception {
        HttpResponse<String> unknown = send("GET", "/products/salt", null);
        assertEquals(404, unknown.statusCode());
        assertEquals("{\"status\":404,\"error\":\"Not Found\",\"message\":\"Unknown product: salt\",\"path\":\"/products/salt\"}", unknown.body());

        HttpResponse<String> conflict = send("POST", "/sales", "{\"product\":\"Rice 25kg\",\"quantity\":13}");
        assertEquals(409, conflict.statusCode());
        assertEquals("Not enough stock: only 12 of Rice 25kg left.", asObject(conflict).get("message"));
        assertEquals(12, shop.quantityOf("Rice 25kg"));
    }

    @Test
    void jsonThatIsBrokenOrNotAnObjectIs400() throws Exception {
        HttpResponse<String> broken = send("POST", "/products", "{\"name\": ");
        assertEquals(400, broken.statusCode());
        assertTrue(((String) asObject(broken).get("message")).startsWith("Malformed JSON"), broken.body());
        assertEquals(400, send("POST", "/products", "[1,2]").statusCode());
    }

    @Test
    void aTypoInAFieldNameIs400AndNamesTheField() throws Exception {
        HttpResponse<String> response = send("POST", "/products", "{\"name\":\"Salt\",\"price\":20,\"reorderLvl\":5}");
        assertEquals(400, response.statusCode());
        assertTrue(((String) asObject(response).get("message")).startsWith("Invalid value for 'reorderLvl'"), response.body());
        assertEquals(4, shop.products().size());
    }

    @Test
    void deletingAnewProductIs204WithNoBody() throws Exception {
        send("POST", "/products", "{\"name\":\"Salt 1kg\",\"price\":20.00,\"reorderLevel\":5}");
        HttpResponse<String> response = send("DELETE", "/products/Salt%201kg", null);
        assertEquals(204, response.statusCode());
        assertEquals("", response.body());
    }

    @Test
    void theWrongMethodIs405WithAnAllowHeader() throws Exception {
        HttpResponse<String> response = send("DELETE", "/products", null);
        assertEquals(405, response.statusCode());
        assertEquals("GET, POST", response.headers().firstValue("Allow").orElseThrow());
    }

    @Test
    void theSampleShopOfMainHasTheFourProducts() {
        assertEquals(4, JsonServer.sampleShop().products().size());
    }
}
