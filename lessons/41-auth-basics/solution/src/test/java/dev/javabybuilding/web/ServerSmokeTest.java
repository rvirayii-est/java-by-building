package dev.javabybuilding.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * The one test class that opens a REAL port and speaks real HTTP (with the JDK client of lesson 33). MockMvc never touches Tomcat, so
 * this is the only place that proves the packaged app starts and answers on a socket, with the real Authorization header. One or two
 * of these are enough: they are the slowest tests of the project, and everything else is already covered without a network.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.datasource.url=jdbc:h2:mem:smoke;DB_CLOSE_DELAY=-1", "shop.seed-sample=true"})
class ServerSmokeTest {

    @LocalServerPort
    int port;

    /** The header a browser or curl builds for  -u admin:admin123 : "Basic " + base64("admin:admin123"). */
    private static String basic(String user, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((user + ":" + password).getBytes(StandardCharsets.UTF_8));
    }

    private HttpResponse<String> get(String path, String authorization) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path));
        if (authorization != null) {
            request.header("Authorization", authorization);
        }
        return HttpClient.newHttpClient().send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void theAppAnswersOnARealPort() throws Exception {
        HttpResponse<String> products = get("/products/Rice%2025kg", basic("admin", "admin123"));
        assertEquals(200, products.statusCode());
        assertTrue(products.headers().firstValue("Content-Type").orElseThrow().startsWith("application/json"));
        assertTrue(products.body().startsWith("{\"name\":\"Rice 25kg\",\"price\":1250.50,"), products.body());
    }

    @Test
    void anErrorTravelsAsTheSameJsonOverTheWire() throws Exception {
        HttpResponse<String> response = get("/products/salt", basic("admin", "admin123"));
        assertEquals(404, response.statusCode());
        assertEquals("{\"status\":404,\"error\":\"Not Found\",\"message\":\"Unknown product: salt\",\"path\":\"/products/salt\"}", response.body());
    }

    @Test
    void withoutAPasswordTheAnswerIs401WithTheSameJsonShape() throws Exception {
        HttpResponse<String> response = get("/products", null);
        assertEquals(401, response.statusCode());
        assertEquals("Basic realm=\"shop\"", response.headers().firstValue("WWW-Authenticate").orElseThrow());
        assertEquals("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Authentication is required. Send a valid username and password.\","
                + "\"path\":\"/products\"}", response.body());
    }
}
