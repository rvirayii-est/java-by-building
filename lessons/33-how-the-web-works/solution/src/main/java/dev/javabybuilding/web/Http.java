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
        return switch (status) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 204 -> "No Content";
            case 400 -> "Bad Request";
            case 404 -> "Not Found";
            case 405 -> "Method Not Allowed";
            case 409 -> "Conflict";
            case 500 -> "Internal Server Error";
            default -> "Unknown";
        };
    }

    /**
     * Cuts a path into its parts and decodes each part: segments("/products/Rice%2025kg") is ["products", "Rice 25kg"].
     * Empty parts are skipped, so "/" and "" give an empty list. A "+" in a path is a real plus sign (only in a
     * query is it a space).
     */
    public static List<String> segments(String path) {
        List<String> parts = new ArrayList<>();
        for (String part : path.split("/")) {
            if (!part.isEmpty()) {
                parts.add(URLDecoder.decode(part.replace("+", "%2B"), StandardCharsets.UTF_8));
            }
        }
        return parts;
    }

    /**
     * Reads "a=1&b=x%20y" into a map, in the order written: {a=1, b=x y}. Works for the query part of a URL and for a
     * form body alike. A null or empty text gives an empty map; a key without "=" gets the value "". In a query a
     * "+" means a space.
     */
    public static Map<String, String> query(String rawQuery) {
        Map<String, String> values = new LinkedHashMap<>();
        if (rawQuery == null || rawQuery.isEmpty()) {
            return values;
        }
        for (String pair : rawQuery.split("&")) {
            int equals = pair.indexOf('=');
            String key = equals < 0 ? pair : pair.substring(0, equals);
            String value = equals < 0 ? "" : pair.substring(equals + 1);
            values.put(URLDecoder.decode(key, StandardCharsets.UTF_8), URLDecoder.decode(value, StandardCharsets.UTF_8));
        }
        return values;
    }
}
