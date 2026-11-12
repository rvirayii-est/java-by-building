package dev.javabybuilding.security;

import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data writes the implementation: findById(username), save(user), count() ... */
public interface AppUserRepository extends JpaRepository<AppUserEntity, String> {}
