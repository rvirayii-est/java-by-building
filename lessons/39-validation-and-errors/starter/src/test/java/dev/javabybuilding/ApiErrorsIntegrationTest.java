package dev.javabybuilding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * Every way a request can go wrong, through the real app, and what the client reads in each case. None of these requests
 * changes the shop, so the tests can share the application (and the database) with ApiIntegrationTest: the settings below are
 * the same, which is how Spring knows it may reuse the one started application.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.datasource.url=jdbc:h2:mem:api-it;DB_CLOSE_DELAY=-1")
class ApiErrorsIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    ObjectMapper json;

    private HttpResponse<String> call(String method, String path, String body, String contentType) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path));
        if (contentType != null) {
            request.header("Content-Type", contentType);
        }
        request.method(method, body == null ? BodyPublishers.noBody() : BodyPublishers.ofString(body));
        return HttpClient.newHttpClient().send(request.build(), BodyHandlers.ofString());
    }

    private HttpResponse<String> call(String method, String path, String body) throws Exception {
        return call(method, path, body, body == null ? null : "application/json");
    }

    // ---- bad bodies: 400 with the fields named ----

    @Test
    void anEmptyObjectNamesEveryRequiredField() throws Exception {
        HttpResponse<String> response = call("POST", "/products", "{}");
        assertEquals(400, response.statusCode());
        assertEquals("{\"status\":400,\"error\":\"Bad Request\",\"message\":\"The request has invalid fields.\",\"path\":\"/products\","
                + "\"problems\":[{\"field\":\"name\",\"message\":\"is required\"},{\"field\":\"price\",\"message\":\"is required\"},"
                + "{\"field\":\"reorderLevel\",\"message\":\"is required\"}]}", response.body());
    }

    @Test
    void severalBadFieldsAreReportedTogetherInAFixedOrder() throws Exception {
        HttpResponse<String> response = call("POST", "/sales",
                "{\"product\":\"Rice 25kg\",\"quantity\":0,\"payment\":\"bitcoin\",\"discount\":\"half\",\"customerPhone\":\"call me\"}");
        assertEquals(400, response.statusCode());
        JsonNode problems = json.readTree(response.body()).get("problems");
        List<String> fields = new ArrayList<>();
        problems.forEach(problem -> fields.add(problem.get("field").asText()));
        assertEquals(List.of("customerPhone", "discount", "payment", "quantity"), fields);
        assertEquals("must be at least 1", problems.get(3).get("message").asText());
    }

    @Test
    void aFieldTheApiDoesNotKnowIsRefusedAndNamed() throws Exception {
        HttpResponse<String> response = call("POST", "/products", "{\"name\":\"Salt\",\"price\":1,\"reorderLvl\":1}");
        assertEquals(400, response.statusCode());
        JsonNode body = json.readTree(response.body());
        assertEquals("The request has a field that does not exist.", body.get("message").asText());
        assertEquals("reorderLvl", body.get("problems").get(0).get("field").asText());
        assertEquals("is not a known field", body.get("problems").get(0).get("message").asText());
        assertFalse(response.body().contains("dev.javabybuilding"), "no internal class names in an answer");
    }

    @Test
    void aValueOfTheWrongKindNamesTheField() throws Exception {
        HttpResponse<String> response = call("POST", "/products", "{\"name\":\"Salt\",\"price\":\"abc\",\"reorderLevel\":1}");
        assertEquals(400, response.statusCode());
        JsonNode problem = json.readTree(response.body()).get("problems").get(0);
        assertEquals("price", problem.get("field").asText());
        assertEquals("has the wrong kind of value", problem.get("message").asText());
        assertEquals(400, call("POST", "/products/Rice%2025kg/stock", "{\"quantity\":\"many\"}").statusCode());
    }

    @Test
    void brokenJsonAndAMissingBodyAreExplainedWithoutDetails() throws Exception {
        assertEquals("{\"status\":400,\"error\":\"Bad Request\",\"message\":\"The request body is not valid JSON.\",\"path\":\"/products\"}",
                call("POST", "/products", "{\"name\": ").body());
        HttpResponse<String> missing = call("POST", "/products", null, "application/json");
        assertEquals(400, missing.statusCode());
        assertEquals("The request body is required.", json.readTree(missing.body()).get("message").asText());
        assertFalse(missing.body().contains("ProductController"), "no internal class names in an answer");
    }

    // ---- bad parameters ----

    @Test
    void aBadSaleNumberIsA400NotA404() throws Exception {
        HttpResponse<String> text = call("GET", "/sales/abc", null);
        assertEquals(400, text.statusCode());
        assertEquals("must be a whole number", json.readTree(text.body()).get("problems").get(0).get("message").asText());
        assertEquals("number", json.readTree(text.body()).get("problems").get(0).get("field").asText());
        HttpResponse<String> zero = call("GET", "/sales/0", null);
        assertEquals(400, zero.statusCode());
        assertEquals("must be at least 1", json.readTree(zero.body()).get("problems").get(0).get("message").asText());
        assertEquals(404, call("GET", "/sales/99999", null).statusCode());
    }

    @Test
    void aBadFilterIsA400() throws Exception {
        HttpResponse<String> status = call("GET", "/products?status=HALF", null);
        assertEquals(400, status.statusCode());
        JsonNode problem = json.readTree(status.body()).get("problems").get(0);
        assertEquals("status", problem.get("field").asText());
        assertEquals("must be OK, LOW or OUT", problem.get("message").asText());
        assertEquals(400, call("GET", "/sales?payment=bitcoin", null).statusCode());
        assertEquals(200, call("GET", "/products?status=low", null).statusCode());
    }

    // ---- the request does not fit the API ----

    @Test
    void anUnknownPathAndAnUnknownProductAre404() throws Exception {
        assertEquals("{\"status\":404,\"error\":\"Not Found\",\"message\":\"No such path: /nothing\",\"path\":\"/nothing\"}",
                call("GET", "/nothing", null).body());
        assertEquals("{\"status\":404,\"error\":\"Not Found\",\"message\":\"Unknown product: salt\",\"path\":\"/products/salt\"}",
                call("GET", "/products/salt", null).body());
        assertEquals(404, call("PUT", "/products/salt", "{\"price\":1.00,\"reorderLevel\":1}").statusCode());
        assertEquals(404, call("DELETE", "/products/salt", null).statusCode());
    }

    @Test
    void theWrongMethodIs405WithAnAllowHeaderAndAWrongContentTypeIs415() throws Exception {
        HttpResponse<String> delete = call("DELETE", "/sales", null);
        assertEquals(405, delete.statusCode());
        assertEquals("GET, POST", delete.headers().firstValue("Allow").orElseThrow());
        assertEquals("DELETE is not allowed here. Use GET, POST.", json.readTree(delete.body()).get("message").asText());
        HttpResponse<String> text = call("POST", "/products", "name=Salt", "text/plain");
        assertEquals(415, text.statusCode());
        assertEquals("The Content-Type must be application/json.", json.readTree(text.body()).get("message").asText());
    }

    // ---- the shop's own refusals ----

    @Test
    void theShopsRefusalsKeepTheirStatusesAndMessages() throws Exception {
        HttpResponse<String> clash = call("POST", "/products", "{\"name\":\"rice 25KG\",\"price\":1,\"reorderLevel\":1}");
        assertEquals(409, clash.statusCode());
        assertEquals("That product already exists: Rice 25kg", json.readTree(clash.body()).get("message").asText());

        HttpResponse<String> tooMany = call("POST", "/sales", "{\"product\":\"Rice 25kg\",\"quantity\":99999}");
        assertEquals(409, tooMany.statusCode());
        assertTrue(json.readTree(tooMany.body()).get("message").asText().startsWith("Not enough stock: only "));

        assertEquals(409, call("DELETE", "/products/Rice%2025kg", null).statusCode());
        assertEquals(404, call("POST", "/sales", "{\"product\":\"salt\",\"quantity\":1}").statusCode());
    }

    // ---- one shape for all of them ----

    @Test
    void everyErrorHasTheSameShapeAndTheStatusInTheBodyMatchesTheStatusLine() throws Exception {
        List<HttpResponse<String>> errors = List.of(
                call("POST", "/products", "{}"), call("POST", "/products", "{\"name\": "), call("GET", "/sales/abc", null),
                call("GET", "/nothing", null), call("GET", "/products/salt", null), call("DELETE", "/sales", null),
                call("POST", "/sales", "{\"product\":\"Rice 25kg\",\"quantity\":99999}"), call("POST", "/products", "x", "text/plain"));
        for (HttpResponse<String> response : errors) {
            JsonNode body = json.readTree(response.body());
            assertEquals(response.statusCode(), body.get("status").asInt(), response.body());
            assertTrue(body.get("error").isTextual() && body.get("message").isTextual() && body.get("path").isTextual(), response.body());
            assertEquals("application/json", response.headers().firstValue("Content-Type").orElseThrow().split(";")[0]);
            assertFalse(body.has("timestamp"), "no moving parts in an error answer: " + response.body());
        }
    }
}
