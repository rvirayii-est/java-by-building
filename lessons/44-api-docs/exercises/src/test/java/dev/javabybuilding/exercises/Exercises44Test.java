package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class Exercises44Test {

    @Test
    void operationIdsAreBuiltFromMethodAndPath() {
        assertEquals("getProducts", OpenApiDrills.operationId("GET", "/products"));
        assertEquals("getProductsByName", OpenApiDrills.operationId("GET", "/products/{name}"));
        assertEquals("postProductsByNameStock", OpenApiDrills.operationId("POST", "/products/{name}/stock"));
        assertEquals("getReportsStock", OpenApiDrills.operationId("get", "/reports/stock"));
    }

    @Test
    void aPathBecomesAJsonPointerKey() {
        assertEquals("~1products~1{name}", OpenApiDrills.escapeJsonPointer("/products/{name}"));
        assertEquals("a~0b~1c", OpenApiDrills.escapeJsonPointer("a~b/c"));
    }

    @Test
    void undocumentedListsWhatIsMappedButNotDescribed() {
        List<String> mapped = List.of("GET /products", "POST /sales", "GET /reports/stock", "GET /products");
        List<String> documented = List.of("GET /products");
        assertEquals(List.of("GET /reports/stock", "POST /sales"), OpenApiDrills.undocumented(mapped, documented));
        assertEquals(List.of(), OpenApiDrills.undocumented(documented, mapped));
    }

    @Test
    void statusPhrasesAreTheStandardWords() {
        assertEquals("Created", OpenApiDrills.statusPhrase(201));
        assertEquals("Conflict", OpenApiDrills.statusPhrase(409));
        assertEquals("Unsupported Media Type", OpenApiDrills.statusPhrase(415));
        assertEquals("Unknown", OpenApiDrills.statusPhrase(418));
    }
}
