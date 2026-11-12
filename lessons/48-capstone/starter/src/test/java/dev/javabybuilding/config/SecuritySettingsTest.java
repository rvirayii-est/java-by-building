package dev.javabybuilding.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.javabybuilding.security.SecuritySettings;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Plain unit tests of the rules for the passwords (no Spring): the same Validator lesson 39 used for request bodies. */
class SecuritySettingsTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private List<String> problems(SecuritySettings settings) {
        Set<ConstraintViolation<SecuritySettings>> violations = validator.validate(settings);
        return violations.stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).sorted().toList();
    }

    @Test
    void goodPasswordsPass() {
        assertEquals(List.of(), problems(new SecuritySettings("admin123", "cashier123", 8)));
    }

    @Test
    void aBlankOrMissingPasswordIsRefused() {
        assertEquals(List.of("adminPassword: must not be blank", "cashierPassword: must not be blank"), problems(new SecuritySettings(" ", null, 8)));
    }

    @Test
    void aShortPasswordIsRefused() {
        List<String> problems = problems(new SecuritySettings("short", "cashier123", 8));
        assertEquals(1, problems.size());
        assertTrue(problems.get(0).startsWith("acceptable: the passwords must be at least minPasswordLength characters"), problems.get(0));
    }

    @Test
    void theProductionLimitIs12() {
        assertEquals(1, problems(new SecuritySettings("admin123", "cashier123", 12)).size(), "8 characters pass on your PC but not in prod");
        assertEquals(List.of(), problems(new SecuritySettings("a-long-admin-1", "a-long-cashier-1", 12)));
    }

    @Test
    void theExampleTextFromDotEnvExampleIsRefusedEvenWhenLongEnough() {
        assertEquals(1, problems(new SecuritySettings("change-me-admin-password", "a-long-cashier-1", 12)).size());
    }

    @Test
    void theMinimumItselfCannotBeLoweredBelow8() {
        assertEquals(List.of("minPasswordLength: must be greater than or equal to 8"), problems(new SecuritySettings("admin123", "cashier123", 4)));
    }
}
