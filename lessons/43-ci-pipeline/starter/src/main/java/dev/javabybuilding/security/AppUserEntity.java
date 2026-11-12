package dev.javabybuilding.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** One row of the app_user table (migration V4). The password is stored only as a BCrypt hash. */
@Entity
@Table(name = "app_user")
public class AppUserEntity {

    @Id
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private String role; // the name of an AppRole; kept as text so the table stays plain SQL

    protected AppUserEntity() {} // JPA needs an empty constructor

    public AppUserEntity(String username, String passwordHash, AppRole role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role.name();
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    /** Throws IllegalArgumentException if the database holds a word that is not an AppRole: better to stop than to guess. */
    public AppRole getRole() {
        return AppRole.valueOf(role);
    }

    public boolean isEnabled() {
        return enabled;
    }
}
