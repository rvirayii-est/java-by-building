package dev.javabybuilding.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.javabybuilding.api.ApiDesign;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Lesson 44: the API description (OpenAPI) is generated from the controllers. These tests read the real document, served at
 * /v3/api-docs, and check it against lesson 34's design table, so the documentation cannot drift away from the code.
 * Nobody signs in here: the description is public (it holds no shop data).
 */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:mvc-it;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class OpenApiTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private JsonNode document() throws Exception {
        String body = mvc.perform(get("/v3/api-docs")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return json.readTree(body);
    }

    @Test
    void theDescriptionIsServedWithoutAPasswordAsOpenApi3() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.openapi").value(org.hamcrest.Matchers.startsWith("3.")))
                .andExpect(jsonPath("$.info.title").value("Inventory & Sales API"));
    }

    @Test
    void everyEndpointOfTheDesignTableIsDocumented() throws Exception {
        JsonNode paths = document().get("paths");
        for (ApiDesign.Endpoint endpoint : ApiDesign.ENDPOINTS) {
            JsonNode item = paths.get(endpoint.path());
            assertTrue(item != null && item.has(endpoint.method().toLowerCase(Locale.ROOT)), endpoint.signature() + " is missing from /v3/api-docs");
        }
    }

    @Test
    void everyOperationHasASummaryAWriterChose() throws Exception {
        document().get("paths").forEach(item -> item.forEach(operation ->
                assertFalse(operation.path("summary").asText().isBlank(), "an operation has no summary: " + operation.path("operationId").asText())));
    }

    @Test
    void theSignInMethodIsDescribedAndOnlyHelloIsPublic() throws Exception {
        JsonNode doc = document();
        assertEquals("http", doc.at("/components/securitySchemes/basicAuth/type").asText());
        assertEquals("basic", doc.at("/components/securitySchemes/basicAuth/scheme").asText());
        assertEquals(0, doc.at("/paths/~1hello/get/security").size(), "/hello needs no sign-in");
        assertTrue(doc.at("/security/0").has("basicAuth"), "everything else does");
    }

    @Test
    void theErrorShapeIsDescribedAndSecuredOperationsListTheirFailures() throws Exception {
        JsonNode doc = document();
        assertTrue(doc.at("/components/schemas/ErrorResponse/properties").has("message"));
        assertEquals("#/components/schemas/ErrorResponse", doc.at("/paths/~1products/get/responses/401/content/application~1json/schema/$ref").asText());
        assertEquals("#/components/schemas/ErrorResponse", doc.at("/paths/~1reports~1stock/get/responses/403/content/application~1json/schema/$ref").asText());
        assertTrue(doc.at("/paths/~1hello/get/responses").has("200"));
        assertFalse(doc.at("/paths/~1hello/get/responses").has("401"));
    }

    @Test
    void theDescriptionContainsNoShopData() throws Exception {
        String body = mvc.perform(get("/v3/api-docs")).andReturn().getResponse().getContentAsString();
        assertFalse(body.contains("Rice 25kg"), "documentation describes the API, it must not carry the data");
        assertFalse(body.contains("admin123"));
    }

    @Test
    void theTryItPageIsReachableWithoutAPassword() throws Exception {
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
        mvc.perform(get("/swagger-ui.html")).andExpect(status().is3xxRedirection());
    }
}
