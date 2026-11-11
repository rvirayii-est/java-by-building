package dev.javabybuilding.web;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Small helpers for reading the pieces of an HTTP request. A framework (Spring, from lesson 36) does all of this for
 * you; here you write it once by hand so you know what the framework is doing.
 */
public final class Http {

    private Http() {}

    /** The standard words that go with a status number: reason(404) is "Not Found". Unknown numbers give "Unknown". */
    public static String reason(int status) {
        // TODO 1: return the words for 200 OK, 201 Created, 204 No Content, 400 Bad Request, 404 Not Found,
        // 405 Method Not Allowed, 409 Conflict and 500 Internal Server Error; "Unknown" for any other number.
        // A switch expression is the tidy way:  return switch (status) { case 200 -> "OK"; ... default -> "Unknown"; };
        return null;
    }

    /**
     * Cuts a path into its parts and decodes each part: segments("/products/Rice%2025kg") is ["products", "Rice 25kg"].
     * Empty parts are skipped, so "/" and "" give an empty list. A "+" in a path is a real plus sign (only in a
     * query is it a space).
     */
    public static List<String> segments(String path) {
        // TODO 2: path.split("/") gives the parts (the first one is empty for a path that starts with "/": skip empty ones).
        // Decode each with URLDecoder.decode(part, StandardCharsets.UTF_8). URLDecoder turns "+" into a space, which is
        // wrong in a path, so first replace("+", "%2B") on the part (%2B decodes to a real plus sign).
        return new ArrayList<>();
    }

    /**
     * Reads "a=1&b=x%20y" into a map, in the order written: {a=1, b=x y}. Works for the query part of a URL and for a
     * form body alike. A null or empty text gives an empty map; a key without "=" gets the value "". In a query a
     * "+" means a space.
     */
    public static Map<String, String> query(String rawQuery) {
        // TODO 3: LinkedHashMap keeps the order. Split on "&"; for each pair find the FIRST "=" (indexOf) and cut the
        // key and the value around it (the value may contain more "=" signs). Decode both with URLDecoder.decode(..., UTF_8).
        return new LinkedHashMap<>();
    }
}
