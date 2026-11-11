package dev.javabybuilding.exercises;

/**
 * Lesson 34 drills: the small decisions of REST design. Complete each TODO until Exercises34Test is green.
 */
public class RestDrills {

    /**
     * Is this a well-designed REST path? The rules:
     * 1. it starts with "/" and does not end with "/" (except the path "/" is not allowed either: there must be a resource);
     * 2. the parts at position 0, 2, 4 ... (counting from 0, after the first "/") are RESOURCE NAMES: lower-case letters
     *    only, words joined with "-" if needed, and they must not start with a verb (get, create, delete, update, add, remove, set);
     * 3. the parts at position 1, 3, 5 ... are IDs and may be anything that is not empty.
     * Good: "/products", "/products/42", "/products/42/stock-movements". Bad: "/getProducts", "/Products", "/products/", "products".
     */
    public static boolean isGoodResourcePath(String path) {
        // TODO 1: check startsWith("/") and !endsWith("/"), then path.substring(1).split("/", -1) and look at every part
        // with its index (a plain for loop). part.matches("[a-z]+(-[a-z]+)*") checks lower-case words joined with "-".
        return false;
    }

    /**
     * The status code a SUCCESSFUL call of this method should answer: GET 200, POST 201 (it created something), PUT 200,
     * PATCH 200, DELETE 204 (it worked and there is nothing to send back).
     *
     * @throws IllegalArgumentException for any other method
     */
    public static int successStatus(String method) {
        // TODO 2: a switch on the method name.
        return 0;
    }

    /**
     * The status for a problem: IllegalArgumentException (bad input) is 400, NoSuchElementException (nothing found) is 404,
     * IllegalStateException (clashes with the current state) is 409, anything else is 500 (a bug on our side).
     */
    public static int statusFor(Exception problem) {
        // TODO 3: if / else if with instanceof.
        return 0;
    }

    /**
     * Is the method idempotent: doing it twice leaves the server in the same state as doing it once?
     * GET, HEAD, OPTIONS, PUT and DELETE are; POST and PATCH are not (two POSTs of a sale make two sales).
     */
    public static boolean isIdempotent(String method) {
        // TODO 4: true for the five idempotent methods.
        return false;
    }

    /**
     * The path of one item of a collection, ready for a Location header: locationFor("products", "Rice 25kg") is
     * "/products/Rice%2025kg". Use java.net.URLEncoder.encode(id, UTF_8); it writes a space as "+", so replace("+", "%20").
     */
    public static String locationFor(String collection, String id) {
        // TODO 5
        return null;
    }
}
