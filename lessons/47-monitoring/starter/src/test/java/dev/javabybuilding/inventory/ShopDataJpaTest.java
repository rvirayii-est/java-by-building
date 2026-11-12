package dev.javabybuilding.inventory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * Our own test annotation: "a @DataJpaTest on a memory database, with our usual HikariCP pool". An annotation can carry other
 * annotations, so the settings are written once and each test class just says {@code @ShopDataJpaTest}.
 *
 * <p>Why not the default? A @DataJpaTest normally swaps the DataSource for its own embedded one, which opens a NEW connection
 * every time and closes the one Flyway used. With H2 2.4 the CHECK ... IN (...) rules made by that closed connection then fail
 * ("Check constraint invalid ... The database has been closed"). Keeping our pooled DataSource (the one the real app uses) avoids
 * it, and makes the slice behave like the app.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DataJpaTest(properties = "spring.datasource.url=jdbc:h2:mem:slice-tests;DB_CLOSE_DELAY=-1")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public @interface ShopDataJpaTest {}
