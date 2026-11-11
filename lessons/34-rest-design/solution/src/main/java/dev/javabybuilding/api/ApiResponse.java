package dev.javabybuilding.api;

import java.util.Map;

/** What the API answers: a status, a body (a DTO record, a list, or null for "no content") and extra headers. */
public record ApiResponse(int status, Object body, Map<String, String> headers) {

    public static ApiResponse ok(Object body) {
        return new ApiResponse(200, body, Map.of());
    }

    /** 201 Created. The Location header says where the new thing can be read. */
    public static ApiResponse created(Object body, String location) {
        return new ApiResponse(201, body, Map.of("Location", location));
    }

    /** 204 No Content: it worked and there is nothing to send back. */
    public static ApiResponse noContent() {
        return new ApiResponse(204, null, Map.of());
    }
}
