package dev.javabybuilding.web;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.javabybuilding.api.CreateProductRequest;
import dev.javabybuilding.api.ProductAlreadyExistsException;
import dev.javabybuilding.api.ProductController;
import dev.javabybuilding.api.ProductResponse;
import dev.javabybuilding.api.ReceiveStockRequest;
import dev.javabybuilding.api.UpdateProductRequest;
import dev.javabybuilding.inventory.InvalidInputException;
import dev.javabybuilding.inventory.Money;
import dev.javabybuilding.inventory.ProductInUseException;
import dev.javabybuilding.inventory.UnknownProductException;
import dev.javabybuilding.service.ProductService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * A web SLICE test. {@code @WebMvcTest(ProductController.class)} starts only the web part of Spring: this controller, the error
 * advice, Jackson and the validation. No Tomcat, no port, no database, no services: {@code @MockitoBean} puts a fake
 * ProductService in their place, and each test tells the fake what to answer ({@code when(...).thenReturn/thenThrow}).
 *
 * <p>{@link MockMvc} sends a request straight into Spring MVC, in the same JVM, and lets you look at the status, the headers and
 * the JSON of the answer. So what is tested here is exactly the web layer: URLs, methods, JSON in and out, validation, error answers.
 * The rules of the shop are not tested here (they have their own tests); that is the point of a slice.
 */
