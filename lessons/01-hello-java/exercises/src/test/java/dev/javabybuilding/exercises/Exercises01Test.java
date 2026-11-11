package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Exercises01Test {

    @Test
    void greetUsesTheName() {
        assertEquals("Hello, Ana!", Exercises01.greet("Ana"));
    }

    @Test
    void receiptHeaderIsUpperCaseAndFramed() {
        assertEquals("*** MANG TOMAS STORE ***", Exercises01.receiptHeader("Mang Tomas Store"));
    }
}
