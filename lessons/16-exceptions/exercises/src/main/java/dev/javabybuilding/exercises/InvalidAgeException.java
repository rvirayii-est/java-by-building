package dev.javabybuilding.exercises;

/** Lesson 16, exercise 1: thrown when a typed age cannot be used. (Done for you.) */
public class InvalidAgeException extends RuntimeException {

    public InvalidAgeException(String message) {
        super(message);
    }

    public InvalidAgeException(String message, Throwable cause) {
        super(message, cause); // keep the original problem (the cause) so nothing is lost
    }
}
