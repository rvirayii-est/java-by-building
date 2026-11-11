package dev.javabybuilding.api;

import java.util.List;

/**
 * The design of the shop's REST API as DATA: one row per endpoint. The README shows the same table; tests make sure the
 * code does what the table promises (design first, then code that follows it).
 *
 * <p>Rules used (lesson 34): the path names a THING (a noun, plural: /products), the method says the ACTION (GET reads,
 * POST creates, PUT changes, DELETE removes), and the status code says how it went.
 */
public final class ApiDesign {

    private ApiDesign() {}

    /** One endpoint: HTTP method, path pattern ({name} is a variable part), status on success, and what it is for. */
    public record Endpoint(String method, String path, int success, String purpose) {
        /** For example "GET /products/{name}". */
        public String signature() {
            return method + " " + path;
        }
    }

    public static final List<Endpoint> ENDPOINTS = List.of(
            new Endpoint("GET", "/products", 200, "list the products (optional ?status=OK|LOW|OUT)"),
            new Endpoint("GET", "/products/{name}", 200, "one product"),
            new Endpoint("POST", "/products", 201, "create a product (it starts with 0 in stock)"),
            new Endpoint("PUT", "/products/{name}", 200, "change a product's price and reorder level"),
            new Endpoint("DELETE", "/products/{name}", 204, "delete a product that has no history"),
            new Endpoint("POST", "/products/{name}/stock", 200, "receive a delivery: add units to the shelf"),
            new Endpoint("GET", "/sales", 200, "list the sales (optional ?payment=CASH|GCASH|CARD)"),
            new Endpoint("GET", "/sales/{number}", 200, "one sale (a receipt)"),
            new Endpoint("POST", "/sales", 201, "record a sale"),
            new Endpoint("GET", "/reports/stock", 200, "the stock report"),
            new Endpoint("GET", "/reports/sales", 200, "the sales report"));
}
