package dev.javabybuilding.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * The settings that start with {@code shop.security.}, as ONE typed object instead of loose {@code @Value} strings.
 *
 * <p>TODO 1: make Spring CHECK them, so the application refuses to start with a missing, short or example password:
 * <ul>
 *   <li>add {@code @Validated} to the record (import org.springframework.validation.annotation.Validated);</li>
 *   <li>put {@code @NotBlank} on both passwords and {@code @Min(8)} on minPasswordLength (jakarta.validation.constraints);</li>
 *   <li>add a method {@code @AssertTrue(message = "the passwords must be at least minPasswordLength characters long, must not start with
 *       'change-me' and must not be an unresolved placeholder (in the prod profile set the variables SHOP_ADMIN_PASSWORD and
 *       SHOP_CASHIER_PASSWORD)") public boolean isAcceptable()} that is true when each non-blank password is long enough, does not
 *       start with "change-me" and does not contain "${" (Spring leaves an unfilled ${NAME} as plain text instead of failing!).</li>
 * </ul>
 * SecuritySettingsTest and ProfilesTest tell you when it is right.
 */
@ConfigurationProperties(prefix = "shop.security")
public record SecuritySettings(String adminPassword, String cashierPassword, @DefaultValue("8") int minPasswordLength) {

    public boolean isAcceptable() {
        return true; // TODO 1: replace with the real check
    }
}
