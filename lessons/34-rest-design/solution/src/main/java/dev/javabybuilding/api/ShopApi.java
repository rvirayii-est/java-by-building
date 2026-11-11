package dev.javabybuilding.api;

import dev.javabybuilding.inventory.Customer;
import dev.javabybuilding.inventory.Discount;
import dev.javabybuilding.inventory.DiscountCodes;
import dev.javabybuilding.inventory.InvalidInputException;
import dev.javabybuilding.inventory.NoDiscount;
import dev.javabybuilding.inventory.PaymentMethod;
import dev.javabybuilding.inventory.Product;
import dev.javabybuilding.inventory.Sale;
import dev.javabybuilding.inventory.ShopException;
import dev.javabybuilding.inventory.ShopService;
import dev.javabybuilding.web.Http;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * The shop's REST API in plain Java, with no web server and no framework: give it an {@link ApiRequest}, get an
 * {@link ApiResponse}. Because nothing here touches a network, a test can call it directly and fast. A small server
 * (lesson 35) carries requests in and out; in lesson 37 Spring's controllers take over the routing.
 *
 * <p>The routes follow {@link ApiDesign}. Every problem becomes one {@link ErrorResponse} through {@link ErrorMapper}.
 */
public class ShopApi {

    /** A request plus the variable parts of its path: for /products/{name} the map holds name. */
    private record Call(ApiRequest request, Map<String, String> vars) {}

    private record Route(String method, String path, List<String> pattern, Function<Call, ApiResponse> handler) {
        /** The values of the {variables} when the path parts fit this route, or null when they do not. */
        Map<String, String> match(List<String> parts) {
            if (parts.size() != pattern.size()) {
                return null;
            }
            Map<String, String> vars = new HashMap<>();
            for (int i = 0; i < parts.size(); i++) {
                String expected = pattern.get(i);
                if (expected.startsWith("{")) {
                    vars.put(expected.substring(1, expected.length() - 1), parts.get(i));
                } else if (!expected.equals(parts.get(i))) {
                    return null;
                }
            }
            return vars;
        }
    }

    private final ShopService shop;
    private final List<Route> routes = new ArrayList<>();

    public ShopApi(ShopService shop) {
        this.shop = shop;
        route("GET", "/products", this::listProducts);
        route("GET", "/products/{name}", call -> ApiResponse.ok(ProductResponse.from(shop.getProduct(call.vars().get("name")))));
        route("POST", "/products", this::createProduct);
        route("PUT", "/products/{name}", this::updateProduct);
        route("DELETE", "/products/{name}", this::deleteProduct);
        route("POST", "/products/{name}/stock", this::receiveStock);
        route("GET", "/sales", this::listSales);
        route("GET", "/sales/{number}", this::oneSale);
        route("POST", "/sales", this::createSale);
        route("GET", "/reports/stock", call -> ApiResponse.ok(StockReportResponse.from(shop.products(), shop.reorderSuggestions())));
        route("GET", "/reports/sales", call -> ApiResponse.ok(SalesReportResponse.from(shop.sales())));
    }

    private void route(String method, String path, Function<Call, ApiResponse> handler) {
        routes.add(new Route(method, path, Http.segments(path), handler));
    }

    /** The routes this API really has, as "GET /products/{name}" lines. A test compares them with {@link ApiDesign}. */
    public List<String> routeTable() {
        return routes.stream().map(route -> route.method() + " " + route.path()).toList();
    }

    /** Answers one request. Never throws: every problem becomes an {@link ErrorResponse}. */
    public ApiResponse handle(ApiRequest request) {
        try {
            List<String> parts = Http.segments(request.path());
            List<String> allowed = new ArrayList<>(); // methods that DO work on this path, for the 405 answer
            for (Route route : routes) {
                Map<String, String> vars = route.match(parts);
                if (vars == null) {
                    continue;
                }
                if (route.method().equals(request.method())) {
                    return route.handler().apply(new Call(request, vars));
                }
                if (!allowed.contains(route.method())) {
                    allowed.add(route.method());
                }
            }
            if (allowed.isEmpty()) {
                return error(404, "No such path: " + request.path(), request);
            }
            ApiResponse wrongMethod = error(405, request.method() + " is not allowed here. Use " + String.join(", ", allowed) + ".", request);
            return new ApiResponse(405, wrongMethod.body(), Map.of("Allow", String.join(", ", allowed)));
        } catch (ShopException e) {
            return error(ErrorMapper.statusFor(e), e.getMessage(), request);
        } catch (RuntimeException e) {
            return error(500, "Something went wrong on the server.", request); // never show a bug's details to a client
        }
    }

