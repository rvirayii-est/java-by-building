package dev.javabybuilding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.javabybuilding.inventory.Inventory;
import dev.javabybuilding.inventory.Shop;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;

/**
 * Starts the WHOLE app (the container, Tomcat, everything) on a random free port and calls it with Java's HttpClient from
 * lesson 33. This is slow compared with a plain test (a second or two), so we keep few of them.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShopApplicationTest {

    @LocalServerPort
    int port;

    @Autowired
    ApplicationContext context;

    @Autowired
    ObjectMapper objectMapper; // nobody in OUR code created this: auto-configuration did

    private HttpResponse<String> get(String path) throws Exception {
        return HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void helloAnswersJson() throws Exception {
        HttpResponse<String> response = get("/hello");
        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("Content-Type").orElseThrow().startsWith("application/json"));
        assertEquals("{\"message\":\"Hello, world!\"}", response.body());
    }

    @Test
    void helloTakesANameFromTheQuery() throws Exception {
        assertEquals("{\"message\":\"Hello, Ana!\"}", get("/hello?name=Ana").body());
    }

    @Test
    void productsComeFromTheShopBean() throws Exception {
        String body = get("/products").body();
        JsonNode products = objectMapper.readTree(body);
        assertEquals(4, products.size());
        assertEquals("Rice 25kg", products.get(0).get("name").asText());
        assertTrue(body.startsWith("[{\"name\":\"Rice 25kg\",\"price\":1250.50,"), body); // two decimals kept: BigDecimal, not double
    }

    @Test
    void anUnknownProductIs404WithSpringsErrorJson() throws Exception {
        HttpResponse<String> response = get("/products/salt");
        assertEquals(404, response.statusCode());
        JsonNode error = objectMapper.readTree(response.body());
        assertEquals(404, error.get("status").asInt());
        assertEquals("Not Found", error.get("error").asText());
        assertEquals("Unknown product: salt", error.get("message").asText());
        assertEquals("/products/salt", error.get("path").asText());
    }

    @Test
    void anUnknownPathIs404TooWithoutAnyCodeOfOurs() throws Exception {
        assertEquals(404, get("/nothing").statusCode());
    }

    @Test
    void theContainerHoldsTheBeansOurRecipesAndAutoConfigurationMade() {
        Shop shop = context.getBean(Shop.class);
        assertInstanceOf(Inventory.class, shop);
        assertNotNull(context.getBean("helloController"));          // found by @ComponentScan
        assertNotNull(context.getBean("shop"));                      // made by our @Bean method ShopConfig.shop()
        assertTrue(context.containsBean("dispatcherServlet"));       // Spring MVC's front controller: auto-configured
        assertTrue(context.containsBean("tomcatWebServerFactoryCustomizer")); // so is the web server
        assertEquals("shop-api", context.getEnvironment().getProperty("spring.application.name"));
    }
}
