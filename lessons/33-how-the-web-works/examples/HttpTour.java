import com.sun.net.httpserver.HttpServer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

/**
 * A tour of the two JDK tools for HTTP: HttpServer (answers requests) and HttpClient (sends them). Both run inside this
 * one program, on a free port. No framework, no download.
 *
 * Run:  java HttpTour.java
 */
public class HttpTour {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);

        // A "context" says: requests whose path starts with this text go to this handler.
        server.createContext("/hello", exchange -> {
            String name = exchange.getRequestURI().getQuery(); // everything after the ? , for example name=Ana
            String text = name == null ? "Hello, world!" : "Hello, " + name.replace("name=", "") + "!";
            reply(exchange, 200, text);
        });
        server.createContext("/echo", exchange -> {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("X-Method", exchange.getRequestMethod());
            reply(exchange, 201, "You sent " + body.length() + " characters: " + body);
        });
        server.start();
        String base = "http://localhost:" + server.getAddress().getPort();

        HttpClient client = HttpClient.newHttpClient();

        show("GET /hello", client.send(HttpRequest.newBuilder(URI.create(base + "/hello")).build(), BodyHandlers.ofString()));
        show("GET /hello?name=Ana", client.send(HttpRequest.newBuilder(URI.create(base + "/hello?name=Ana")).build(), BodyHandlers.ofString()));
        show("POST /echo", client.send(HttpRequest.newBuilder(URI.create(base + "/echo"))
                .header("Content-Type", "text/plain")
                .POST(BodyPublishers.ofString("two bags of rice")).build(), BodyHandlers.ofString()));
        show("GET /missing", client.send(HttpRequest.newBuilder(URI.create(base + "/missing")).build(), BodyHandlers.ofString()));

        server.stop(0);
    }

    private static void reply(com.sun.net.httpserver.HttpExchange exchange, int status, String text) throws java.io.IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length); // status and length first, then the body
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    private static void show(String what, HttpResponse<String> response) {
        System.out.println("== " + what);
        System.out.println("status:       " + response.statusCode());
        System.out.println("content-type: " + response.headers().firstValue("Content-Type").orElse("(none)"));
        System.out.println("x-method:     " + response.headers().firstValue("X-Method").orElse("(none)"));
        System.out.println("body:         " + response.body());
    }
}
