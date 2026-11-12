package dev.javabybuilding.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.javabybuilding.api.CreateSaleRequest;
import dev.javabybuilding.api.ProductResponse;
import dev.javabybuilding.inventory.ShopService;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * The capstone feature, tested at the smallest size that can show the bug (lesson 40): a plain test of the service, no Spring.
 * The sample shop has Sugar 1kg at 0 (OUT), Cooking Oil 1L at 4 of a reorder level of 10 (LOW), Rice 25kg at 12 (OK), Sardines at 60 (OK).
 */
class LowStockReportTest {

    private final ShopService shop = TestShop.sample();
    private final ReportService reports = new ReportService(shop);

    private List<String> names() {
        return reports.lowStock().stream().map(ProductResponse::name).toList();
    }

    @Test
    void outOfStockComesFirstThenLow() {
        assertEquals(List.of("Sugar 1kg", "Cooking Oil 1L"), names());
        assertEquals(List.of("OUT", "LOW"), reports.lowStock().stream().map(ProductResponse::status).toList());
    }

    @Test
    void aDeliveryTakesAProductOffTheList() {
        shop.receive("Sugar 1kg", 50);
        assertEquals(List.of("Cooking Oil 1L"), names());
    }

    @Test
    void aSaleCanPutAProductOnTheListAndEachGroupIsInNameOrder() {
        new SaleService(shop).create(new CreateSaleRequest("Rice 25kg", 12, null, null, null, null)); // sells all the rice: OUT
        assertEquals(List.of("Rice 25kg", "Sugar 1kg", "Cooking Oil 1L"), names());
    }

    @Test
    void whenNothingNeedsAttentionTheListIsEmptyNotAnError() {
        shop.receive("Sugar 1kg", 50);
        shop.receive("Cooking Oil 1L", 50);
        assertEquals(List.of(), names());
    }
}
