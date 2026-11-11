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
        // TODO 1: split with line.split(" ") and check the rules above, then return new RequestLine(parts[0], parts[1], parts[2]).
        // method.matches("[A-Z]+") checks capital letters only.
        return null;
    }

    /**
     * The kind of answer a status number means: 100-199 "Informational", 200-299 "Success", 300-399 "Redirect",
     * 400-499 "Client error" (the caller did something wrong), 500-599 "Server error" (the server did).
     *
     * @throws IllegalArgumentException for a number below 100 or above 599
     */
    public static String statusClass(int status) {
        // TODO 2: integer division helps: status / 100 is 2 for every 2xx status.
        return null;
    }

    /**
     * Reads a header line such as "Content-Type:  text/plain " into its name in lower case and its value without
     * the spaces around it: ("content-type", "text/plain"). Only the FIRST colon splits (a value may contain more).
     *
     * @throws IllegalArgumentException when there is no colon or the name is empty
     */
    public static Map.Entry<String, String> parseHeader(String line) {
        // TODO 3: int colon = line.indexOf(':'); then substring, trim, toLowerCase. Return new AbstractMap.SimpleEntry<>(name, value).
        return new AbstractMap.SimpleEntry<>("", "");
    }

    /**
     * The text a client sends for a simple GET, ready to write to a socket. HTTP ends every line with \r\n (carriage
     * return + line feed) and ends the headers with an empty line:
     * "GET /health HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n"
     */
    public static String buildRequest(String method, String path, String host) {
        // TODO 4: put the request line, the Host header, the "Connection: close" header and the final empty line together.
        return "";
    }

    /** A "safe" method only reads and never changes anything on the server: GET, HEAD and OPTIONS are safe; POST, PUT, PATCH and DELETE are not. */
    public static boolean isSafe(String method) {
        // TODO 5: compare with the three safe methods (capitals matter: HTTP methods are case-sensitive).
        return false;
    }
}
