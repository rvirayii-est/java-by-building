package dev.javabybuilding.web;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Lesson 47: what the actuator shows, and to WHOM. Real filter chain, real users. (It has its own in-memory database, because @AutoConfigureObservability makes it a different application from the other
 * full tests: {@code @AutoConfigureObservability} switches the metrics on again, because Spring Boot turns them off in tests.
 */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:mvc-actuator;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
@AutoConfigureObservability
class ActuatorTest {

    @Autowired MockMvc mvc;

    // ---- health: public, but only the verdict ----

    @Test
    void aPlatformCanAskIfTheAppIsUpWithoutAPasswordAndGetsOnlyTheVerdict() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components").doesNotExist());
    }

    @Test
    void livenessAndReadinessAreSeparateQuestionsBothUp() throws Exception {
        mvc.perform(get("/actuator/health/liveness")).andExpect(status().isOk()).andExpect(content().json("{\"status\":\"UP\"}", true));
        mvc.perform(get("/actuator/health/readiness")).andExpect(status().isOk()).andExpect(content().json("{\"status\":\"UP\"}", true));
    }

    @Test
    void anAdminSeesTheParts() throws Exception {
        mvc.perform(get("/actuator/health").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components.db.status").value("UP"))
                .andExpect(jsonPath("$.components.diskSpace.status").value("UP"));
    }

    @Test
    void aCashierDoesNotSeeTheParts() throws Exception {
        mvc.perform(get("/actuator/health").with(httpBasic("cashier", "cashier123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components").doesNotExist());
    }

    @Test
    void theHealthAnswerNamesNeitherTheDatabaseNorThePathsToStrangers() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(content().string(not(containsString("H2")))).andExpect(content().string(not(containsString("path"))));
    }

    // ---- everything else: admin only ----

    @Test
    void infoIsForAdminsOnly() throws Exception {
        mvc.perform(get("/actuator/info")).andExpect(status().isUnauthorized());
        mvc.perform(get("/actuator/info").with(httpBasic("cashier", "cashier123"))).andExpect(status().isForbidden());
        mvc.perform(get("/actuator/info").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app.name").value("Inventory & Sales API"));
    }

    @Test
    void metricsAreForAdminsOnly() throws Exception {
        mvc.perform(get("/actuator/metrics")).andExpect(status().isUnauthorized());
        mvc.perform(get("/actuator/metrics").with(httpBasic("cashier", "cashier123"))).andExpect(status().isForbidden());
        mvc.perform(get("/actuator/metrics").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.names", hasItem("jvm.memory.used")))
                .andExpect(jsonPath("$.names", hasItem("http.server.requests")));
    }

    @Test
    void theRequestsOfTheTestsThemselvesAreCounted() throws Exception {
        mvc.perform(get("/products").with(httpBasic("admin", "admin123"))).andExpect(status().isOk());
        mvc.perform(get("/actuator/metrics/http.server.requests").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.measurements[?(@.statistic == 'COUNT')].value").isNotEmpty());
    }

    // ---- what is not exposed at all ----

    @Test
    void dangerousEndpointsAreNotExposedEvenToAnAdmin() throws Exception {
        for (String path : new String[] {"/actuator/env", "/actuator/beans", "/actuator/heapdump", "/actuator/threaddump", "/actuator/mappings", "/actuator/configprops"}) {
            mvc.perform(get(path).with(httpBasic("admin", "admin123"))).andExpect(status().isNotFound());
        }
        mvc.perform(post("/actuator/shutdown").with(httpBasic("admin", "admin123"))).andExpect(status().isNotFound());
    }

    @Test
    void theDiscoveryPageListsOnlyWhatIsExposed() throws Exception {
        mvc.perform(get("/actuator").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.health").exists())
                .andExpect(jsonPath("$._links.info").exists())
                .andExpect(jsonPath("$._links.metrics").exists())
                .andExpect(jsonPath("$._links.env").doesNotExist());
    }

    // ---- the request id ----

    @Test
    void everyAnswerCarriesARequestIdEvenARefusedOne() throws Exception {
        mvc.perform(get("/products")).andExpect(status().isUnauthorized()).andExpect(header().exists("X-Request-Id"));
        mvc.perform(get("/products").with(httpBasic("admin", "admin123"))).andExpect(header().exists("X-Request-Id"));
    }

    @Test
    void aPlainIdFromTheCallerIsKept() throws Exception {
        mvc.perform(get("/hello").header("X-Request-Id", "order-4711")).andExpect(header().string("X-Request-Id", "order-4711"));
    }
}
