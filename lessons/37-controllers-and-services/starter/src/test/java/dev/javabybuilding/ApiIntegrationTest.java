package dev.javabybuilding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * The whole app on a random port with an in-memory database, used the way a client would: real HTTP and real JSON. The tests
 * run in order and share ONE shop (that is what makes it a story: create, receive, sell, report). Slow tests are few on purpose.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "shop.database.mode=memory")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    ObjectMapper json;

    private HttpResponse<String> call(String method, String path, String body) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path));
        if (body != null) {
            request.header("Content-Type", "application/json");
        }
        request.method(method, body == null ? BodyPublishers.noBody() : BodyPublishers.ofString(body));
        return HttpClient.newHttpClient().send(request.build(), BodyHandlers.ofString());
    }

    private JsonNode tree(HttpResponse<String> response) throws Exception {
        return json.readTree(response.body());
    }

    @Test
    @Order(1)
    void theFreshDatabaseHoldsTheFourSampleProducts() throws Exception {
        HttpResponse<String> response = call("GET", "/products", null);
        assertEquals(200, response.statusCode());
        assertEquals(4, tree(response).size());
        assertEquals(1, tree(call("GET", "/products?status=LOW", null)).size());
    }

    @Test
    @Order(2)
    void aProductIsCreatedReceivedAndFound() throws Exception {
        HttpResponse<String> created = call("POST", "/products", "{\"name\":\"Salt 1kg\",\"price\":20.00,\"reorderLevel\":5}");
        assertEquals(201, created.statusCode());
        assertEquals("/products/Salt%201kg", created.headers().firstValue("Location").orElseThrow());
        assertEquals(0, tree(created).get("quantity").asInt());

        HttpResponse<String> received = call("POST", "/products/Salt%201kg/stock", "{\"quantity\":24}");
        assertEquals(200, received.statusCode());
        assertEquals(24, tree(received).get("quantity").asInt());
        assertEquals("OK", tree(call("GET", "/products/Salt%201kg", null)).get("status").asText());
    }

    @Test
    @Order(3)
    void aProductCanBeChanged() throws Exception {
        HttpResponse<String> response = call("PUT", "/products/Salt%201kg", "{\"price\":22.50,\"reorderLevel\":30}");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"price\":22.50"), response.body());
        assertEquals("LOW", tree(response).get("status").asText()); // 24 on the shelf is at or below the new level of 30
    }

    @Test
    @Order(4)
    void aSaleTakesStockOffAndAppearsInTheReports() throws Exception {
        HttpResponse<String> sale = call("POST", "/sales",
                "{\"product\":\"Rice 25kg\",\"quantity\":2,\"customerName\":\"Ana Reyes\",\"payment\":\"GCASH\",\"discount\":\"percent:10\"}");
        assertEquals(201, sale.statusCode());
        assertEquals("/sales/1", sale.headers().firstValue("Location").orElseThrow());
        assertTrue(sale.body().contains("\"total\":2250.90"), sale.body());

        assertEquals(10, tree(call("GET", "/products/Rice%2025kg", null)).get("quantity").asInt());
        JsonNode salesReport = tree(call("GET", "/reports/sales", null));
        assertEquals(1, salesReport.get("salesCount").asInt());
        assertEquals(2250.90, salesReport.get("revenue").asDouble(), 0.0001);
        JsonNode stockReport = tree(call("GET", "/reports/stock", null));
        assertEquals(5, stockReport.get("products").size());
        assertEquals("Ana Reyes", tree(call("GET", "/sales/1", null)).get("customer").asText());
        assertEquals(1, tree(call("GET", "/sales?payment=gcash", null)).size());
    }

    @Test
    @Order(5)
    void problemsGetTheRightStatusAndAMessage() throws Exception {
        HttpResponse<String> unknown = call("GET", "/products/pepper", null);
        assertEquals(404, unknown.statusCode());
        assertEquals("Unknown product: pepper", tree(unknown).get("message").asText());

        HttpResponse<String> tooMany = call("POST", "/sales", "{\"product\":\"Rice 25kg\",\"quantity\":99}");
        assertEquals(409, tooMany.statusCode());
        assertEquals("Not enough stock: only 10 of Rice 25kg left.", tree(tooMany).get("message").asText());

        assertEquals(409, call("POST", "/products", "{\"name\":\"salt 1KG\",\"price\":1,\"reorderLevel\":1}").statusCode());
        assertEquals(409, call("DELETE", "/products/Salt%201kg", null).statusCode());
        assertEquals(400, call("POST", "/products", "{\"name\":\"Pepper\",\"price\":0,\"reorderLevel\":1}").statusCode());
        assertEquals(400, call("POST", "/products", "{\"name\":\"Pepper\",\"price\":1,\"reorderLvl\":1}").statusCode()); // a typo is refused
        assertEquals(400, call("POST", "/products", "{\"name\": ").statusCode());                                       // broken JSON
        assertEquals(400, call("GET", "/sales/abc", null).statusCode());
        assertEquals(404, call("GET", "/sales/99", null).statusCode());
        assertEquals(405, call("DELETE", "/sales", null).statusCode());
    }

    @Test
    @Order(6)
    void aProductWithNoHistoryCanBeDeleted() throws Exception {
        call("POST", "/products", "{\"name\":\"Pepper 50g\",\"price\":15.00,\"reorderLevel\":2}");
        HttpResponse<String> deleted = call("DELETE", "/products/Pepper%2050g", null);
        assertEquals(204, deleted.statusCode());
        assertEquals("", deleted.body());
        assertEquals(404, call("GET", "/products/Pepper%2050g", null).statusCode());
    }
}
