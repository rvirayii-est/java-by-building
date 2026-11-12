package dev.javabybuilding.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * MILESTONE M7, tested: one story told over REAL HTTP on a real port, through every layer the course built, with the security on:
 * the filter chain, BCrypt logins from the database, roles, validation, the shop's rules, JPA and Flyway, the actuator, the API
 * description. Nothing is faked and MockMvc is not used.
 *
 * <p>The tests run in order and share one application and one in-memory database, like the scenario test of lesson 40. This is the
 * ONE place where that is acceptable: a story. Everywhere else in the project tests are independent.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.datasource.url=jdbc:h2:mem:m7;DB_CLOSE_DELAY=-1", "shop.seed-sample=true"})
@AutoConfigureObservability
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EndToEndScenarioTest {

    @LocalServerPort int port;

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    record Reply(int status, HttpResponse<String> raw, JsonNode body) {
        String header(String name) {
            return raw.headers().firstValue(name).orElse(null);
        }
    }

    private Reply call(String method, String path, String user, String body) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
        if (body != null) {
            request.header("Content-Type", "application/json");
        }
        if (user != null) {
            String password = user.equals("admin") ? "admin123" : "cashier123";
            request.header("Authorization", "Basic " + Base64.getEncoder().encodeToString((user + ":" + password).getBytes(StandardCharsets.UTF_8)));
        }
        HttpResponse<String> response = HTTP.send(request.build(), HttpResponse.BodyHandlers.ofString());
        JsonNode json = response.body().isBlank() ? null : JSON.readTree(response.body());
        return new Reply(response.statusCode(), response, json);
    }

    private Reply get(String path, String user) throws Exception {
        return call("GET", path, user, null);
    }

    // ---- Act 1: a stranger ----

    @Test
    @Order(1)
    void aStrangerSeesTheGreetingTheHealthAndTheDocumentationAndNothingElse() throws Exception {
        assertEquals(200, get("/hello", null).status());
        Reply health = get("/actuator/health", null);
        assertEquals(200, health.status());
        assertEquals("UP", health.body().get("status").asText());
        assertFalse(health.body().has("components"), "a stranger gets the verdict, not the details");
        Reply docs = get("/v3/api-docs", null);
        assertEquals(200, docs.status());
        assertTrue(docs.body().get("paths").has("/reports/low-stock"));

        Reply refused = get("/products", null);
        assertEquals(401, refused.status());
        assertEquals("Unauthorized", refused.body().get("error").asText());
        assertEquals("Basic realm=\"shop\"", refused.header("WWW-Authenticate"));
        assertNotNull(refused.header("X-Request-Id"), "even a refused request has an id for the log");
        assertEquals(200, get("/products", "admin").status(), "the same request with a password is fine");
    }

    // ---- Act 2: the cashier's day ----

    @Test
    @Order(2)
    void theCashierSeesTheFourSampleProductsButCannotChangeTheCatalogueOrReadReports() throws Exception {
        Reply products = get("/products", "cashier");
        assertEquals(200, products.status());
        assertEquals(4, products.body().size());
        assertEquals("Rice 25kg", products.body().get(0).get("name").asText());

        Reply create = call("POST", "/products", "cashier", "{\"name\":\"Salt 1kg\",\"price\":20.50,\"reorderLevel\":5}");
        assertEquals(403, create.status());
        assertEquals("Forbidden", create.body().get("error").asText());
        assertEquals(403, get("/reports/stock", "cashier").status());
        assertEquals(403, get("/actuator/metrics", "cashier").status());
        assertEquals(404, get("/products/Salt%201kg", "admin").status(), "the refused request created nothing");
    }

    // ---- Act 3: the admin sets up a product ----

    @Test
    @Order(3)
    void theAdminCreatesAProductAndReceivesADelivery() throws Exception {
        Reply created = call("POST", "/products", "admin", "{\"name\":\"Salt 1kg\",\"price\":20.50,\"reorderLevel\":5}");
        assertEquals(201, created.status());
        assertEquals("/products/Salt%201kg", created.header("Location"));
        assertEquals("OUT", created.body().get("status").asText());

        Reply invalid = call("POST", "/products", "admin", "{}");
        assertEquals(400, invalid.status());
        assertTrue(invalid.body().get("problems").size() >= 3, "validation names every missing field");
        assertEquals(409, call("POST", "/products", "admin", "{\"name\":\"Salt 1kg\",\"price\":20.50,\"reorderLevel\":5}").status());

        Reply delivery = call("POST", "/products/Salt%201kg/stock", "admin", "{\"quantity\":20}");
        assertEquals(200, delivery.status());
        assertEquals(20, delivery.body().get("quantity").asInt());
        assertEquals("OK", delivery.body().get("status").asText());
    }

    // ---- Act 4: customers ----

    @Test
    @Order(4)
    void theCashierSellsAndTheStockGoesDown() throws Exception {
        Reply sale = call("POST", "/sales", "cashier", "{\"product\":\"Salt 1kg\",\"quantity\":3}");
        assertEquals(201, sale.status());
        assertEquals("/sales/1", sale.header("Location"));
        assertEquals(61.50, sale.body().get("total").asDouble(), 0.001);
        assertEquals(17, get("/products/Salt%201kg", "cashier").body().get("quantity").asInt());

        Reply listed = get("/sales", "cashier");
        assertEquals(1, listed.body().size());
        assertEquals(200, get("/sales/1", "cashier").status());
    }

    @Test
    @Order(5)
    void aSaleThatCannotHappenChangesNothing() throws Exception {
        Reply tooMany = call("POST", "/sales", "cashier", "{\"product\":\"Salt 1kg\",\"quantity\":99999}");
        assertEquals(409, tooMany.status());
        assertEquals(17, get("/products/Salt%201kg", "cashier").body().get("quantity").asInt());
        assertEquals(1, get("/sales", "cashier").body().size(), "no receipt was written");
        assertEquals(404, call("POST", "/sales", "cashier", "{\"product\":\"Nothing\",\"quantity\":1}").status());
    }

    // ---- Act 5: the admin looks at the numbers ----

    @Test
    @Order(6)
    void theAdminReadsTheReports() throws Exception {
        Reply sales = get("/reports/sales", "admin");
        assertEquals(200, sales.status());
        assertEquals(1, sales.body().get("salesCount").asInt());
        assertEquals(61.50, sales.body().get("revenue").asDouble(), 0.001);

        Reply stock = get("/reports/stock", "admin");
        assertEquals(200, stock.status());
        assertEquals(5, stock.body().get("products").size());

        Reply low = get("/reports/low-stock", "admin");
        assertEquals(200, low.status());
        assertEquals("Sugar 1kg", low.body().get(0).get("name").asText());
        assertEquals("OUT", low.body().get(0).get("status").asText());
        assertEquals("Cooking Oil 1L", low.body().get(1).get("name").asText());
        assertEquals(2, low.body().size());
    }

    @Test
    @Order(7)
    void aProductWithHistoryCannotBeDeletedButOneWithoutCan() throws Exception {
        assertEquals(403, call("DELETE", "/products/Salt%201kg", "cashier", null).status());
        assertEquals(409, call("DELETE", "/products/Salt%201kg", "admin", null).status(), "it has a sale and a delivery in its history");
        call("POST", "/products", "admin", "{\"name\":\"Temporary\",\"price\":1,\"reorderLevel\":1}");
        assertEquals(204, call("DELETE", "/products/Temporary", "admin", null).status());
        assertEquals(404, get("/products/Temporary", "admin").status());
    }

    // ---- Act 6: operating the system ----

    @Test
    @Order(8)
    void theAdminCanSeeHowTheSystemIsDoing() throws Exception {
        Reply health = get("/actuator/health", "admin");
        assertEquals("UP", health.body().get("components").get("db").get("status").asText());
        assertEquals("UP", get("/actuator/health/readiness", null).body().get("status").asText());

        Reply requests = get("/actuator/metrics/http.server.requests", "admin");
        assertEquals(200, requests.status());
        double count = requests.body().get("measurements").get(0).get("value").asDouble();
        assertTrue(count > 10, "the story above made many requests: " + count);

        assertEquals(404, get("/actuator/env", "admin").status(), "not exposed, even to the admin");
        Reply me = get("/me", "cashier");
        assertEquals("cashier", me.body().get("username").asText());
        assertEquals("ROLE_CASHIER", me.body().get("authorities").get(0).asText());
    }

    @Test
    @Order(9)
    void aCallersRequestIdComesBackAndAHostileOneIsReplaced() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/hello")).header("X-Request-Id", "demo-123").build();
        assertEquals("demo-123", HTTP.send(request, HttpResponse.BodyHandlers.ofString()).headers().firstValue("X-Request-Id").orElseThrow());
        HttpRequest hostile = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/hello")).header("X-Request-Id", "has space and more than the allowed characters").build();
        String replaced = HTTP.send(hostile, HttpResponse.BodyHandlers.ofString()).headers().firstValue("X-Request-Id").orElseThrow();
        assertTrue(replaced.matches("[0-9a-f-]{36}"), replaced);
    }
}
