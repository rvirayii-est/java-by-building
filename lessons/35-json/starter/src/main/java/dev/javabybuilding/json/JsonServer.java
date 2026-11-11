package dev.javabybuilding.json;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import dev.javabybuilding.api.ApiRequest;
import dev.javabybuilding.api.ApiResponse;
import dev.javabybuilding.api.ErrorResponse;
import dev.javabybuilding.api.ShopApi;
import dev.javabybuilding.inventory.Database;
import dev.javabybuilding.inventory.Inventory;
import dev.javabybuilding.inventory.InvalidInputException;
import dev.javabybuilding.inventory.JdbcProductRepository;
import dev.javabybuilding.inventory.JdbcSaleRepository;
import dev.javabybuilding.inventory.JdbcStockMovementRepository;
import dev.javabybuilding.inventory.Migrator;
import dev.javabybuilding.inventory.ShopService;
import dev.javabybuilding.web.Http;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Map;

/**
 * The shop's REST API on a real port, with JSON, using only the JDK server and Jackson. It is a thin carrier: it reads the
 * HTTP request into an {@link ApiRequest} (JSON text becomes a Map), asks {@link ShopApi} for the answer, and writes the
 * {@link ApiResponse} back as JSON. All the rules live in ShopApi. This is the whole job a framework does for you in lesson 37,
 * and you will be glad of it.
 *
 * <p>One request at a time (the JDK server's default), which is what the single JDBC connection of lesson 29 needs.
 */
public class JsonServer implements AutoCloseable {

    private final ShopApi api;
    private final HttpServer server;

    public JsonServer(ShopApi api, int port) throws IOException {
        this.api = api;
        this.server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0);
        this.server.createContext("/", this::handle);
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
        String path = exchange.getRequestURI().getRawPath();
        ApiResponse response;
        try {
            String text = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> body = text.isBlank() ? null : Json.readObject(text); // JSON object -> Map
            ApiRequest request = new ApiRequest(exchange.getRequestMethod(), path,
                    Http.query(exchange.getRequestURI().getRawQuery()), body);
            response = api.handle(request);
        } catch (InvalidInputException e) { // the body was not JSON, or not a JSON object
            response = new ApiResponse(400, new ErrorResponse(400, Http.reason(400), e.getMessage(), path), Map.of());
        }
        // TODO 5: write the answer. bytes = Json.write(response.body()) as UTF-8 (or no bytes at all when the body is null).
        // Set the header Content-Type: application/json, copy every header of response.headers(), then
        // exchange.sendResponseHeaders(response.status(), bytes.length == 0 ? -1 : bytes.length) (-1 means "no body"),
        // write the bytes to exchange.getResponseBody() and close the exchange.
        throw new UnsupportedOperationException("TODO 5");
    }

    /** A shop on an in-memory H2 database, filled with the sample products. The data is gone when the program stops. */
    public static ShopService sampleShop() {
        Database database = Database.openMemory("json-shop");
        Migrator.standard(database).migrate();
        ShopService shop = new ShopService(new JdbcProductRepository(database), new JdbcSaleRepository(database),
                new JdbcStockMovementRepository(database), Clock.system(Inventory.SHOP_ZONE), database);
        shop.importFrom(Inventory.sample());
        return shop;
    }

    /** Runs the API on port 8080 (or the port you give). Stop it with Ctrl+C. */
    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        JsonServer server = new JsonServer(new ShopApi(sampleShop()), port);
        server.start();
        System.out.println("Shop API running at http://localhost:" + server.port() + "  (stop it with Ctrl+C)");
    }
}
