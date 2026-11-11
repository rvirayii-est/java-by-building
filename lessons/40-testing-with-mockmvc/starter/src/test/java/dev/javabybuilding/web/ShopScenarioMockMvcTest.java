package dev.javabybuilding.web;

import static org.junit.jupiter.api.Assertions.fail;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Milestone M6, tested. The WHOLE application (controllers, services, the shop core, JPA, Flyway, an in-memory H2 database) behind
 * MockMvc: no port is opened and no HTTP happens, but every layer is real. The tests run in order and tell one story: the shop opens
 * with the four sample products, then a product is created, delivered, changed, sold, reported and deleted.
 *
 * <p>{@code @SpringBootTest} starts everything (about two seconds), so we keep few of these and let the slices carry the details.
 */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:mvc-it;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ShopScenarioMockMvcTest {

    @Autowired
    MockMvc mvc;

    private static MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder request, String body) {
        return request.contentType(MediaType.APPLICATION_JSON).content(body);
    }

    @Test
    @Order(1)
    void theShopOpensWithTheFourSampleProducts() throws Exception {
        mvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].name").value("Rice 25kg"))
                .andExpect(jsonPath("$[0].price").value(1250.50));
        mvc.perform(get("/products").param("status", "low"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Cooking Oil 1L"));
        mvc.perform(get("/reports/sales"))
                .andExpect(jsonPath("$.salesCount").value(0));
    }

    @Test
    @Order(2)
    void aProductIsCreatedAndThenReceived() throws Exception {
        mvc.perform(json(post("/products"), "{\"name\":\"Salt 1kg\",\"price\":20.00,\"reorderLevel\":5}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/products/Salt%201kg"))
                .andExpect(jsonPath("$.quantity").value(0))
                .andExpect(jsonPath("$.status").value("OUT"));
        mvc.perform(json(post("/products/{name}/stock", "Salt 1kg"), "{\"quantity\":24}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(24))
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    @Order(3)
    void aProductCanBeChangedAndTheStatusFollowsTheNewReorderLevel() throws Exception {
        mvc.perform(json(put("/products/{name}", "Salt 1kg"), "{\"price\":22.50,\"reorderLevel\":30}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(22.50))
                .andExpect(jsonPath("$.status").value("LOW")); // 24 on the shelf is at or below the new level of 30
    }

    @Test
    @Order(4)
    void aSaleTakesStockOffAndShowsUpInTheReports() throws Exception {
        // TODO 4: the whole application is real here. POST a sale of 2 "Rice 25kg" to /sales with customerName "Ana Reyes", payment
        // "GCASH" and discount "percent:10": expect 201, Location "/sales/1", discount "10% off", discountAmount 250.10, total 2250.90.
        // Then GET /products/Rice 25kg (quantity is now 10), GET /sales/1 (customer "Ana Reyes"), GET /reports/sales (salesCount 1,
        // revenue 2250.90, biggestSale 1).
        fail("TODO 4: write this test");
}

    @Test
    @Order(5)
    void aSaleThatCannotHappenChangesNothing() throws Exception {
        mvc.perform(json(post("/sales"), "{\"product\":\"Rice 25kg\",\"quantity\":99}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Not enough stock: only 10 of Rice 25kg left."));
        mvc.perform(get("/products/{name}", "Rice 25kg")).andExpect(jsonPath("$.quantity").value(10));
        mvc.perform(get("/reports/sales")).andExpect(jsonPath("$.salesCount").value(1));
    }

    @Test
    @Order(6)
    void onlyAProductWithNoHistoryCanBeDeleted() throws Exception {
        mvc.perform(delete("/products/{name}", "Salt 1kg")).andExpect(status().isConflict()); // it was received: it has history
        mvc.perform(json(post("/products"), "{\"name\":\"Pepper 50g\",\"price\":15.00,\"reorderLevel\":2}")).andExpect(status().isCreated());
        mvc.perform(delete("/products/{name}", "Pepper 50g")).andExpect(status().isNoContent()).andExpect(content().string(""));
        mvc.perform(get("/products/{name}", "Pepper 50g")).andExpect(status().isNotFound());
    }
}
