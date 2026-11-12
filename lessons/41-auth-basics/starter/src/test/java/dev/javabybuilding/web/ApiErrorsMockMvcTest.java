package dev.javabybuilding.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Every kind of failure, through the real application: lesson 39's promises, now checked with MockMvc. None of these requests
 * changes the shop, so the tests share the one started application (and database) with ShopScenarioMockMvcTest: Spring sees the same
 * settings and starts it only once.
 */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:mvc-it;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
@WithMockUser // a signed-in user; the rules about who may do what are tested in AuthenticationTest
class ApiErrorsMockMvcTest {

    @Autowired
    MockMvc mvc;

    private ResultActions postJson(String path, String body) throws Exception {
        return mvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(body));
    }

    @Test
    void anEmptyObjectNamesEveryRequiredFieldInOneAnswer() throws Exception {
        postJson("/products", "{}")
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":400,\"error\":\"Bad Request\",\"message\":\"The request has invalid fields.\",\"path\":\"/products\","
                        + "\"problems\":[{\"field\":\"name\",\"message\":\"is required\"},{\"field\":\"price\",\"message\":\"is required\"},"
                        + "{\"field\":\"reorderLevel\",\"message\":\"is required\"}]}", true));
    }

    @Test
    void severalBadSaleFieldsComeBackSortedByField() throws Exception {
        postJson("/sales", "{\"product\":\"Rice 25kg\",\"quantity\":0,\"payment\":\"bitcoin\",\"discount\":\"half\",\"customerPhone\":\"call me\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.problems[*].field").value(org.hamcrest.Matchers.contains("customerPhone", "discount", "payment", "quantity")));
    }

    @Test
    void brokenJsonUnknownFieldsAndWrongTypesNameNothingInternal() throws Exception {
        postJson("/products", "{\"name\": ")
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":400,\"error\":\"Bad Request\",\"message\":\"The request body is not valid JSON.\",\"path\":\"/products\"}", true));
        postJson("/products", "{\"name\":\"Salt\",\"price\":1,\"reorderLvl\":1}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.problems[0].field").value("reorderLvl"))
                .andExpect(content().string(not(containsString("dev.javabybuilding"))));
        postJson("/products", "{\"name\":\"Salt\",\"price\":\"abc\",\"reorderLevel\":1}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.problems[0].message").value("has the wrong kind of value"));
        mvc.perform(post("/products").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The request body is required."))
                .andExpect(content().string(not(containsString("ProductController"))));
    }

    @Test
    void badParametersAreA400NotA404() throws Exception {
        mvc.perform(get("/sales/abc")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.problems[0].message").value("must be a whole number"));
        mvc.perform(get("/sales/0")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.problems[0].message").value("must be at least 1"));
        mvc.perform(get("/sales/99999")).andExpect(status().isNotFound());
        mvc.perform(get("/products").param("status", "HALF")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.problems[0].field").value("status"));
        mvc.perform(get("/sales").param("payment", "bitcoin")).andExpect(status().isBadRequest());
    }

    @Test
    void unknownThingsAre404WithTheExactShape() throws Exception {
        mvc.perform(get("/products/salt"))
                .andExpect(content().json("{\"status\":404,\"error\":\"Not Found\",\"message\":\"Unknown product: salt\",\"path\":\"/products/salt\"}", true));
        mvc.perform(get("/nothing"))
                .andExpect(content().json("{\"status\":404,\"error\":\"Not Found\",\"message\":\"No such path: /nothing\",\"path\":\"/nothing\"}", true));
        mvc.perform(delete("/products/salt")).andExpect(status().isNotFound());
        mvc.perform(post("/sales").contentType(MediaType.APPLICATION_JSON).content("{\"product\":\"salt\",\"quantity\":1}")).andExpect(status().isNotFound());
    }

    @Test
    void theWrongMethodIs405WithAllowAndTheWrongContentTypeIs415() throws Exception {
        mvc.perform(delete("/sales"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string("Allow", "GET, POST"))
                .andExpect(jsonPath("$.message").value("DELETE is not allowed here. Use GET, POST."));
        mvc.perform(post("/products").contentType(MediaType.TEXT_PLAIN).content("name=Salt"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.message").value("The Content-Type must be application/json."));
    }

    @Test
    void theShopsRefusalsAre409() throws Exception {
        postJson("/products", "{\"name\":\"rice 25KG\",\"price\":1,\"reorderLevel\":1}")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("That product already exists: Rice 25kg"));
        postJson("/sales", "{\"product\":\"Rice 25kg\",\"quantity\":99999}")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.startsWith("Not enough stock: only ")));
        mvc.perform(delete("/products/{name}", "Rice 25kg")).andExpect(status().isConflict());
    }

    @Test
    void everyErrorHasTheSameShapeAndNoMovingParts() throws Exception {
        ResultActions[] errors = {
            postJson("/products", "{}"), postJson("/products", "{\"name\": "), mvc.perform(get("/sales/abc")), mvc.perform(get("/nothing")),
            mvc.perform(get("/products/salt")), mvc.perform(delete("/sales")), postJson("/sales", "{\"product\":\"Rice 25kg\",\"quantity\":99999}")
        };
        for (ResultActions error : errors) {
            error.andExpect(jsonPath("$.status").isNumber())
                    .andExpect(jsonPath("$.error").isString())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.path").isString())
                    .andExpect(jsonPath("$.timestamp").doesNotExist())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        }
    }
}
