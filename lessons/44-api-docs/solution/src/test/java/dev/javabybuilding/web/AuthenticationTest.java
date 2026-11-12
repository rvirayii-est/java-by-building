package dev.javabybuilding.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.javabybuilding.security.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Lesson 41: authentication, with the real filter chain, the real database and the real BCrypt. No @WithMockUser here:
 * every request carries the real credentials (httpBasic builds the Authorization header).
 */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:mvc-it;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class AuthenticationTest {

    @Autowired MockMvc mvc;
    @Autowired AppUserRepository users;
    @Autowired PasswordEncoder encoder;

    @Test
    void noCredentialsIs401WithTheSameJsonShapeAsEveryOtherError() throws Exception {
        mvc.perform(get("/products"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", "Basic realm=\"shop\""))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.path").value("/products"));
    }

    @Test
    void aWrongPasswordIs401() throws Exception {
        mvc.perform(get("/products").with(httpBasic("admin", "wrong")))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", "Basic realm=\"shop\""))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void anUnknownUserGetsExactlyTheSameAnswerAsAWrongPassword() throws Exception {
        String wrongPassword = mvc.perform(get("/products").with(httpBasic("admin", "wrong"))).andReturn().getResponse().getContentAsString();
        String unknownUser = mvc.perform(get("/products").with(httpBasic("nobody", "wrong"))).andReturn().getResponse().getContentAsString();
        assertTrue(wrongPassword.contains("Unauthorized"), wrongPassword);
        assertEquals(wrongPassword, unknownUser, "the answer must not reveal which usernames exist");
    }

    @Test
    void theRightPasswordOpensTheDoor() throws Exception {
        mvc.perform(get("/products").with(httpBasic("admin", "admin123"))).andExpect(status().isOk());
        mvc.perform(get("/products").with(httpBasic("cashier", "cashier123"))).andExpect(status().isOk());
    }

    @Test
    void aBrokenAuthorizationHeaderIs401NotAnError() throws Exception {
        mvc.perform(get("/products").header("Authorization", "Basic !!!not-base64!!!")).andExpect(status().isUnauthorized());
        mvc.perform(get("/products").header("Authorization", "Bearer abc")).andExpect(status().isUnauthorized());
    }

    @Test
    void helloStaysPublic() throws Exception {
        mvc.perform(get("/hello")).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Hello, world!"));
    }

    @Test
    void anUnknownPathIs401ForStrangersSoTheyCannotMapTheApi() throws Exception {
        mvc.perform(get("/nothing")).andExpect(status().isUnauthorized());
        mvc.perform(get("/nothing").with(httpBasic("admin", "admin123"))).andExpect(status().isNotFound());
    }

    @Test
    void meSaysWhoIsCalling() throws Exception {
        mvc.perform(get("/me").with(httpBasic("cashier", "cashier123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("cashier"));
    }

    @Test
    void thePasswordIsStoredAsABcryptHashNeverAsText() {
        String stored = users.findById("admin").orElseThrow().getPasswordHash();
        assertNotEquals("admin123", stored);
        assertTrue(stored.startsWith("$2a$"), "a BCrypt hash starts with $2a$ and the cost: " + stored);
        assertEquals(60, stored.length());
        assertTrue(encoder.matches("admin123", stored));
    }

    @Test
    void theSamePasswordHashesDifferentlyEveryTimeBecauseOfTheSalt() {
        String first = encoder.encode("admin123");
        String second = encoder.encode("admin123");
        assertNotEquals(first, second);
        assertTrue(encoder.matches("admin123", first) && encoder.matches("admin123", second));
    }
}
