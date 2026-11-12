package dev.javabybuilding.config;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.javabybuilding.ShopApplication;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Lesson 46: the same jar behaves differently in different PLACES because of configuration, not code. Here the "prod" profile is
 * switched on, and the values that a server would put in environment variables (SHOP_ADMIN_PASSWORD ...) are given as properties:
 * Spring resolves ${SHOP_ADMIN_PASSWORD} in application-prod.properties from either.
 */
class ProfilesTest {

    @Nested
    @SpringBootTest(properties = {
            "spring.profiles.active=prod",
            "spring.datasource.url=jdbc:h2:mem:prod-profile;DB_CLOSE_DELAY=-1",
            "SHOP_ADMIN_PASSWORD=prod-admin-secret", "SHOP_CASHIER_PASSWORD=prod-cashier-secret"})
    @AutoConfigureMockMvc
    class ProdProfile {

        @Autowired MockMvc mvc;

        @Test
        void thePasswordsComeFromTheEnvironmentNotFromTheDefaults() throws Exception {
            mvc.perform(get("/me").with(httpBasic("admin", "prod-admin-secret"))).andExpect(status().isOk());
            mvc.perform(get("/me").with(httpBasic("cashier", "prod-cashier-secret"))).andExpect(status().isOk());
            mvc.perform(get("/me").with(httpBasic("admin", "admin123"))).andExpect(status().isUnauthorized());
        }

        @Test
        void aRealShopStartsEmptyWithNoSampleProducts() throws Exception {
            mvc.perform(get("/products").with(httpBasic("admin", "prod-admin-secret")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        void theSwaggerPageIsOffButTheApiDescriptionIsStillServed() throws Exception {
            mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isNotFound());
            mvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
        }
    }

    @Nested
    @SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:dev-profile;DB_CLOSE_DELAY=-1")
    @AutoConfigureMockMvc
    class DefaultProfile {

        @Autowired MockMvc mvc;

        @Test
        void onYourPcTheLearningDefaultsWorkAndTheSampleProductsAreThere() throws Exception {
            mvc.perform(get("/products").with(httpBasic("admin", "admin123")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(4));
            mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
        }
    }

    /** Starts the whole application and returns the message the failure was reported with (the chain of causes, joined). */
    private static String startAndCollectTheFailure(String... properties) {
        // Command-line arguments (--name=value) beat application.properties; builder.properties(...) would be the WEAKEST source.
        String[] args = Stream.concat(Stream.of(properties), Stream.of("server.port=0", "spring.datasource.url=jdbc:h2:mem:fail-" + System.nanoTime()))
                .map(p -> "--" + p).toArray(String[]::new);
        Exception failure = assertThrows(Exception.class, () -> new SpringApplicationBuilder(ShopApplication.class).run(args).close());
        StringBuilder messages = new StringBuilder();
        for (Throwable t = failure; t != null; t = t.getCause()) {
            messages.append(t.getMessage()).append(" | ");
        }
        return messages.toString();
    }

    @Test
    void theProdProfileRefusesToStartWithoutItsPasswords() {
        assumeTrue(System.getenv("SHOP_ADMIN_PASSWORD") == null, "this machine has SHOP_ADMIN_PASSWORD set");
        String why = startAndCollectTheFailure("spring.profiles.active=prod");
        assertTrue(why.contains("SHOP_ADMIN_PASSWORD"), "the message must name the missing variable: " + why);
    }

    @Test
    void theProdProfileRefusesWeakPasswords() {
        String why = startAndCollectTheFailure("spring.profiles.active=prod", "SHOP_ADMIN_PASSWORD=admin123", "SHOP_CASHIER_PASSWORD=cashier123");
        assertTrue(why.contains("at least minPasswordLength characters"), why);
    }

    @Test
    void theExamplePasswordsFromDotEnvExampleAreRefused() {
        String why = startAndCollectTheFailure("spring.profiles.active=prod",
                "SHOP_ADMIN_PASSWORD=change-me-admin-password", "SHOP_CASHIER_PASSWORD=change-me-cashier-password");
        assertTrue(why.contains("must not start with 'change-me'"), why);
    }

    @Test
    void aTooShortPasswordIsRefusedEvenOnYourOwnPc() {
        String why = startAndCollectTheFailure("shop.security.admin-password=abc");
        assertTrue(why.contains("at least minPasswordLength characters"), why);
    }
}
