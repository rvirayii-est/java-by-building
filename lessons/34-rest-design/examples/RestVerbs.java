import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

/**
 * One tiny REST resource, "/items" (a shopping list), to see the rules of lesson 34 in action:
 * a noun in the path, the method as the verb, a status code for the result, and what "idempotent" means.
 *
 * Run:  java RestVerbs.java
 */
public class RestVerbs {

    private static final Map<Integer, String> items = new TreeMap<>(); // id -> text
    private static int nextId = 1;

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/items", RestVerbs::handle);
        server.start();
        String base = "http://localhost:" + server.getAddress().getPort();
        HttpClient client = HttpClient.newHttpClient();

        System.out.println("-- POST is not idempotent: sending the same request twice makes TWO items");
        call(client, base, "POST", "/items", "milk");
        call(client, base, "POST", "/items", "milk");
        System.out.println("-- PUT is idempotent: sending the same request twice leaves the same state");
        call(client, base, "PUT", "/items/1", "milk 2L");
        call(client, base, "PUT", "/items/1", "milk 2L");
        call(client, base, "GET", "/items", null);
        System.out.println("-- DELETE: the state after is the same, though the second answer is 404");
        call(client, base, "DELETE", "/items/2", null);
        call(client, base, "DELETE", "/items/2", null);
        call(client, base, "GET", "/items", null);
        System.out.println("-- Problems");
        call(client, base, "GET", "/items/99", null);
        call(client, base, "PUT", "/items/1", "");
        call(client, base, "PATCH", "/items", null);
        server.stop(0);
    }

    // ---- the server: one handler for /items and /items/{id} ----

    private static void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] parts = exchange.getRequestURI().getPath().substring(1).split("/"); // ["items"] or ["items", "1"]
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        if (parts.length == 1) {
            if (method.equals("GET")) {
                send(exchange, 200, items.toString());
            } else if (method.equals("POST")) {
                items.put(nextId, body);
                exchange.getResponseHeaders().set("Location", "/items/" + nextId);
                send(exchange, 201, "created #" + nextId++);
            } else {
                exchange.getResponseHeaders().set("Allow", "GET, POST");
                send(exchange, 405, "use GET or POST");
            }
            return;
        }
        int id = Integer.parseInt(parts[1]);
        if (!items.containsKey(id)) {
            send(exchange, 404, "no item " + id);
        } else if (method.equals("GET")) {
            send(exchange, 200, items.get(id));
        } else if (method.equals("PUT")) {
            if (body.isBlank()) {
                send(exchange, 400, "an item needs some text");
            } else {
                items.put(id, body);
                send(exchange, 200, "replaced #" + id);
            }
        } else if (method.equals("DELETE")) {
            items.remove(id);
            send(exchange, 204, "");
        } else {
            exchange.getResponseHeaders().set("Allow", "GET, PUT, DELETE");
            send(exchange, 405, "use GET, PUT or DELETE");
        }
    }

    private static void send(HttpExchange exchange, int status, String text) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, status == 204 ? -1 : bytes.length);
        if (status != 204) {
            exchange.getResponseBody().write(bytes);
        }
        exchange.close();
    }

    // ---- the client ----

    private static void call(HttpClient client, String base, String method, String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(base + path))
                .method(method, body == null ? BodyPublishers.noBody() : BodyPublishers.ofString(body)).build();
        var response = client.send(request, BodyHandlers.ofString());
        String location = response.headers().firstValue("Location").map(l -> "  Location: " + l).orElse("");
        System.out.printf("%-6s %-9s -> %d %s%s%n", method, path, response.statusCode(), response.body(), location);
    }
}
