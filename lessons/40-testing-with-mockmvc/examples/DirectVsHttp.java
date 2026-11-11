import com.sun.net.httpserver.HttpServer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * The idea behind MockMvc in 70 lines. A web app is really a FUNCTION: request in, response out. A real server wraps that function
 * in sockets and threads. A test does not need the wrapping: it can call the function directly, in the same JVM. MockMvc does exactly
 * that for Spring MVC; it builds a request object, hands it to Spring's front controller, and gives you the response object.
 * The same checks run both ways below, and give the same answers.
 *
 * Run:  java DirectVsHttp.java
 */
public class DirectVsHttp {

    record Request(String method, String path) {}

    record Response(int status, String body) {}

    /** The "application": a pure function. No network anywhere in it. */
    static final Function<Request, Response> APP = request -> {
        if (!request.method().equals("GET")) {
            return new Response(405, "Use GET");
        }
        return switch (request.path()) {
            case "/health" -> new Response(200, "OK");
            case "/products/rice" -> new Response(200, "Rice 25kg,1250.50,12,OK");
            default -> new Response(404, "Not found: " + request.path());
        };
    };

    /** A check that works with ANY way of calling the app: it only needs "send a request, get a response". */
    static void check(String how, Function<Request, Response> send) {
        Response health = send.apply(new Request("GET", "/health"));
        Response rice = send.apply(new Request("GET", "/products/rice"));
        Response missing = send.apply(new Request("GET", "/salt"));
        Response wrongMethod = send.apply(new Request("DELETE", "/health"));
        System.out.printf("%-7s health=%d %s | rice=%d | missing=%d %s | delete=%d%n", how, health.status(), health.body(), rice.status(),
                missing.status(), missing.body(), wrongMethod.status());
    }

    public static void main(String[] args) throws Exception {
        // 1. Direct: call the function. This is the MockMvc way. Instant, nothing to start or stop, nothing can clash on a port.
        check("direct", APP);

        // 2. Over HTTP: wrap the same function in a real server and call it with a real client. This is what a deployed app does.
        HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/", exchange -> {
            Response response = APP.apply(new Request(exchange.getRequestMethod(), exchange.getRequestURI().getPath()));
            byte[] bytes = response.body().getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(response.status(), bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
        HttpClient client = HttpClient.newHttpClient();
        check("http", request -> {
            try {
                HttpResponse<String> answer = client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + server.getAddress().getPort() + request.path()))
                        .method(request.method(), HttpRequest.BodyPublishers.noBody()).build(), HttpResponse.BodyHandlers.ofString());
                return new Response(answer.statusCode(), answer.body());
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
        server.stop(0);

        System.out.println("Same answers both ways. The direct way is what makes hundreds of API tests run in seconds.");
    }
}
