package dev.javabybuilding.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.javabybuilding.inventory.Inventory;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Starts the real server on a free port (port 0) and talks to it with the JDK's HttpClient. Nothing is left behind. */
class ShopHttpServerTest {

    private ShopHttpServer server;
    private Inventory inventory;
    private final HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    void start() throws IOException {
        inventory = Inventory.sample();
        server = new ShopHttpServer(inventory, 0);
        server.start();
    }

    @AfterEach
    void stop() {
        server.close();
    }

    private HttpResponse<String> send(String method, String pathAndQuery, String body) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create("http://localhost:" + server.port() + pathAndQuery));
        request.method(method, body == null ? BodyPublishers.noBody() : BodyPublishers.ofString(body));
        return client.send(request.build(), BodyHandlers.ofString());
    }

    private HttpResponse<String> get(String pathAndQuery) throws Exception {
        return send("GET", pathAndQuery, null);
    }

    @Test
    void healthSaysOk() throws Exception {
        HttpResponse<String> response = get("/health");
        assertEquals(200, response.statusCode());
        assertEquals("OK", response.body());
        assertTrue(response.headers().firstValue("Content-Type").orElseThrow().startsWith("text/plain"));
    }

    @Test
    void theProductListHasOneLinePerProduct() throws Exception {
        HttpResponse<String> response = get("/products");
        assertEquals(200, response.statusCode());
        assertEquals("Rice 25kg,1250.50,12,OK\nCooking Oil 1L,98.00,4,LOW\nSugar 1kg,75.00,0,OUT\nSardines 155g,24.00,60,OK\n",
                response.body());
    }

    @Test
    void theStatusQueryFiltersTheList() throws Exception {
        assertEquals("Rice 25kg,1250.50,12,OK\nSardines 155g,24.00,60,OK\n", get("/products?status=ok").body());
        assertEquals("Sugar 1kg,75.00,0,OUT\n", get("/products?status=OUT").body());
        assertEquals("", get("/products?status=NOPE").body());
    }

    @Test
    void oneProductByNameWithSpacesInThePath() throws Exception {
        HttpResponse<String> response = get("/products/Rice%2025kg");
        assertEquals(200, response.statusCode());
        assertEquals("Rice 25kg,1250.50,12,OK", response.body());
    }

    @Test
    void anUnknownProductIs404WithTheShopsMessage() throws Exception {
        HttpResponse<String> response = get("/products/salt");
        assertEquals(404, response.statusCode());
        assertEquals("Unknown product: salt", response.body());
    }

    @Test
    void anUnknownPathIs404() throws Exception {
        HttpResponse<String> response = get("/nothing/here");
        assertEquals(404, response.statusCode());
        assertEquals("Not found: /nothing/here", response.body());
    }

    @Test
    void theWrongMethodIs405AndTheAllowHeaderSaysWhichOneWorks() throws Exception {
        HttpResponse<String> response = send("DELETE", "/products", null);
        assertEquals(405, response.statusCode());
        assertEquals("GET", response.headers().firstValue("Allow").orElseThrow());
        assertEquals(405, get("/sales").statusCode());
    }

    @Test
    void aSaleIs201AndTakesStockOffTheShelf() throws Exception {
        HttpResponse<String> response = send("POST", "/sales", "product=Rice+25kg&quantity=2");
        assertEquals(201, response.statusCode());
        assertEquals("Sale #1: 2 x Rice 25kg = PHP 2,501.00", response.body());
        assertEquals(10, inventory.quantityOf("Rice 25kg"));
    }

    @Test
    void aSaleOfMoreThanWeHaveIs409AndChangesNothing() throws Exception {
        HttpResponse<String> response = send("POST", "/sales", "product=Rice+25kg&quantity=13");
        assertEquals(409, response.statusCode());
        assertEquals("Not enough stock: only 12 of Rice 25kg left.", response.body());
        assertEquals(12, inventory.quantityOf("Rice 25kg"));
    }

    @Test
    void aSaleOfAnUnknownProductIs404() throws Exception {
        assertEquals(404, send("POST", "/sales", "product=salt&quantity=1").statusCode());
    }

    @Test
    void badFormFieldsAre400() throws Exception {
        assertEquals("Missing field: product", send("POST", "/sales", "quantity=1").body());
        assertEquals(400, send("POST", "/sales", "quantity=1").statusCode());
        assertEquals("quantity must be a whole number", send("POST", "/sales", "product=Rice+25kg&quantity=two").body());
        assertEquals(400, send("POST", "/sales", "product=Rice+25kg&quantity=0").statusCode());
    }
}