@WebMvcTest(ProductController.class)
class ProductControllerWebTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ProductService products; // a fake: Mockito makes it, Spring puts it where the controller expects the real one

    private static MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder request, String body) {
        return request.contentType(MediaType.APPLICATION_JSON).content(body);
    }

    // ---- reading ----

    @Test
    void listingAnswers200WithAJsonArray() throws Exception {
        when(products.list(null)).thenReturn(List.of(Fixtures.rice(), Fixtures.oil()));

        mvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Rice 25kg"))
                .andExpect(jsonPath("$[0].price").value(1250.50))
                .andExpect(jsonPath("$[1].status").value("LOW"));
    }

    @Test
    void theStatusFilterIsPassedToTheService() throws Exception {
        when(products.list("low")).thenReturn(List.of(Fixtures.oil()));

        mvc.perform(get("/products").param("status", "low"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Cooking Oil 1L"));
        verify(products).list("low");
    }

    @Test
    void aBadStatusFilterIs400AndTheServiceIsNeverCalled() throws Exception {
        mvc.perform(get("/products").param("status", "HALF"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.problems[0].field").value("status"))
                .andExpect(jsonPath("$.problems[0].message").value("must be OK, LOW or OUT"));
        verifyNoInteractions(products);
    }

    @Test
    void oneProductIsFoundByTheNameInThePath() throws Exception {
        when(products.get("Rice 25kg")).thenReturn(Fixtures.rice());

        mvc.perform(get("/products/{name}", "Rice 25kg")) // MockMvc encodes the space as %20 and Spring decodes it again
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Rice 25kg"))
                .andExpect(jsonPath("$.stockValue").value(15006.00));
    }

    @Test
    void anUnknownProductIs404WithTheStandardErrorShape() throws Exception {
        when(products.get("salt")).thenThrow(new UnknownProductException("salt"));

        mvc.perform(get("/products/salt"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Not Found\",\"message\":\"Unknown product: salt\",\"path\":\"/products/salt\"}", true));
    }

    // ---- creating ----

    @Test
    void creatingAnswers201WithALocationAndTheNewProduct() throws Exception {
        // TODO 1: teach the fake service what to answer, send the request, and check the answer.
        //   when(products.create(new CreateProductRequest("Salt 1kg", new BigDecimal("20.00"), 5)))
        //           .thenReturn(new ProductResponse("Salt 1kg", Money.of("20.00"), 0, 5, "OUT", Money.ZERO));
        //   mvc.perform(json(post("/products"), "{\"name\":\"Salt 1kg\",\"price\":20.00,\"reorderLevel\":5}"))
        //   then expect: status().isCreated(); header().string("Location", "/products/Salt%201kg"); jsonPath("$.name") is "Salt 1kg";
        //   jsonPath("$.quantity") is 0.
        fail("TODO 1: write this test");
}

    @Test
    void anEmptyBodyIs400WithEveryRequiredFieldNamedAndTheServiceIsNeverCalled() throws Exception {
        // TODO 2: POST "{}" to /products. Expect 400, jsonPath("$.message") "The request has invalid fields.", three problems
        // (jsonPath("$.problems.length()") is 3) named name, price and reorderLevel in that order, and the message "is required"
        // for reorderLevel. Finish with verifyNoInteractions(products): a bad request must never reach the service.
        fail("TODO 2: write this test");
}

    @Test
    void severalWrongValuesAreAllReported() throws Exception {
        mvc.perform(json(post("/products"), "{\"name\":\"  \",\"price\":0,\"reorderLevel\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.problems[?(@.field == 'name')].message").value("is required"))
                .andExpect(jsonPath("$.problems[?(@.field == 'price')].message").value("must be at least 0.01"))
                .andExpect(jsonPath("$.problems[?(@.field == 'reorderLevel')].message").value("must be 0 or more"));
    }

    @Test
    void brokenJsonUnknownFieldsAndWrongTypesAre400WithoutInternalDetails() throws Exception {
        mvc.perform(json(post("/products"), "{\"name\": "))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"The request body is not valid JSON.\"}"));
        mvc.perform(json(post("/products"), "{\"name\":\"A\",\"price\":1,\"reorderLvl\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.problems[0].field").value("reorderLvl"))
                .andExpect(jsonPath("$.problems[0].message").value("is not a known field"));
        mvc.perform(json(post("/products"), "{\"name\":\"A\",\"price\":\"abc\",\"reorderLevel\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.problems[0].message").value("has the wrong kind of value"));
        mvc.perform(post("/products").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The request body is required."));
        verifyNoInteractions(products);
    }

    @Test
    void aBodyThatIsNotJsonIs415() throws Exception {
        mvc.perform(post("/products").contentType(MediaType.TEXT_PLAIN).content("name=Salt"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.message").value("The Content-Type must be application/json."));
    }

    @Test
    void aNameThatIsTakenIs409AndBadShopInputIs400() throws Exception {
        when(products.create(any())).thenThrow(new ProductAlreadyExistsException("Rice 25kg"));
        mvc.perform(json(post("/products"), "{\"name\":\"rice 25KG\",\"price\":1,\"reorderLevel\":1}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("That product already exists: Rice 25kg"));

        // When a method is already stubbed, stub it again with doThrow(...).when(mock): when(mock.method()) would CALL the first stub.
        doThrow(new InvalidInputException("Price must be from PHP 0.01 to PHP 10,000.00.")).when(products).create(any());
        mvc.perform(json(post("/products"), "{\"name\":\"A\",\"price\":1,\"reorderLevel\":1}"))
                .andExpect(status().isBadRequest());
    }

    // ---- changing and deleting ----

    @Test
    void updatingPassesThePathNameAndTheBodyToTheService() throws Exception {
        when(products.update("Rice 25kg", new UpdateProductRequest(new BigDecimal("1300.00"), 15))).thenReturn(Fixtures.rice());

        mvc.perform(json(put("/products/{name}", "Rice 25kg"), "{\"price\":1300.00,\"reorderLevel\":15}"))
                .andExpect(status().isOk());
        verify(products).update("Rice 25kg", new UpdateProductRequest(new BigDecimal("1300.00"), 15));
    }

    @Test
    void anUpdateWithoutAPriceIs400() throws Exception {
        mvc.perform(json(put("/products/{name}", "Rice 25kg"), "{\"reorderLevel\":15}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.problems[0].field").value("price"));
        verifyNoInteractions(products);
    }

    @Test
    void deletingAnswers204WithNoBodyAndAProductWithHistoryIs409() throws Exception {
        mvc.perform(delete("/products/{name}", "Salt 1kg"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        verify(products).delete("Salt 1kg");

        doThrow(new ProductInUseException("Rice 25kg")).when(products).delete("Rice 25kg");
        mvc.perform(delete("/products/{name}", "Rice 25kg"))
                .andExpect(status().isConflict());
    }

    @Test
    void receivingStockAnswers200AndRefusesAQuantityBelowOne() throws Exception {
        when(products.receiveStock("Sugar 1kg", new ReceiveStockRequest(24))).thenReturn(Fixtures.rice());

        mvc.perform(json(post("/products/{name}/stock", "Sugar 1kg"), "{\"quantity\":24}"))
                .andExpect(status().isOk());
        mvc.perform(json(post("/products/{name}/stock", "Sugar 1kg"), "{\"quantity\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.problems[0].message").value("must be at least 1"));
    }

    // ---- the request does not fit ----

    @Test
    void theWrongMethodIs405WithAnAllowHeaderAndAnUnknownPathIs404() throws Exception {
        mvc.perform(delete("/products"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string("Allow", "GET, POST"));
        mvc.perform(get("/nothing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No such path: /nothing"));
    }
}
