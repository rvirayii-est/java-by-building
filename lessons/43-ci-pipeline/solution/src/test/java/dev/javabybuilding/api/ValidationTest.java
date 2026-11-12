package dev.javabybuilding.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * The rules on the request records, checked WITHOUT Spring MVC and without a server: Bean Validation is a library you can call
 * yourself. {@code validator.validate(object)} returns the violations; an empty set means "valid". The controller's {@code @Valid}
 * does the same call for you before your method runs.
 */
class ValidationTest {

    private static final Validator VALIDATOR = createValidator();

    private static Validator createValidator() {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.afterPropertiesSet();
        return factory;
    }

    /** "field: message" for every violation, sorted, so a test can compare whole lists. */
    private static List<String> problems(Object request) {
        return VALIDATOR.validate(request).stream()
                .map((ConstraintViolation<Object> v) -> v.getPropertyPath() + ": " + v.getMessage())
                .sorted()
                .toList();
    }

    // ---- CreateProductRequest ----

    @Test
    void aGoodProductHasNoProblems() {
        assertTrue(problems(new CreateProductRequest("Salt 1kg", new BigDecimal("20.00"), 5)).isEmpty());
        assertTrue(problems(new CreateProductRequest("Free sample", new BigDecimal("0.01"), 0)).isEmpty());
        assertTrue(problems(new CreateProductRequest("Gold", new BigDecimal("10000.00"), 0)).isEmpty());
    }

    @Test
    void anEmptyProductNamesEveryMissingField() {
        assertEquals(List.of("name: is required", "price: is required", "reorderLevel: is required"),
                problems(new CreateProductRequest(null, null, null)));
    }

    @Test
    void aBlankOrTooLongNameIsRefused() {
        assertEquals(List.of("name: is required"), problems(new CreateProductRequest("   ", new BigDecimal("1"), 1)));
        assertEquals(List.of("name: must be at most 60 characters"), problems(new CreateProductRequest("x".repeat(61), new BigDecimal("1"), 1)));
    }

    @Test
    void thePriceHasLimitsAndAtMostTwoDecimals() {
        assertEquals(List.of("price: must be at least 0.01"), problems(new CreateProductRequest("A", new BigDecimal("0"), 1)));
        assertEquals(List.of("price: must be at least 0.01"), problems(new CreateProductRequest("A", new BigDecimal("-5.00"), 1)));
        assertEquals(List.of("price: must be at most 10000.00"), problems(new CreateProductRequest("A", new BigDecimal("10000.01"), 1)));
        assertEquals(List.of("price: must have at most 2 decimals"), problems(new CreateProductRequest("A", new BigDecimal("12.345"), 1)));
    }

    @Test
    void aNegativeReorderLevelIsRefusedButZeroIsFine() {
        assertEquals(List.of("reorderLevel: must be 0 or more"), problems(new CreateProductRequest("A", new BigDecimal("1"), -1)));
        assertTrue(problems(new CreateProductRequest("A", new BigDecimal("1"), 0)).isEmpty());
    }

    @Test
    void severalProblemsAreAllReportedTogether() {
        assertEquals(List.of("name: is required", "price: must be at least 0.01", "reorderLevel: must be 0 or more"),
                problems(new CreateProductRequest("", new BigDecimal("0"), -1)));
    }

    // ---- UpdateProductRequest and ReceiveStockRequest ----

    @Test
    void anUpdateNeedsAPriceAndAReorderLevel() {
        assertTrue(problems(new UpdateProductRequest(new BigDecimal("1300.00"), 15)).isEmpty());
        assertEquals(List.of("price: is required", "reorderLevel: is required"), problems(new UpdateProductRequest(null, null)));
        assertEquals(List.of("price: must be at least 0.01"), problems(new UpdateProductRequest(new BigDecimal("0"), 1)));
    }

    @Test
    void aDeliveryNeedsAtLeastOneUnit() {
        assertTrue(problems(new ReceiveStockRequest(24)).isEmpty());
        assertEquals(List.of("quantity: is required"), problems(new ReceiveStockRequest(null)));
        assertEquals(List.of("quantity: must be at least 1"), problems(new ReceiveStockRequest(0)));
        assertEquals(List.of("quantity: must be at least 1"), problems(new ReceiveStockRequest(-3)));
    }

    // ---- CreateSaleRequest ----

    @Test
    void theSmallestGoodSaleHasOnlyAProductAndAQuantity() {
        assertTrue(problems(new CreateSaleRequest("Rice 25kg", 1, null, null, null, null)).isEmpty());
    }

    @Test
    void aSaleNeedsAProductAndAQuantityOfAtLeastOne() {
        assertEquals(List.of("product: is required", "quantity: is required"), problems(new CreateSaleRequest(null, null, null, null, null, null)));
        assertEquals(List.of("quantity: must be at least 1"), problems(new CreateSaleRequest("Rice 25kg", 0, null, null, null, null)));
    }

    @Test
    void theOptionalFieldsAreCheckedOnlyWhenTheyAreSent() {
        assertTrue(problems(new CreateSaleRequest("Rice", 1, "Ana Reyes", "+63 917 123 4567", "percent:10", "GCash")).isEmpty());
        assertEquals(List.of("customerName: must not be blank"), problems(new CreateSaleRequest("Rice", 1, "   ", null, null, null)));
        assertEquals(List.of("customerPhone: may contain only digits, spaces and + ( ) -"), problems(new CreateSaleRequest("Rice", 1, null, "call me", null, null)));
    }

    @Test
    void discountCodesAndPaymentNamesMustBeOnesTheShopKnows() {
        for (String good : List.of("none", "senior", "percent:10", "percent:100", "fixed:50", "fixed:50.00")) {
            assertTrue(problems(new CreateSaleRequest("Rice", 1, null, null, good, null)).isEmpty(), good);
        }
        for (String bad : List.of("half", "percent:", "percent:1000", "fixed:-5", "fixed:5.123", "PERCENT:10")) {
            assertEquals(List.of("discount: must be none, senior, percent:<number> or fixed:<amount>"),
                    problems(new CreateSaleRequest("Rice", 1, null, null, bad, null)), bad);
        }
        for (String good : List.of("cash", "Cash", "GCASH", "gcash", "Card")) {
            assertTrue(problems(new CreateSaleRequest("Rice", 1, null, null, null, good)).isEmpty(), good);
        }
        assertEquals(List.of("payment: must be Cash, GCash or Card"), problems(new CreateSaleRequest("Rice", 1, null, null, null, "bitcoin")));
    }
}
