package dev.javabybuilding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The front door of the app. {@code @SpringBootApplication} is three annotations in one:
 * <ul>
 *   <li>{@code @SpringBootConfiguration}: this class can declare beans (objects Spring creates and manages);</li>
 *   <li>{@code @ComponentScan}: look for classes marked {@code @RestController}, {@code @Service}, {@code @Component}... in this
 *       package and every package below it (dev.javabybuilding.api, dev.javabybuilding.config ...) and create them;</li>
 *   <li>{@code @EnableAutoConfiguration}: look at the libraries on the class path and set up what they need, for example a
 *       web server because spring-boot-starter-web is there, and a JSON converter because Jackson is there.</li>
 * </ul>
 * That is why this class must sit in the TOP package: everything the scan should find is below it.
 */
@SpringBootApplication
public class ShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args); // builds the container, starts the web server, then keeps running
    }
}
