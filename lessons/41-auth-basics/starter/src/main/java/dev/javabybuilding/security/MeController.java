package dev.javabybuilding.security;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** "Who am I?" Spring Security hands the controller the Authentication of the request: proof that the filter chain accepted it. */
@RestController
public class MeController {

    /** The answer of GET /me. */
    public record Me(String username, List<String> authorities) {}

    @GetMapping("/me")
    public Me me(Authentication authentication) {
        return new Me(authentication.getName(), authentication.getAuthorities().stream().map(a -> a.getAuthority()).sorted().toList());
    }
}
