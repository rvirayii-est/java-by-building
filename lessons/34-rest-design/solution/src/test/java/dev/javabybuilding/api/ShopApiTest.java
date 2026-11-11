package dev.javabybuilding.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.javabybuilding.inventory.InMemoryProductRepository;
import dev.javabybuilding.inventory.InMemorySaleRepository;
import dev.javabybuilding.inventory.InMemoryStockMovementRepository;
import dev.javabybuilding.inventory.Inventory;
import dev.javabybuilding.inventory.Money;
import dev.javabybuilding.inventory.ShopService;
import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Calls the API as plain Java: no server, no network, no JSON. Fast and exact. */
class ShopApiTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-03-01T01:30:00Z"), Inventory.SHOP_ZONE); // 09:30 in Manila

    private ShopService shop;
    private ShopApi api;

    @BeforeEach
    void setUp() {
        shop = new ShopService(new InMemoryProductRepository(), new InMemorySaleRepository(), new InMemoryStockMovementRepository(), CLOCK);
        shop.importFrom(Inventory.sample(new Inventory(CLOCK)));
        api = new ShopApi(shop);
    }

    /** A body from key, value, key, value ... */
    private static Map<String, Object> body(Object... pairs) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            map.put((String) pairs[i], pairs[i + 1]);
        }
        return map;
    }

    private ApiResponse call(String method, String path, Map<String, Object> body) {
        return api.handle(ApiRequest.of(method, path, body));
    }

    private ApiResponse get(String path) {
        return api.handle(ApiRequest.get(path));
    }

    private static ErrorResponse error(ApiResponse response) {
        return (ErrorResponse) response.body();
    }

    // ---- products ----

    @Test
    void listingProductsGivesEveryProductInOrder() {
        ApiResponse response = get("/products");
        assertEquals(200, response.status());
        @SuppressWarnings("unchecked")
        List<ProductResponse> products = (List<ProductResponse>) response.body();
        assertEquals(List.of("Rice 25kg", "Cooking Oil 1L", "Sugar 1kg", "Sardines 155g"), products.stream().map(ProductResponse::name).toList());
    }

    @Test
    void listingCanFilterByStatus() {
        ApiResponse response = api.handle(new ApiRequest("GET", "/products", Map.of("status", "low"), null));
        @SuppressWarnings("unchecked")
        List<ProductResponse> products = (List<ProductResponse>) response.body();
        assertEquals(List.of("Cooking Oil 1L"), products.stream().map(ProductResponse::name).toList());
    }

    @Test
    void anUnknownStatusFilterIs400() {
        ApiResponse response = api.handle(new ApiRequest("GET", "/products", Map.of("status", "HALF"), null));
        assertEquals(400, response.status());
        assertEquals("status must be OK, LOW or OUT", error(response).message());
    }

    @Test
    void oneProductWithASpaceInItsEncodedName() {
        ApiResponse response = get("/products/Rice%2025kg");
        assertEquals(200, response.status());
        assertEquals(new ProductResponse("Rice 25kg", Money.of("1250.50"), 12, 10, "OK", Money.of("15006.00")), response.body());
    }

    @Test
    void anUnknownProductIs404WithOneStandardErrorShape() {
        ApiResponse response = get("/products/salt");
        assertEquals(404, response.status());
        assertEquals(new ErrorResponse(404, "Not Found", "Unknown product: salt", "/products/salt"), response.body());
    }

    @Test
    void creatingAProductIs201WithALocationAndNoStockYet() {
        ApiResponse response = call("POST", "/products", body("name", "Salt 1kg", "price", "20.00", "reorderLevel", 5));
        assertEquals(201, response.status());
        assertEquals("/products/Salt%201kg", response.headers().get("Location"));
        ProductResponse created = (ProductResponse) response.body();
        assertEquals("Salt 1kg", created.name());
        assertEquals(0, created.quantity());
        assertEquals("OUT", created.status());
        assertEquals(200, get("/products/Salt%201kg").status());
    }

    @Test
    void aPriceMayBeSentAsANumberToo() {
        ApiResponse response = call("POST", "/products", body("name", "Salt 1kg", "price", 20.5, "reorderLevel", 5));
        assertEquals(Money.of("20.50"), ((ProductResponse) response.body()).price());
    }

    @Test
    void creatingAProductThatExistsIs409() {
        ApiResponse response = call("POST", "/products", body("name", "rice 25KG", "price", "1.00", "reorderLevel", 1));
        assertEquals(409, response.status());
        assertEquals("That product already exists: Rice 25kg", error(response).message());
        assertEquals(4, shop.products().size());
    }

    @Test
    void badProductFieldsAre400WithAMessageAboutTheField() {
        assertEquals("Missing or empty field: name", error(call("POST", "/products", body("price", "1.00", "reorderLevel", 1))).message());
        assertEquals("price must be a number", error(call("POST", "/products", body("name", "A", "price", "abc", "reorderLevel", 1))).message());
        assertEquals("reorderLevel must be a whole number", error(call("POST", "/products", body("name", "A", "price", "1.00", "reorderLevel", "x"))).message());
        assertEquals("Price must be from PHP 0.01 to PHP 10,000.00.", error(call("POST", "/products", body("name", "A", "price", "0", "reorderLevel", 1))).message());
        assertEquals(400, call("POST", "/products", null).status());
        assertEquals(4, shop.products().size());
    }

    @Test
    void updatingAProductChangesPriceAndReorderLevel() {
        ApiResponse response = call("PUT", "/products/Rice%2025kg", body("price", "1300.00", "reorderLevel", 20));
        assertEquals(200, response.status());
        ProductResponse updated = (ProductResponse) response.body();
        assertEquals(Money.of("1300.00"), updated.price());
        assertEquals(20, updated.reorderLevel());
        assertEquals("LOW", updated.status()); // 12 on the shelf is now at or below the new level of 20
        assertEquals(404, call("PUT", "/products/salt", body("price", "1.00", "reorderLevel", 1)).status());
        assertEquals(400, call("PUT", "/products/Rice%2025kg", body("price", "-1", "reorderLevel", 1)).status());
    }

    @Test
    void deletingAProductWithNoHistoryIs204() {
        call("POST", "/products", body("name", "Salt 1kg", "price", "20.00", "reorderLevel", 5));
        ApiResponse response = call("DELETE", "/products/Salt%201kg", null);
        assertEquals(204, response.status());
        assertNull(response.body());
        assertEquals(404, get("/products/Salt%201kg").status());
    }

    @Test
    void deletingAProductWithHistoryIs409AndUnknownIs404() {
        ApiResponse response = call("DELETE", "/products/Rice%2025kg", null);
        assertEquals(409, response.status());
        assertEquals("Cannot delete Rice 25kg: it already has stock movements or sales.", error(response).message());
        assertEquals(404, call("DELETE", "/products/salt", null).status());
    }

    @Test
    void receivingStockAddsToTheShelf() {
        ApiResponse response = call("POST", "/products/Sugar%201kg/stock", body("quantity", 24));
        assertEquals(200, response.status());
        assertEquals(24, ((ProductResponse) response.body()).quantity());
        assertEquals("OK", ((ProductResponse) response.body()).status());
        assertEquals(24, shop.quantityOf("Sugar 1kg"));
    }

    @Test
    void receivingBadStockIsRefused() {
        assertEquals(400, call("POST", "/products/Sugar%201kg/stock", body("quantity", 0)).status());
        assertEquals(400, call("POST", "/products/Sugar%201kg/stock", body()).status());
        assertEquals(404, call("POST", "/products/salt/stock", body("quantity", 5)).status());
        assertEquals(0, shop.quantityOf("Sugar 1kg"));
    }

    // ---- sales ----

    @Test
    void aCashSaleToAWalkInIs201WithAReceipt() {
        ApiResponse response = call("POST", "/sales", body("product", "Rice 25kg", "quantity", 2));
        assertEquals(201, response.status());
        assertEquals("/sales/1", response.headers().get("Location"));
        SaleResponse sale = (SaleResponse) response.body();
        assertEquals(1, sale.number());
        assertEquals("Walk-in", sale.customer());
        assertEquals("CASH", sale.payment());
        assertEquals("No discount", sale.discount());
        assertEquals(Money.of("2501.00"), sale.total());
        assertEquals(List.of(new SaleLineResponse("Rice 25kg", Money.of("1250.50"), 2, Money.of("2501.00"))), sale.lines());
        assertEquals(10, shop.quantityOf("Rice 25kg"));
    }

    @Test
    void aSaleWithACustomerADiscountAndGcash() {
        ApiResponse response = call("POST", "/sales", body("product", "Rice 25kg", "quantity", 1, "customerName", "Ana Reyes",
                "customerPhone", "09171234567", "discount", "percent:10", "payment", "GCASH"));
        assertEquals(201, response.status());
        SaleResponse sale = (SaleResponse) response.body();
        assertEquals("Ana Reyes", sale.customer());
        assertEquals("10% off", sale.discount());
        assertEquals(Money.of("1250.50"), sale.subtotal());
        assertEquals(Money.of("125.05"), sale.discountAmount());
        assertEquals(Money.of("1125.45"), sale.total());
        assertEquals("GCASH", sale.payment());
    }

    @Test
    void notEnoughStockIs409AndNothingChanges() {
        ApiResponse response = call("POST", "/sales", body("product", "Rice 25kg", "quantity", 13));
        assertEquals(409, response.status());
        assertEquals("Not enough stock: only 12 of Rice 25kg left.", error(response).message());
        assertEquals(12, shop.quantityOf("Rice 25kg"));
        assertTrue(shop.sales().isEmpty());
    }

    @Test
    void badSalesAreRefusedWithTheRightStatus() {
        assertEquals(404, call("POST", "/sales", body("product", "salt", "quantity", 1)).status());
        assertEquals(400, call("POST", "/sales", body("product", "Rice 25kg", "quantity", 0)).status());
        assertEquals("payment must be Cash, GCash or Card", error(call("POST", "/sales", body("product", "Rice 25kg", "quantity", 1, "payment", "bitcoin"))).message());
        assertEquals("Unknown discount code: half", error(call("POST", "/sales", body("product", "Rice 25kg", "quantity", 1, "discount", "half"))).message());
        assertEquals("customerName must not be blank", error(call("POST", "/sales", body("product", "Rice 25kg", "quantity", 1, "customerName", " "))).message());
        assertEquals(12, shop.quantityOf("Rice 25kg"));
    }

    @Test
    void salesCanBeListedFilteredAndReadOneByOne() {
        call("POST", "/sales", body("product", "Rice 25kg", "quantity", 1));
        call("POST", "/sales", body("product", "Sardines 155g", "quantity", 10, "payment", "card"));
        @SuppressWarnings("unchecked")
        List<SaleResponse> all = (List<SaleResponse>) get("/sales").body();
        assertEquals(2, all.size());
        @SuppressWarnings("unchecked")
        List<SaleResponse> cards = (List<SaleResponse>) api.handle(new ApiRequest("GET", "/sales", Map.of("payment", "Card"), null)).body();
        assertEquals(List.of(2), cards.stream().map(SaleResponse::number).toList());
        assertEquals(Money.of("240.00"), ((SaleResponse) get("/sales/2").body()).total());
        assertEquals(404, get("/sales/9").status());
        assertEquals("Unknown sale: #9", error(get("/sales/9")).message());
        assertEquals(400, get("/sales/two").status());
        assertEquals(400, api.handle(new ApiRequest("GET", "/sales", Map.of("payment", "bitcoin"), null)).status());
    }

    // ---- reports ----

    @Test
    void theStockReportListsProductsValueAndWhatToReorder() {
        ApiResponse response = get("/reports/stock");
        assertEquals(200, response.status());
        StockReportResponse report = (StockReportResponse) response.body();
        assertEquals(4, report.products().size());
        assertEquals(Money.of("16838.00"), report.totalValue());
        assertEquals(List.of(new StockReportResponse.Reorder("Cooking Oil 1L", 16), new StockReportResponse.Reorder("Sugar 1kg", 20)), report.reorder());
    }

    @Test
    void theSalesReportOfAnEmptyDayIsAllZeros() {
        SalesReportResponse report = (SalesReportResponse) get("/reports/sales").body();
        assertEquals(new SalesReportResponse(0, Money.ZERO, Money.ZERO, Map.of(), null), report);
    }

    @Test
    void theSalesReportAddsUpEverySale() {
        call("POST", "/sales", body("product", "Rice 25kg", "quantity", 1));                               // 1250.50 cash
        call("POST", "/sales", body("product", "Sardines 155g", "quantity", 10, "payment", "GCash"));      //  240.00 gcash
        call("POST", "/sales", body("product", "Sardines 155g", "quantity", 5));                           //  120.00 cash
        SalesReportResponse report = (SalesReportResponse) get("/reports/sales").body();
        assertEquals(3, report.salesCount());
        assertEquals(Money.of("1610.50"), report.revenue());
        assertEquals(Money.of("536.83"), report.averageSale());
        assertEquals(List.of("CASH", "GCASH"), List.copyOf(report.revenueByPayment().keySet()));
        assertEquals(Money.of("1370.50"), report.revenueByPayment().get("CASH"));
        assertEquals(1, report.biggestSale());
    }

    // ---- the routing itself ----

    @Test
    void anUnknownPathIs404() {
        ApiResponse response = get("/nothing/here");
        assertEquals(new ErrorResponse(404, "Not Found", "No such path: /nothing/here", "/nothing/here"), response.body());
    }

    @Test
    void theWrongMethodIs405WithAnAllowHeader() {
        ApiResponse collection = call("DELETE", "/products", null);
        assertEquals(405, collection.status());
        assertEquals("GET, POST", collection.headers().get("Allow"));
        assertEquals("GET, PUT, DELETE", call("POST", "/products/Rice%2025kg", null).headers().get("Allow"));
        assertEquals("DELETE is not allowed here. Use GET, POST.", error(collection).message());
    }

    @Test
    void aTrailingSlashFindsTheSameRoute() {
        assertEquals(200, get("/products/").status());
    }

    @Test
    void aBugInsideIsA500ThatHidesTheDetails() {
        ShopApi broken = new ShopApi(null); // no shop: the first use throws NullPointerException
        ApiResponse response = broken.handle(ApiRequest.get("/products"));
        assertEquals(500, response.status());
        assertEquals("Something went wrong on the server.", error(response).message());
    }
}
