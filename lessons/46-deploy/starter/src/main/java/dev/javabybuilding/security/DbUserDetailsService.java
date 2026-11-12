package dev.javabybuilding.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security asks ONE question when someone sends a username and password: "who is this user?" This class answers it from
 * the database. It hands back the stored HASH; Spring Security hashes the password that was typed and compares the two.
 * We never see, compare or log a plain password.
 */
@Service
public class DbUserDetailsService implements UserDetailsService {

    private final AppUserRepository users;

    public DbUserDetailsService(AppUserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        AppUserEntity user = users.findById(username).orElseThrow(() -> new UsernameNotFoundException("Unknown user"));
        return User.withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .disabled(!user.isEnabled())
                .roles(user.getRole().name()) // becomes the authority ROLE_ADMIN or ROLE_CASHIER
                .build();
    }
}
