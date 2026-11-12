package dev.javabybuilding.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** Plain unit tests: the user store is faked, so no Spring and no database. */
class DbUserDetailsServiceTest {

    private final AppUserRepository users = mock(AppUserRepository.class);
    private final DbUserDetailsService service = new DbUserDetailsService(users);

    @Test
    void anExistingUserIsReturnedWithItsHashAndNotItsPlainPassword() {
        when(users.findById("ana")).thenReturn(Optional.of(new AppUserEntity("ana", "$2a$10$hash", AppRole.CASHIER)));
        UserDetails details = service.loadUserByUsername("ana");
        assertEquals("ana", details.getUsername());
        assertEquals("$2a$10$hash", details.getPassword());
        assertEquals(true, details.isEnabled());
        assertEquals(java.util.List.of("ROLE_CASHIER"), details.getAuthorities().stream().map(a -> a.getAuthority()).toList());
    }

    @Test
    void anUnknownUserThrowsTheExceptionSpringSecurityExpects() {
        when(users.findById("nobody")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("nobody"));
    }

    @Test
    void theSeederHashesBeforeItStores() {
        AppUserRepository repo = mock(AppUserRepository.class);
        when(repo.count()).thenReturn(0L);
        new UserSeeder(repo, new BCryptPasswordEncoder(), new SecuritySettings("secret-admin", "secret-cashier", 8)).run(null);
        ArgumentCaptor<AppUserEntity> saved = ArgumentCaptor.forClass(AppUserEntity.class);
        verify(repo, times(2)).save(saved.capture());
        for (AppUserEntity user : saved.getAllValues()) {
            assertFalse(user.getPasswordHash().contains("secret"), "a plain password was stored!");
        }
    }
}
