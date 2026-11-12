package dev.javabybuilding.web;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.javabybuilding.api.ProductController;
import dev.javabybuilding.api.ReportController;
import dev.javabybuilding.security.SecurityConfig;
import dev.javabybuilding.service.ProductService;
import dev.javabybuilding.service.ReportService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * The same rules as RolesTest, as a fast SLICE: no database, no real users. {@code @WithMockUser(roles = "CASHIER")} signs the
 * request in as a cashier without any password, and the services are fakes. The important check is verifyNoInteractions:
 * a refused request must never reach the service.
 */
@WebMvcTest({ProductController.class, ReportController.class})
@Import(SecurityConfig.class)
@WithMockUser(roles = "CASHIER")
class RolesWebTest {

    @Autowired MockMvc mvc;
    @MockitoBean ProductService products;
    @MockitoBean ReportService reports;

    @Test
    void aCashierMayReadProducts() throws Exception {
        when(products.list(null)).thenReturn(List.of());
        mvc.perform(get("/products")).andExpect(status().isOk());
    }

    @Test
    void aCashierMayNotCreateAProductAndTheServiceIsNeverCalled() throws Exception {
        mvc.perform(post("/products").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Salt\",\"price\":20,\"reorderLevel\":5}"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(products);
    }

    @Test
    void aCashierMayNotDeleteAProduct() throws Exception {
        mvc.perform(delete("/products/Rice 25kg")).andExpect(status().isForbidden());
        verifyNoInteractions(products);
    }

    @Test
    void aCashierMayNotReadReports() throws Exception {
        mvc.perform(get("/reports/stock")).andExpect(status().isForbidden());
        verifyNoInteractions(reports);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void anAdminMayReadReports() throws Exception {
        mvc.perform(get("/reports/stock")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void anAdminMayDeleteAProduct() throws Exception {
        mvc.perform(delete("/products/Rice 25kg")).andExpect(status().isNoContent());
    }
}
