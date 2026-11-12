package dev.javabybuilding.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

/**
 * The security rules of the whole API, in one place. Spring Security is a chain of filters that every request passes through
 * BEFORE it reaches a controller; this class configures that chain.
 *
 * <p>Authentication = "who are you?" (this lesson). Authorization = "what may you do?" (lesson 42).
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
        http
                // CSRF protects browsers that keep you logged in with a cookie. This API keeps no cookie or session: every request
                // carries its own credentials, so there is nothing for a forged page to ride on. (Not true for a web site with a login form.)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // HTTP Basic: every request has the header  Authorization: Basic base64(username:password).
                .httpBasic(basic -> basic.authenticationEntryPoint(unauthorized))        // used when the password is WRONG
                .exceptionHandling(errors -> errors.authenticationEntryPoint(unauthorized)) // used when NO credentials were sent
                .authorizeHttpRequests(rules -> rules
                        .requestMatchers("/hello", "/error").permitAll() // the greeting stays public
                        .anyRequest().permitAll());                       // TODO 3: everything else needs a known user: change permitAll() to authenticated()
        return http.build();
    }
}