    private static ApiResponse error(int status, String message, ApiRequest request) {
        return new ApiResponse(status, new ErrorResponse(status, Http.reason(status), message, request.path()), Map.of());
    }

    // ---- products ----

    private ApiResponse listProducts(Call call) {
        String status = call.request().query().get("status");
        if (status != null && !List.of("OK", "LOW", "OUT").contains(status.toUpperCase())) {
            throw new InvalidInputException("status must be OK, LOW or OUT");
        }
        List<ProductResponse> products = shop.products().stream()
                .filter(product -> status == null || product.status().equalsIgnoreCase(status))
                .map(ProductResponse::from)
                .toList();
        return ApiResponse.ok(products);
    }

    private ApiResponse createProduct(Call call) {
        Body body = new Body(call.request().body());
        String name = body.text("name");
        if (shop.find(name) != null) {
            throw new ProductAlreadyExistsException(shop.find(name)); // a clash with what exists: 409, not 400
        }
        shop.addProduct(name, body.decimal("price"), body.integer("reorderLevel"));
        Product created = shop.getProduct(name);
        return ApiResponse.created(ProductResponse.from(created), "/products/" + encode(created.getName()));
    }

    private ApiResponse updateProduct(Call call) {
        Body body = new Body(call.request().body());
        Product changed = shop.updateProduct(call.vars().get("name"), body.decimal("price"), body.integer("reorderLevel"));
        return ApiResponse.ok(ProductResponse.from(changed));
    }

    private ApiResponse deleteProduct(Call call) {
        shop.deleteProduct(call.vars().get("name"));
        return ApiResponse.noContent();
    }

    private ApiResponse receiveStock(Call call) {
        String name = call.vars().get("name");
        shop.receive(name, new Body(call.request().body()).integer("quantity"));
        return ApiResponse.ok(ProductResponse.from(shop.getProduct(name)));
    }

    // ---- sales ----

    private ApiResponse listSales(Call call) {
        String payment = call.request().query().get("payment");
        PaymentMethod wanted = payment == null ? null : paymentFrom(payment);
        List<SaleResponse> sales = shop.sales().stream()
                .filter(sale -> wanted == null || sale.getPayment() == wanted)
                .map(SaleResponse::from)
                .toList();
        return ApiResponse.ok(sales);
    }

    private ApiResponse oneSale(Call call) {
        int number;
        try {
            number = Integer.parseInt(call.vars().get("number"));
        } catch (NumberFormatException e) {
            throw new InvalidInputException("A sale number is a whole number");
        }
        Sale sale = shop.sales().stream().filter(s -> s.getNumber() == number).findFirst()
                .orElseThrow(() -> new UnknownSaleException(number));
        return ApiResponse.ok(SaleResponse.from(sale));
    }

    private ApiResponse createSale(Call call) {
        Body body = new Body(call.request().body());
        String product = body.text("product");
        int quantity = body.integer("quantity");
        Customer customer = customerFrom(body);
        String discountCode = body.optionalText("discount");
        Discount discount = discountCode == null ? new NoDiscount() : DiscountCodes.decode(discountCode);
        String paymentText = body.optionalText("payment");
        PaymentMethod payment = paymentText == null ? PaymentMethod.CASH : paymentFrom(paymentText);
        Sale sale = shop.sell(product, quantity, customer, discount, payment);
        return ApiResponse.created(SaleResponse.from(sale), "/sales/" + sale.getNumber());
    }

    private static Customer customerFrom(Body body) {
        String name = body.optionalText("customerName");
        if (name == null) {
            return Customer.WALK_IN;
        }
        if (name.isBlank()) {
            throw new InvalidInputException("customerName must not be blank");
        }
        String phone = body.optionalText("customerPhone");
        return new Customer(name.trim(), phone == null ? "" : phone.trim());
    }

    private static PaymentMethod paymentFrom(String text) {
        PaymentMethod method = PaymentMethod.fromText(text);
        if (method == null) {
            throw new InvalidInputException("payment must be Cash, GCash or Card");
        }
        return method;
    }

    /** A product name as one safe piece of a URL: "Rice 25kg" becomes "Rice%2025kg". */
    static String encode(String text) {
        return URLEncoder.encode(text, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
