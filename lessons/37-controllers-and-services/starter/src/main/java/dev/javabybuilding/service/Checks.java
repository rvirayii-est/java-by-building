package dev.javabybuilding.service;

import dev.javabybuilding.inventory.InvalidInputException;

/** Small checks for request fields. Lesson 39 replaces them with annotations such as {@code @NotBlank}. */
final class Checks {

    private Checks() {}

    /** The text, or a 400 (InvalidInputException) when it is missing or blank. */
    static String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidInputException("Missing or empty field: " + field);
        }
        return value;
    }
}
