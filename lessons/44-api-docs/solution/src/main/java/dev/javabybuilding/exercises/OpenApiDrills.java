package dev.javabybuilding.exercises;

import java.util.List;
import java.util.TreeSet;

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
        StringBuilder id = new StringBuilder(method.toLowerCase());
        for (String part : path.split("/")) {
            if (part.isEmpty()) {
                continue;
            }
            if (part.startsWith("{") && part.endsWith("}")) {
                id.append("By").append(capitalize(part.substring(1, part.length() - 1)));
            } else {
                id.append(capitalize(part));
            }
        }
        return id.toString();
    }

    private static String capitalize(String word) {
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }

    /**
     * A path as a JSON pointer key (that is how a path is written in a pointer such as /paths/~1products~1{name}/get):
     * "~" becomes "~0" and "/" becomes "~1", in that order. escapeJsonPointer("/products/{name}") is "~1products~1{name}".
     */
    public static String escapeJsonPointer(String path) {
        return path.replace("~", "~0").replace("/", "~1");
    }

    /**
     * Which mapped endpoints are missing from the documentation? Both lists hold texts like "GET /products". Returns the missing ones
     * sorted, without duplicates. This is what lesson 44's OpenApiTest checks against the design table.
     */
    public static List<String> undocumented(List<String> mapped, List<String> documented) {
        TreeSet<String> missing = new TreeSet<>(mapped);
        missing.removeAll(documented);
        return List.copyOf(missing);
    }

    /** The standard words of a status code as the documentation shows them: 200 OK, 201 Created, 204 No Content, 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found, 405 Method Not Allowed, 409 Conflict, 415 Unsupported Media Type, 500 Internal Server Error; any other number gives "Unknown". */
    public static String statusPhrase(int status) {
        return switch (status) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 204 -> "No Content";
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 405 -> "Method Not Allowed";
            case 409 -> "Conflict";
            case 415 -> "Unsupported Media Type";
            case 500 -> "Internal Server Error";
            default -> "Unknown";
        };
    }
}
