package dev.javabybuilding.exercises;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Lesson 33 drills: the small pieces of text that every HTTP message is made of. Complete each TODO until
 * Exercises33Test is green.
 */
public class HttpDrills {

    /** The first line of a request: "GET /products?status=LOW HTTP/1.1" is method GET, target /products?status=LOW, version HTTP/1.1. */
    public record RequestLine(String method, String target, String version) {}

    /**
     * Reads a request line. It must have exactly three parts separated by single spaces, the method must be capital
     * letters only (GET, POST, ...) and the version must start with "HTTP/".
     *
     * @throws IllegalArgumentException for anything else
     */
    public static RequestLine parseRequestLine(String line) {
        String[] parts = line.split(" ", -1);
        if (parts.length != 3 || !parts[0].matches("[A-Z]+") || parts[1].isEmpty() || !parts[2].startsWith("HTTP/")) {
            throw new IllegalArgumentException("Not a request line: " + line);
        }
        return new RequestLine(parts[0], parts[1], parts[2]);
    }

    /**
     * The kind of answer a status number means: 100-199 "Informational", 200-299 "Success", 300-399 "Redirect",
     * 400-499 "Client error" (the caller did something wrong), 500-599 "Server error" (the server did).
     *
     * @throws IllegalArgumentException for a number below 100 or above 599
     */
    public static String statusClass(int status) {
        return switch (status / 100) {
            case 1 -> "Informational";
            case 2 -> "Success";
            case 3 -> "Redirect";
            case 4 -> "Client error";
            case 5 -> "Server error";
            default -> throw new IllegalArgumentException("Not an HTTP status: " + status);
        };
    }

    /**
     * Reads a header line such as "Content-Type:  text/plain " into its name in lower case and its value without
     * the spaces around it: ("content-type", "text/plain"). Only the FIRST colon splits (a value may contain more).
     *
     * @throws IllegalArgumentException when there is no colon or the name is empty
     */
    public static Map.Entry<String, String> parseHeader(String line) {
        int colon = line.indexOf(':');
        if (colon <= 0) {
            throw new IllegalArgumentException("Not a header line: " + line);
        }
        return new AbstractMap.SimpleEntry<>(line.substring(0, colon).trim().toLowerCase(), line.substring(colon + 1).trim());
    }

    /**
     * The text a client sends for a simple GET, ready to write to a socket. HTTP ends every line with \r\n (carriage
     * return + line feed) and ends the headers with an empty line:
     * "GET /health HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n"
     */
    public static String buildRequest(String method, String path, String host) {
        return method + " " + path + " HTTP/1.1\r\nHost: " + host + "\r\nConnection: close\r\n\r\n";
    }

    /** A "safe" method only reads and never changes anything on the server: GET, HEAD and OPTIONS are safe; POST, PUT, PATCH and DELETE are not. */
    public static boolean isSafe(String method) {
        return method.equals("GET") || method.equals("HEAD") || method.equals("OPTIONS");
    }
}
