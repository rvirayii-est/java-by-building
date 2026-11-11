package dev.javabybuilding.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.javabybuilding.api.CreateProductRequest;
import dev.javabybuilding.api.CreateSaleRequest;
import dev.javabybuilding.api.ErrorResponse;
import dev.javabybuilding.api.ProductResponse;
import dev.javabybuilding.api.ReceiveStockRequest;
import dev.javabybuilding.api.SaleLineResponse;
import dev.javabybuilding.api.SaleResponse;
import dev.javabybuilding.api.UpdateProductRequest;
import dev.javabybuilding.inventory.Customer;
import dev.javabybuilding.inventory.InvalidInputException;
import dev.javabybuilding.inventory.Money;
import dev.javabybuilding.inventory.NoDiscount;
import dev.javabybuilding.inventory.Sale;
import dev.javabybuilding.inventory.SaleLine;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class JsonTest {

    private static final ProductResponse RICE = new ProductResponse("Rice 25kg", Money.of("1250.50"), 12, 10, "OK", Money.of("15006.00"));

    @Test
    void aProductBecomesJsonWithMoneyWrittenAsPlainNumbers() {
        assertEquals("{\"name\":\"Rice 25kg\",\"price\":1250.50,\"quantity\":12,\"reorderLevel\":10,\"status\":\"OK\",\"stockValue\":15006.00}",
                Json.write(RICE));
    }

    @Test
    void aSaleBecomesJsonWithAnIsoDate() {
        SaleResponse sale = new SaleResponse(1, LocalDateTime.of(2026, 3, 1, 9, 30), "Walk-in",
                List.of(new SaleLineResponse("Rice 25kg", Money.of("1250.50"), 2, Money.of("2501.00"))),
                "No discount", Money.of("2501.00"), Money.ZERO, Money.of("2501.00"), "CASH");
        assertEquals("{\"number\":1,\"soldAt\":\"2026-03-01T09:30:00\",\"customer\":\"Walk-in\",\"lines\":[{\"product\":\"Rice 25kg\","
                + "\"unitPrice\":1250.50,\"quantity\":2,\"lineTotal\":2501.00}],\"discount\":\"No discount\",\"subtotal\":2501.00,"
                + "\"discountAmount\":0.00,\"total\":2501.00,\"payment\":\"CASH\"}", Json.write(sale));
    }

    @Test
    void aListBecomesAJsonArrayAndAnErrorKeepsOneShape() {
        assertEquals("[]", Json.write(List.of()));
        assertEquals("{\"status\":404,\"error\":\"Not Found\",\"message\":\"Unknown product: salt\",\"path\":\"/products/salt\"}",
                Json.write(new ErrorResponse(404, "Not Found", "Unknown product: salt", "/products/salt")));
    }

    @Test
    void prettyPrintingSpreadsTheTextOverLines() {
        String pretty = Json.writePretty(new ReceiveStockRequest(24));
        assertEquals("{\n  \"quantity\" : 24\n}", pretty.replace("\r\n", "\n"));
    }

    @Test
    void jsonBecomesARequestRecord() {
        CreateProductRequest request = Json.read("{\"name\":\"Salt 1kg\",\"price\":20.00,\"reorderLevel\":5}", CreateProductRequest.class);
        assertEquals(new CreateProductRequest("Salt 1kg", new BigDecimal("20.00"), 5), request);
    }

    @Test
    void moneyKeepsItsDecimalsWhenRead() {
        UpdateProductRequest request = Json.read("{\"price\":98.50,\"reorderLevel\":0}", UpdateProductRequest.class);
        assertEquals(new BigDecimal("98.50"), request.price()); // BigDecimal.equals checks the decimals too: 98.5 would not be equal
    }

    @Test
    void aMissingFieldIsNullOrZeroAndOptionalFieldsMayBeLeftOut() {
        CreateSaleRequest request = Json.read("{\"product\":\"Rice 25kg\",\"quantity\":2}", CreateSaleRequest.class);
        assertEquals(new CreateSaleRequest("Rice 25kg", 2, null, null, null, null), request);
        assertEquals(0, Json.read("{}", ReceiveStockRequest.class).quantity());
    }

    @Test
    void anObjectReadIntoAMapKeepsIntegersAndExactDecimals() {
        Map<String, Object> map = Json.readObject("{\"quantity\":3,\"price\":98.50,\"name\":\"Rice\",\"gift\":null}");
        assertEquals(3, map.get("quantity"));
        assertEquals(new BigDecimal("98.50"), map.get("price"));
        assertEquals("Rice", map.get("name"));
        assertTrue(map.containsKey("gift"));
    }

    @Test
    void writingAndReadingGivesTheSameRecordBack() {
        assertEquals(RICE, Json.read(Json.write(RICE), ProductResponse.class));
    }

    @Test
    void aFieldTheRecordDoesNotKnowIsRefusedSoTyposAreCaught() {
        InvalidInputException e = assertThrows(InvalidInputException.class,
                () -> Json.read("{\"name\":\"A\",\"price\":1,\"reorderLvl\":5}", CreateProductRequest.class));
        assertTrue(e.getMessage().startsWith("Invalid value for 'reorderLvl': Unrecognized field \"reorderLvl\""), e.getMessage());
    }

    @Test
    void aValueOfTheWrongKindNamesTheField() {
        InvalidInputException e = assertThrows(InvalidInputException.class,
                () -> Json.read("{\"quantity\":\"many\"}", ReceiveStockRequest.class));
        assertTrue(e.getMessage().startsWith("Invalid value for 'quantity': Cannot deserialize value of type `int`"), e.getMessage());
    }

    @Test
    void textThatIsNotJsonIsRefused() {
        InvalidInputException e = assertThrows(InvalidInputException.class, () -> Json.readObject("{\"name\": "));
        assertTrue(e.getMessage().startsWith("Malformed JSON: "), e.getMessage());
        assertThrows(InvalidInputException.class, () -> Json.readObject("[1, 2, 3]")); // an array is not an object
        assertThrows(InvalidInputException.class, () -> Json.readObject("hello"));
    }

    @Test
    void convertTurnsAMapIntoARecordAndNullIntoAnEmptyOne() {
        assertEquals(new ReceiveStockRequest(5), Json.convert(Map.of("quantity", 5), ReceiveStockRequest.class));
        assertEquals(new ReceiveStockRequest(0), Json.convert(null, ReceiveStockRequest.class));
        assertThrows(InvalidInputException.class, () -> Json.convert(Map.of("quantity", "x"), ReceiveStockRequest.class));
    }

    // ---- why the API sends DTOs and not the domain classes ----

    @Test
    void aDomainSaleCannotBeWrittenAtAllBecauseDiscountIsAnInterfaceWithNoFields() {
        Sale sale = new Sale(1, Customer.WALK_IN, LocalDateTime.of(2026, 3, 1, 9, 30));
        sale.addLine(new SaleLine("Rice 25kg", Money.of("1250.50"), 1));
        sale.setDiscount(new NoDiscount());
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> Json.write(sale));
        assertTrue(e.getMessage().contains("No serializer found for class dev.javabybuilding.inventory.NoDiscount"), e.getMessage());
    }

    @Test
    void aDomainCustomerWouldLeakFieldsNobodyDecidedToPublish() {
        Customer ana = new Customer("Ana Reyes", "09171234567");
        ana.addPurchase(new BigDecimal("250.00"));
        String json = Json.write(ana);
        assertTrue(json.contains("\"phone\":\"09171234567\""), json); // the phone number is out
        assertTrue(json.contains("\"points\":2"), json);              // and so is everything with a getter
        assertTrue(json.contains("\"walkIn\":false"), json);          // even a helper method such as isWalkIn()
    }
}
