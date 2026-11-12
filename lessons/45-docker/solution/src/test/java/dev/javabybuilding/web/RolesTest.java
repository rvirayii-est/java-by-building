package dev.javabybuilding.web;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Lesson 42: the rules table of SecurityConfig, tested end to end with the REAL users from the database (V4 + V5 + the seeder).
 *
 * <p>It has its OWN in-memory database (mvc-roles) because one test really records a sale, and the story test of lesson 40 expects a pristine shop.
 *
 * <p>Every row of the matrix sends a request that does no harm when it is allowed: an empty body (answered 400 by validation), or a
 * product that does not exist (404). So "allowed" simply means "the security filter let it through": any status except 401 and 403.
 */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:mvc-roles;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class RolesTest {

    @Autowired MockMvc mvc;

    /** One row of the matrix: a request, and whether an admin, a cashier and a stranger (nobody signed in) may send it. */
    record Row(HttpMethod method, String path, boolean admin, boolean cashier) {
        @Override
        public String toString() {
            return method + " " + path + "  (admin " + (admin ? "yes" : "no") + ", cashier " + (cashier ? "yes" : "no") + ")";
        }
    }

    static Stream<Row> matrix() {
        return Stream.of(
                new Row(HttpMethod.GET, "/products", true, true),
                new Row(HttpMethod.GET, "/products/Rice", true, true),
                new Row(HttpMethod.GET, "/sales", true, true),
                new Row(HttpMethod.GET, "/sales/1", true, true),
                new Row(HttpMethod.POST, "/sales", true, true),
                new Row(HttpMethod.POST, "/products", true, false),
                new Row(HttpMethod.PUT, "/products/nothing", true, false),
                new Row(HttpMethod.DELETE, "/products/nothing", true, false),
                new Row(HttpMethod.POST, "/products/nothing/stock", true, false),
                new Row(HttpMethod.GET, "/reports/stock", true, false),
                new Row(HttpMethod.GET, "/reports/sales", true, false),
                new Row(HttpMethod.GET, "/me", true, true));
    }

    private MvcResult send(Row row, String user, String password) throws Exception {
        var request = request(row.method(), row.path()).contentType(MediaType.APPLICATION_JSON).content("{}");
        if (user != null) {
            request = request.with(httpBasic(user, password));
        }
        return mvc.perform(request).andReturn();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("matrix")
    void theAdminMayDoWhatTheTableSays(Row row) throws Exception {
        int status = send(row, "admin", "admin123").getResponse().getStatus();
        assertEquals(row.admin(), status != 401 && status != 403, row + " -> " + status);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("matrix")
    void theCashierMayDoWhatTheTableSays(Row row) throws Exception {
        int status = send(row, "cashier", "cashier123").getResponse().getStatus();
        assertEquals(row.cashier(), status != 401 && status != 403, row + " -> " + status);
        if (!row.cashier()) {
            assertEquals(403, status, "a signed-in user who lacks the role gets 403, not 401");
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("matrix")
    void aStrangerIsAlways401(Row row) throws Exception {
        assertEquals(401, send(row, null, null).getResponse().getStatus());
    }

    @Test
    void aRefusedCashierGetsTheSameJsonShapeAsEveryOtherError() throws Exception {
        mvc.perform(get("/reports/stock").with(httpBasic("cashier", "cashier123")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Your role is not allowed to do this."))
                .andExpect(jsonPath("$.path").value("/reports/stock"));
    }

    @Test
    void theRolesComeFromTheDatabase() throws Exception {
        mvc.perform(get("/me").with(httpBasic("admin", "admin123"))).andExpect(jsonPath("$.authorities", hasItem("ROLE_ADMIN")));
        mvc.perform(get("/me").with(httpBasic("cashier", "cashier123"))).andExpect(jsonPath("$.authorities", hasItem("ROLE_CASHIER")));
    }

    @Test
    void aPathNobodyListedIsClosedToTheCashierButOpenToTheAdminWhoSeesA404() throws Exception {
        assertEquals(403, mvc.perform(get("/nothing").with(httpBasic("cashier", "cashier123"))).andReturn().getResponse().getStatus());
        assertEquals(404, mvc.perform(get("/nothing").with(httpBasic("admin", "admin123"))).andReturn().getResponse().getStatus());
    }

    @Test
    void aCashierCanReallyRecordASaleAndThenSeeItButNeverTheReport() throws Exception {
        assertNotEquals(403, mvc.perform(request(HttpMethod.POST, "/sales").contentType(MediaType.APPLICATION_JSON)
                .content("{\"product\":\"Rice 25kg\",\"quantity\":1}").with(httpBasic("cashier", "cashier123"))).andReturn().getResponse().getStatus());
        mvc.perform(get("/sales").with(httpBasic("cashier", "cashier123"))).andExpect(status().isOk());
        mvc.perform(get("/reports/sales").with(httpBasic("cashier", "cashier123"))).andExpect(status().isForbidden());
    }
}
