package dev.javabybuilding.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import dev.javabybuilding.inventory.Customer;
import dev.javabybuilding.inventory.InsufficientStockException;
import dev.javabybuilding.inventory.InvalidInputException;
import dev.javabybuilding.inventory.Inventory;
import dev.javabybuilding.inventory.NoDiscount;
import dev.javabybuilding.inventory.PaymentMethod;
import dev.javabybuilding.inventory.PriceCalculator;
import dev.javabybuilding.inventory.Product;
import dev.javabybuilding.inventory.Sale;
import dev.javabybuilding.inventory.Shop;
import dev.javabybuilding.inventory.UnknownProductException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The shop as a tiny web server, built only from the JDK ({@code com.sun.net.httpserver}). It answers in plain text.
 * Every line of routing, decoding and status-code choosing is written by hand on purpose: after lesson 36 a
 * framework does this for you, and you will know what it saved you.
 *
 * <p>The server handles one request at a time (the default for this class), so the {@link Shop} needs no locking.
 * It listens on this computer only (the loopback address), never on the network.
 */
public class ShopHttpServer implements AutoCloseable {

    /** What to send back: a status number, some text and extra headers. */
    record Reply(int status, String body, Map<String, String> headers) {
        static Reply text(int status, String body) {
            return new Reply(status, body, Map.of());
        }
    }

    private final Shop shop;
    private final HttpServer server;

    /** port 0 means "any free port": ask {@link #port()} which one you got. */
    public ShopHttpServer(Shop shop, int port) throws IOException {
        this.shop = shop;
        this.server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0);
        this.server.createContext("/", this::handle); // one handler for every path; we do the routing ourselves
    }

    public void start() {
        server.start();
    }

    public int port() {
        return server.getAddress().getPort();
    }

    @Override
    public void close() {
        server.stop(0);
    }

    private void handle(HttpExchange exchange) throws IOException {
        Reply reply;
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            reply = route(exchange.getRequestMethod(), exchange.getRequestURI().getRawPath(),
                    exchange.getRequestURI().getRawQuery(), body);
        } catch (UnknownProductException e) {
            reply = Reply.text(404, e.getMessage());
        } catch (InsufficientStockException e) {
            reply = Reply.text(409, e.getMessage());
        } catch (InvalidInputException e) {
            reply = Reply.text(400, e.getMessage());
        } catch (RuntimeException e) {
            reply = Reply.text(500, "Something went wrong on the server.");
        }
        byte[] bytes = reply.body().getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        reply.headers().forEach((name, value) -> exchange.getResponseHeaders().set(name, value));
        exchange.sendResponseHeaders(reply.status(), reply.status() == 204 ? -1 : bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            if (reply.status() != 204) {
                out.write(bytes);
            }
        }
    }

    /** Decides what to answer. No network in here, so tests can call it directly. */
    Reply route(String method, String rawPath, String rawQuery, String body) {
        List<String> parts = Http.segments(rawPath);
        int count = parts.size();
        if (count == 0) {
            return only("GET", method, () -> Reply.text(200, "Inventory & Sales System. Try /products or /health"));
        }
        String first = parts.get(0);
        if (first.equals("health") && count == 1) {
            return only("GET", method, () -> Reply.text(200, "OK"));
        }
        if (first.equals("products") && count == 1) {
            return only("GET", method, () -> listProducts(Http.query(rawQuery).get("status")));
        }
        if (first.equals("products") && count == 2) {
            return only("GET", method, () -> Reply.text(200, line(shop.getProduct(parts.get(1)))));
        }
        if (first.equals("sales") && count == 1) {
            return only("POST", method, () -> sell(body));
        }
        return notFound(rawPath);
    }

    private static Reply notFound(String rawPath) {
        return Reply.text(404, "Not found: " + rawPath);
    }

    /** Runs the action when the method is the allowed one; otherwise 405 and an Allow header that says which one is. */
    private static Reply only(String allowed, String method, Supplier<Reply> action) {
        if (!allowed.equals(method)) {
            return new Reply(405, method + " is not allowed here. Use " + allowed + ".", Map.of("Allow", allowed));
        }
        return action.get();
    }

    private Reply listProducts(String status) {
        StringBuilder text = new StringBuilder();
        for (Product product : shop.products()) {
            if (status == null || product.status().equalsIgnoreCase(status)) {
                text.append(line(product)).append('\n');
            }
        }
        return Reply.text(200, text.toString());
    }

    /** One product as one line: name,price,quantity,status. */
    private static String line(Product product) {
        return product.getName() + "," + product.getPrice().toPlainString() + "," + product.getQuantity() + "," + product.status();
    }

    /** POST /sales with a form body such as product=Rice%2025kg&quantity=2 sells that much for cash to a walk-in. */
    private Reply sell(String body) {
        Map<String, String> form = Http.query(body);
        String product = form.get("product");
        if (product == null || product.isBlank()) {
            return Reply.text(400, "Missing field: product");
        }
        int quantity;
        try {
            quantity = Integer.parseInt(form.getOrDefault("quantity", "").trim());
        } catch (NumberFormatException e) {
            return Reply.text(400, "quantity must be a whole number");
        }
        // The shop throws UnknownProductException / InsufficientStockException / InvalidInputException;
        // handle() turns those into 404 / 409 / 400.
        Sale sale = shop.sell(product, quantity, Customer.WALK_IN, new NoDiscount(), PaymentMethod.CASH);
        return Reply.text(201, "Sale #" + sale.getNumber() + ": " + quantity + " x " + sale.getLines().get(0).productName()
                + " = " + PriceCalculator.format(sale.total()));
    }

    /** Runs the sample shop on port 8080 (or the port you give). Stop it with Ctrl+C. */
    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        ShopHttpServer server = new ShopHttpServer(Inventory.sample(), port);
        server.start();
        System.out.println("Shop server running at http://localhost:" + server.port() + "  (stop it with Ctrl+C)");
    }
}
