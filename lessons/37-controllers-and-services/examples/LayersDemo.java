import com.sun.net.httpserver.HttpServer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Three layers in one file, with no framework, so you can see the DIVISION OF WORK that Spring's @RestController, @Service and
 * @Repository stand for. Each layer talks only to the one below it, and prints a line when it is used.
 *
 *   web layer (controller)  : knows HTTP: paths, status codes, text going in and out
 *   service layer           : knows what the app DOES: the rules, in plain Java, no HTTP
 *   data layer (repository) : knows where the data lives
 *
 * Run:  java LayersDemo.java
 */
public class LayersDemo {

    static final List<String> trace = new ArrayList<>(); // what was called, in order

    // ---- data layer ----
    static class ProductRepository {
        private final List<String> names = new ArrayList<>(List.of("Rice 25kg", "Sugar 1kg"));

        List<String> findAll() {
            trace.add("repository.findAll");
            return names;
        }

        void add(String name) {
            trace.add("repository.add");
            names.add(name);
        }
    }

    // ---- service layer: the rules. It does not know HTTP exists. ----
    static class ProductService {
        private final ProductRepository repository;

        ProductService(ProductRepository repository) {
            this.repository = repository;
        }

        String create(String name) {
            trace.add("service.create");
            String clean = name.trim();
            if (clean.isEmpty()) {
                throw new IllegalArgumentException("A product needs a name"); // a RULE, said without any status number
            }
            if (repository.findAll().stream().anyMatch(existing -> existing.equalsIgnoreCase(clean))) {
                throw new IllegalStateException("That product already exists: " + clean);
            }
            repository.add(clean);
            return clean;
        }

        String get(String name) {
            trace.add("service.get");
            return repository.findAll().stream().filter(existing -> existing.equalsIgnoreCase(name)).findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Unknown product: " + name));
        }
    }

    // ---- web layer: HTTP in, HTTP out. It turns the service's problems into status codes. ----
    static class ProductController {
        private final ProductService service;

        ProductController(ProductService service) {
            this.service = service;
        }

        void register(HttpServer server) {
            server.createContext("/products", exchange -> {
                trace.add("controller." + exchange.getRequestMethod());
                int status;
                String body;
                try {
                    if (exchange.getRequestMethod().equals("POST")) {
                        body = service.create(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
                        status = 201;
                    } else {
                        body = service.get(exchange.getRequestURI().getPath().substring("/products/".length()));
                        status = 200;
                    }
                } catch (IllegalArgumentException e) {
                    status = 400;
                    body = e.getMessage();
                } catch (NoSuchElementException e) {
                    status = 404;
                    body = e.getMessage();
                } catch (IllegalStateException e) {
                    status = 409;
                    body = e.getMessage();
                }
                byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(status, bytes.length);
                exchange.getResponseBody().write(bytes);
                exchange.close();
            });
        }
    }

    public static void main(String[] args) throws Exception {
        // The wiring: each layer is given the layer below it. (Spring does exactly this for you: dependency injection.)
        ProductController controller = new ProductController(new ProductService(new ProductRepository()));
        HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        controller.register(server);
        server.start();
        String base = "http://localhost:" + server.getAddress().getPort();

        request(base, "GET", "/products/rice%2025kg", null);
        request(base, "POST", "/products", "Salt 1kg");
        request(base, "POST", "/products", "salt 1KG");
        request(base, "POST", "/products", "   ");
        request(base, "GET", "/products/pepper", null);
        server.stop(0);
    }

    static void request(String base, String method, String path, String body) throws Exception {
        trace.clear();
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(base + path));
        builder.method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
        HttpResponse<String> response = HttpClient.newHttpClient().send(builder.build(), HttpResponse.BodyHandlers.ofString());
        System.out.printf("%s %s -> %d %s%n    layers: %s%n", method, path, response.statusCode(), response.body(), String.join(" > ", trace));
    }
}
