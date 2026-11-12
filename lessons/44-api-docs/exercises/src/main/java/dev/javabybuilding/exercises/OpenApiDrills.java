package dev.javabybuilding.exercises;

import java.util.List;

/**
 * Lesson 44 drills: the small chores a documentation generator does, written by hand so the OpenAPI file is not magic.
 * Complete each TODO until Exercises44Test is green.
 */
public class OpenApiDrills {

    /**
     * A name for an operation made from its method and path: the method in lower case, then each path part with a capital letter;
     * a variable part {name} becomes "By" + "Name". operationId("GET", "/products") is "getProducts";
     * operationId("GET", "/products/{name}") is "getProductsByName"; operationId("POST", "/products/{name}/stock") is "postProductsByNameStock".
     */
    public static String operationId(String method, String path) {
        // TODO 1: start a StringBuilder with method.toLowerCase(); split the path on "/", skip empty parts;
        // a part like {name} (starts with { and ends with }) adds "By" + the capitalised inside; any other part adds itself capitalised.
        return null;
    }

    /**
     * A path as a JSON pointer key (that is how a path is written in a pointer such as /paths/~1products~1{name}/get):
     * "~" becomes "~0" and "/" becomes "~1", in that order. escapeJsonPointer("/products/{name}") is "~1products~1{name}".
     */
    public static String escapeJsonPointer(String path) {
        // TODO 2: path.replace("~", "~0").replace("/", "~1")   (the ORDER matters: why?)
        return null;
    }

    /**
     * Which mapped endpoints are missing from the documentation? Both lists hold texts like "GET /products". Returns the missing ones
     * sorted, without duplicates. This is what lesson 44's OpenApiTest checks against the design table.
     */
    public static List<String> undocumented(List<String> mapped, List<String> documented) {
        // TODO 3: a TreeSet of mapped (sorted, no duplicates), removeAll(documented), then List.copyOf(...).
        return null;
    }

    /** The standard words of a status code as the documentation shows them: 200 OK, 201 Created, 204 No Content, 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found, 405 Method Not Allowed, 409 Conflict, 415 Unsupported Media Type, 500 Internal Server Error; any other number gives "Unknown". */
    public static String statusPhrase(int status) {
        // TODO 4: a switch expression on status with a default.
        return null;
    }
}
