package dev.javabybuilding.api;

/** The JSON {@code {"message":"Hello, Ana!"}}: a record is all Jackson needs. */
public record Greeting(String message) {}
