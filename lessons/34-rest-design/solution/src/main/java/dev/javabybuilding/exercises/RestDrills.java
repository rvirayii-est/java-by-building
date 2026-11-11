package dev.javabybuilding.exercises;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Lesson 34 drills: the small decisions of REST design. Complete each TODO until Exercises34Test is green.
 */
public class RestDrills {

    private static final List<String> VERBS = List.of("get", "create", "delete", "update", "add", "remove", "set");

    /**
     * Is this a well-designed REST path? The rules:
     * 1. it starts with "/" and does not end with "/" (except the path "/" is not allowed either: there must be a resource);
     * 2. the parts at position 0, 2, 4 ... (counting from 0, after the first "/") are RESOURCE NAMES: lower-case letters
     *    only, words joined with "-" if needed, and they must not start with a verb (get, create, delete, update, add, remove, set);
     * 3. the parts at position 1, 3, 5 ... are IDs and may be anything that is not empty.
     * Good: "/products", "/products/42", "/products/42/stock-movements". Bad: "/getProducts", "/Products", "/products/", "products".
     */
    public static boolean isGoodResourcePath(String path) {
        if (!path.startsWith("/") || path.endsWith("/")) {
            return false;
        }
        String[] parts = path.substring(1).split("/", -1);
        for (int i = 0; i < parts.length; i++) {
            if (i % 2 == 1) {
                if (parts[i].isEmpty()) {
                    return false; // an id must not be empty
                }
            } else if (!parts[i].matches("[a-z]+(-[a-z]+)*") || VERBS.stream().anyMatch(parts[i]::startsWith)) {
                return false; // a resource name: lower-case words, no verb
            }
        }
        return true;
    }

    /**
     * The status code a SUCCESSFUL call of this method should answer: GET 200, POST 201 (it created something), PUT 200,
     * PATCH 200, DELETE 204 (it worked and there is nothing to send back).
     *
     * @throws IllegalArgumentException for any other method
     */
    public static int successStatus(String method) {
        return switch (method) {
            case "GET", "PUT", "PATCH" -> 200;
            case "POST" -> 201;
            case "DELETE" -> 204;
            default -> throw new IllegalArgumentException("Unknown method: " + method);
        };
    }

    /**
     * The status for a problem: IllegalArgumentException (bad input) is 400, NoSuchElementException (nothing found) is 404,
     * IllegalStateException (clashes with the current state) is 409, anything else is 500 (a bug on our side).
     */
    public static int statusFor(Exception problem) {
        if (problem instanceof IllegalArgumentException) {
            return 400;
        } else if (problem instanceof NoSuchElementException) {
            return 404;
        } else if (problem instanceof IllegalStateException) {
            return 409;
        }
        return 500;
    }

    /**
     * Is the method idempotent: doing it twice leaves the server in the same state as doing it once?
     * GET, HEAD, OPTIONS, PUT and DELETE are; POST and PATCH are not (two POSTs of a sale make two sales).
     */
    public static boolean isIdempotent(String method) {
        return List.of("GET", "HEAD", "OPTIONS", "PUT", "DELETE").contains(method);
    }

    /**
     * The path of one item of a collection, ready for a Location header: locationFor("products", "Rice 25kg") is
     * "/products/Rice%2025kg". Use java.net.URLEncoder.encode(id, UTF_8); it writes a space as "+", so replace("+", "%20").
     */
    public static String locationFor(String collection, String id) {
        return "/" + collection + "/" + URLEncoder.encode(id, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
