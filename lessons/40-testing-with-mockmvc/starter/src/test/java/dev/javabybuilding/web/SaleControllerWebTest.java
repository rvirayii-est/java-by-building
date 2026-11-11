package dev.javabybuilding.web;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.javabybuilding.api.CreateSaleRequest;
import dev.javabybuilding.api.SaleController;
import dev.javabybuilding.api.UnknownSaleException;
import dev.javabybuilding.inventory.InsufficientStockException;
import dev.javabybuilding.inventory.UnknownProductException;
import dev.javabybuilding.service.SaleService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(SaleController.class)
class SaleControllerWebTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    SaleService sales;

    private static MockHttpServletRequestBuilder json(String body) {
        return post("/sales").contentType(MediaType.APPLICATION_JSON).content(body);
    }

    @Test
    void aSaleAnswers201WithALocationAndTheReceipt() throws Exception {
        // TODO 3: when(sales.create(new CreateSaleRequest("Rice 25kg", 2, null, null, null, null))) returns Fixtures.saleOfTwoRice().
        // POST {"product":"Rice 25kg","quantity":2} to /sales (json(...) helper). Expect 201, Location "/sales/1", jsonPath of
        // $.number (1), $.soldAt ("2026-03-01T09:30:00"), $.customer ("Walk-in"), $.lines[0].lineTotal (2501.00), $.total, $.payment ("CASH").
        fail("TODO 3: write this test");
}

    @Test
    void everyFieldOfTheRequestReachesTheService() throws Exception {
        when(sales.create(new CreateSaleRequest("Rice 25kg", 1, "Ana Reyes", "0917 123 4567", "percent:10", "GCASH")))
                .thenReturn(Fixtures.saleOfTwoRice());

        mvc.perform(json("{\"product\":\"Rice 25kg\",\"quantity\":1,\"customerName\":\"Ana Reyes\",\"customerPhone\":\"0917 123 4567\","
                + "\"discount\":\"percent:10\",\"payment\":\"GCASH\"}"))
                .andExpect(status().isCreated());
        verify(sales).create(new CreateSaleRequest("Rice 25kg", 1, "Ana Reyes", "0917 123 4567", "percent:10", "GCASH"));
    }

    @Test
    void badFieldsAre400AndEveryProblemIsNamedInAFixedOrder() throws Exception {
        mvc.perform(json("{\"product\":\"Rice 25kg\",\"quantity\":0,\"payment\":\"bitcoin\",\"discount\":\"half\",\"customerPhone\":\"call me\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.problems.length()").value(4))
                .andExpect(jsonPath("$.problems[0].field").value("customerPhone"))
                .andExpect(jsonPath("$.problems[1].field").value("discount"))
                .andExpect(jsonPath("$.problems[2].field").value("payment"))
                .andExpect(jsonPath("$.problems[3].field").value("quantity"))
                .andExpect(jsonPath("$.problems[3].message").value("must be at least 1"));
        verifyNoInteractions(sales);
    }

    @Test
    void optionalFieldsMayBeLeftOutButProductAndQuantityMayNot() throws Exception {
        mvc.perform(json("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.problems.length()").value(2))
                .andExpect(jsonPath("$.problems[0].field").value("product"))
                .andExpect(jsonPath("$.problems[1].field").value("quantity"));
    }

    @Test
    void theShopsRefusalsKeepTheirStatuses() throws Exception {
        when(sales.create(new CreateSaleRequest("Rice 25kg", 99, null, null, null, null))).thenThrow(new InsufficientStockException("Rice 25kg", 99, 12));
        when(sales.create(new CreateSaleRequest("salt", 1, null, null, null, null))).thenThrow(new UnknownProductException("salt"));

        mvc.perform(json("{\"product\":\"Rice 25kg\",\"quantity\":99}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Not enough stock: only 12 of Rice 25kg left."));
        mvc.perform(json("{\"product\":\"salt\",\"quantity\":1}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listingCanBeFilteredByPayment() throws Exception {
        when(sales.list(null)).thenReturn(List.of(Fixtures.saleOfTwoRice()));
        when(sales.list("gcash")).thenReturn(List.of());

        mvc.perform(get("/sales")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/sales").param("payment", "gcash")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/sales").param("payment", "bitcoin"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.problems[0].field").value("payment"));
    }

    @Test
    void oneSaleIsFoundByItsNumber() throws Exception {
        when(sales.get(1)).thenReturn(Fixtures.saleOfTwoRice());
        when(sales.get(9)).thenThrow(new UnknownSaleException(9));

        mvc.perform(get("/sales/{number}", 1)).andExpect(status().isOk()).andExpect(jsonPath("$.number").value(1));
        mvc.perform(get("/sales/{number}", 9))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Unknown sale: #9"));
    }

    @Test
    void aSaleNumberThatIsNotAPositiveWholeNumberIs400NotA404() throws Exception {
        mvc.perform(get("/sales/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.problems[0].field").value("number"))
                .andExpect(jsonPath("$.problems[0].message").value("must be a whole number"));
        mvc.perform(get("/sales/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.problems[0].message").value("must be at least 1"));
        verifyNoInteractions(sales);
    }
}
