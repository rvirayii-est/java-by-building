package dev.javabybuilding.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * The security rules of the whole API, in one place. Spring Security is a chain of filters that every request passes through
 * BEFORE it reaches a controller; this class configures that chain.
 *
 * <p>Authentication = "who are you?" (lesson 41). Authorization = "what may you do?" (this lesson): the table of rules below.
 */
@Configuration
public class SecurityConfig {

    /** BCrypt: a slow, salted, one-way hash made for passwords. We use the library's; nobody should write their own. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper json) throws Exception {
        AuthenticationEntryPoint unauthorized = (request, response, e) -> { // "I do not know who you are": 401 in our JSON shape
            response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"shop\"");
            SecurityErrors.write(request, response, json, HttpStatus.UNAUTHORIZED, "Authentication is required. Send a valid username and password.");
        };
        AccessDeniedHandler forbidden = (request, response, e) -> // "I know who you are, and the answer is no": 403
                SecurityErrors.write(request, response, json, HttpStatus.FORBIDDEN, "Your role is not allowed to do this.");

        http
                // CSRF protects browsers that keep you logged in with a cookie. This API keeps no cookie or session: every request
                // carries its own credentials, so there is nothing for a forged page to ride on. (Not true for a web site with a login form.)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(basic -> basic.authenticationEntryPoint(unauthorized))        // used when the password is WRONG
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint(unauthorized)                           // used when NO credentials were sent
                        .accessDeniedHandler(forbidden))                                  // used when the role is not enough
                // THE RULES. The first rule that matches a request decides, so the specific ones come first and the catch-all last.
                .authorizeHttpRequests(rules -> rules
                        .requestMatchers("/hello", "/error").permitAll()
                        // The API description and the try-it page describe the API; they hold no shop data. (A real deployment may close them: lesson 46.)
                        .requestMatchers("/v3/api-docs", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                        .requestMatchers("/me").authenticated()
                        // Everyone in the shop may LOOK at products and sales, and record a sale...
                        .requestMatchers(HttpMethod.GET, "/products", "/products/*", "/sales", "/sales/*").hasAnyRole("ADMIN", "CASHIER")
                        .requestMatchers(HttpMethod.POST, "/sales").hasAnyRole("ADMIN", "CASHIER")
                        // ...but changing the catalogue (create, change, delete, receive a delivery) and the reports are for the admin.
                        .requestMatchers("/products", "/products/**", "/reports/**").hasRole("ADMIN")
                        // Anything not listed above is for the admin only: a NEW endpoint is closed until someone decides who may use it.
                        .anyRequest().hasRole("ADMIN"));
        return http.build();
    }

}
