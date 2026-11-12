package dev.javabybuilding.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** A controller is an ordinary Java class: you can test its logic by calling the method, with no Spring and no server. */
class HelloControllerTest {

    private final HelloController controller = new HelloController();

    @Test
    void greetsByName() {
        assertEquals(new Greeting("Hello, Ana!"), controller.hello("Ana"));
    }

    @Test
    void trimsTheName() {
        assertEquals("Hello, Ben!", controller.hello("  Ben ").message());
    }

    @Test
    void aBlankNameGreetsTheWorld() {
        assertEquals("Hello, world!", controller.hello("   ").message());
    }
}
