package dev.javabybuilding.web;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.javabybuilding.api.HelloController;
import dev.javabybuilding.api.ReportController;
import dev.javabybuilding.api.SalesReportResponse;
import dev.javabybuilding.api.StockReportResponse;
import dev.javabybuilding.inventory.Money;
import dev.javabybuilding.service.ReportService;
import java.util.List;
import java.util.Map;
import dev.javabybuilding.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** One slice can cover several controllers: list them all in {@code @WebMvcTest(...)}. Only the ones that need a service get a fake. */
@WebMvcTest({ReportController.class, HelloController.class})
@Import(SecurityConfig.class) // use the real security rules, as the app does
@WithMockUser(roles = "ADMIN") // signed in as an admin; who may do what is tested in RolesTest and RolesWebTest
class ReportAndHelloWebTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ReportService reports;

    @Test
    void theStockReportHasProductsTotalAndReorderList() throws Exception {
        when(reports.stockReport()).thenReturn(new StockReportResponse(List.of(Fixtures.rice(), Fixtures.oil()), Money.of("15398.00"),
                List.of(new StockReportResponse.Reorder("Cooking Oil 1L", 16))));

        mvc.perform(get("/reports/stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products.length()").value(2))
                .andExpect(jsonPath("$.totalValue").value(15398.00))
                .andExpect(jsonPath("$.reorder[0].product").value("Cooking Oil 1L"))
                .andExpect(jsonPath("$.reorder[0].quantity").value(16));
    }

    @Test
    void theSalesReportShowsTheTotalsAndALackOfASaleAsNull() throws Exception {
        when(reports.salesReport()).thenReturn(new SalesReportResponse(0, Money.ZERO, Money.ZERO, Map.of(), null));

        mvc.perform(get("/reports/sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salesCount").value(0))
                .andExpect(jsonPath("$.revenue").value(0.00))
                .andExpect(jsonPath("$.biggestSale").value(nullValue()));
    }

    @Test
    void theLowStockReportListsWhatNeedsAttentionAsProducts() throws Exception {
        when(reports.lowStock()).thenReturn(List.of(Fixtures.oil()));

        mvc.perform(get("/reports/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Cooking Oil 1L"))
                .andExpect(jsonPath("$[0].status").exists());
    }

    @Test
    void helloGreetsTheWorldOrAName() throws Exception {
        mvc.perform(get("/hello")).andExpect(status().isOk()).andExpect(content().json("{\"message\":\"Hello, world!\"}", true));
        mvc.perform(get("/hello").param("name", "Ana")).andExpect(content().json("{\"message\":\"Hello, Ana!\"}", true));
        mvc.perform(get("/hello").param("name", "  ")).andExpect(jsonPath("$.message").value("Hello, world!"));
    }
}
