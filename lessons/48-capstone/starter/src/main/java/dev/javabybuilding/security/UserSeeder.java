package dev.javabybuilding.security;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Runs once after start-up: an EMPTY app_user table gets two demo users, "admin" and "cashier". Their passwords come from
 * {@link SecuritySettings} (so a real installation sets its own through the environment, without touching code) and are HASHED
 * before they are stored. The default passwords in application.properties are for learning only; the prod profile has none.
 */
@Component
@EnableConfigurationProperties(SecuritySettings.class) // registers and validates the settings object (only where this component is loaded)
public class UserSeeder implements ApplicationRunner {

    private final AppUserRepository users;
    private final PasswordEncoder encoder;
    private final SecuritySettings settings;

    public UserSeeder(AppUserRepository users, PasswordEncoder encoder, SecuritySettings settings) {
        this.users = users;
        this.encoder = encoder;
        this.settings = settings;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (users.count() == 0) {
            users.save(new AppUserEntity("admin", encoder.encode(settings.adminPassword()), AppRole.ADMIN));
            users.save(new AppUserEntity("cashier", encoder.encode(settings.cashierPassword()), AppRole.CASHIER));
        }
    }
}
