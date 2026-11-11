package dev.javabybuilding.api;

import java.util.Map;

/**
 * A request, already cut into parts. {@code path} is the raw path as sent (still URL-encoded), {@code query} holds the
 * decoded ?name=value pairs, and {@code body} is the data sent with a POST or PUT, already read into a Map (a JSON object
 * becomes a Map in lesson 35). It is null when there was no body.
 */
public record ApiRequest(String method, String path, Map<String, String> query, Map<String, Object> body) {

    public static ApiRequest get(String path) {
        return new ApiRequest("GET", path, Map.of(), null);
    }

    public static ApiRequest of(String method, String path, Map<String, Object> body) {
        return new ApiRequest(method, path, Map.of(), body);
    }
}
