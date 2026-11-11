package dev.javabybuilding.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * The one test that opens a REAL port and speaks real HTTP (with the JDK client of lesson 33). MockMvc never touches Tomcat, so
 * this is the only place that proves the packaged app starts and answers on a socket. One or two of these are enough: they are the
 * slowest tests of the project, and everything else is already covered without a network.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.datasource.url=jdbc:h2:mem:smoke;DB_CLOSE_DELAY=-1", "shop.seed-sample=true"})
class ServerSmokeTest {

    @LocalServerPort
    int port;

    private HttpResponse<String> get(String path) throws Exception {
        return HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void theAppAnswersOnARealPort() throws Exception {
        HttpResponse<String> products = get("/products/Rice%2025kg");
        assertEquals(200, products.statusCode());
        assertTrue(products.headers().firstValue("Content-Type").orElseThrow().startsWith("application/json"));
        assertTrue(products.body().startsWith("{\"name\":\"Rice 25kg\",\"price\":1250.50,"), products.body());
    }

    @Test
    void anErrorTravelsAsTheSameJsonOverTheWire() throws Exception {
        HttpResponse<String> response = get("/products/salt");
        assertEquals(404, response.statusCode());
        assertEquals("{\"status\":404,\"error\":\"Not Found\",\"message\":\"Unknown product: salt\",\"path\":\"/products/salt\"}", response.body());
    }
}
