package dev.javabybuilding.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Runs once after start-up: an EMPTY app_user table gets two demo users, "admin" and "cashier". Their passwords come from
 * settings (so a real installation can change them without touching code) and are HASHED before they are stored.
 * The default passwords in application.properties are for learning only.
 */
@Component
public class UserSeeder implements ApplicationRunner {

    private final AppUserRepository users;
    private final PasswordEncoder encoder;
    private final String adminPassword;
    private final String cashierPassword;

    public UserSeeder(AppUserRepository users, PasswordEncoder encoder,
                      @Value("${shop.security.admin-password}") String adminPassword,
                      @Value("${shop.security.cashier-password}") String cashierPassword) {
        this.users = users;
        this.encoder = encoder;
        this.adminPassword = adminPassword;
        this.cashierPassword = cashierPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (users.count() == 0) {
            users.save(new AppUserEntity("admin", encoder.encode(adminPassword), AppRole.ADMIN));
            users.save(new AppUserEntity("cashier", encoder.encode(cashierPassword), AppRole.CASHIER));
        }
    }
}
