package dev.javabybuilding.security;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

/**
 * The settings that start with {@code shop.security.}, as ONE typed object instead of loose {@code @Value} strings, and CHECKED:
 * if a value is missing, too short or still the example text, the application refuses to start, with a message that says why.
 * Failing at start-up is a feature: far better than running with a password nobody chose.
 *
 * <p>{@code minPasswordLength} is 8 on your PC and 12 in the prod profile (application-prod.properties).
 */
@Validated
@ConfigurationProperties(prefix = "shop.security")
public record SecuritySettings(
        @NotBlank String adminPassword,
        @NotBlank String cashierPassword,
        @DefaultValue("8") @Min(8) int minPasswordLength) {

    @AssertTrue(message = "the passwords must be at least minPasswordLength characters long, must not start with 'change-me' and must not be an unresolved placeholder (in the prod profile set the variables SHOP_ADMIN_PASSWORD and SHOP_CASHIER_PASSWORD)")
    public boolean isAcceptable() {
        return acceptable(adminPassword) && acceptable(cashierPassword);
    }

    private boolean acceptable(String password) {
        if (password == null || password.isBlank()) {
            return true; // @NotBlank reports a missing password; do not report it twice
        }
        // A ${NAME} that no environment variable fills is NOT an error for Spring: it stays as text. Refuse it here.
        return password.length() >= minPasswordLength && !password.startsWith("change-me") && !password.contains("${");
    }
}
