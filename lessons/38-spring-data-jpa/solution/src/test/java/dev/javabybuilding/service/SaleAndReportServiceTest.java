package dev.javabybuilding.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.javabybuilding.api.CreateSaleRequest;
import dev.javabybuilding.api.SaleResponse;
import dev.javabybuilding.api.SalesReportResponse;
import dev.javabybuilding.api.StockReportResponse;
import dev.javabybuilding.api.UnknownSaleException;
import dev.javabybuilding.inventory.InsufficientStockException;
import dev.javabybuilding.inventory.InvalidInputException;
import dev.javabybuilding.inventory.Money;
import dev.javabybuilding.inventory.ShopService;
import dev.javabybuilding.inventory.UnknownProductException;
import java.util.List;
import org.junit.jupiter.api.Test;

class SaleAndReportServiceTest {

    private final ShopService shop = TestShop.sample();
    private final SaleService sales = new SaleService(shop);
    private final ReportService reports = new ReportService(shop);

    private static CreateSaleRequest cash(String product, int quantity) {
        return new CreateSaleRequest(product, quantity, null, null, null, null);
    }

    @Test
    void aSaleToAWalkInPaysCashAtFullPrice() {
        SaleResponse sale = sales.create(cash("Rice 25kg", 2));
        assertEquals(1, sale.number());
        assertEquals("Walk-in", sale.customer());
        assertEquals("CASH", sale.payment());
        assertEquals(Money.of("2501.00"), sale.total());
        assertEquals(10, shop.quantityOf("Rice 25kg"));
    }

    @Test
    void aSaleWithCustomerDiscountAndPayment() {
        SaleResponse sale = sales.create(new CreateSaleRequest("Rice 25kg", 1, " Ana Reyes ", "09171234567", "fixed:50.00", "gcash"));
        assertEquals("Ana Reyes", sale.customer());
        assertEquals("GCASH", sale.payment());
        assertEquals(Money.of("1200.50"), sale.total());
    }

    @Test
    void badSalesAreRefusedAndChangeNothing() {
        assertThrows(InsufficientStockException.class, () -> sales.create(cash("Rice 25kg", 13)));
        assertThrows(UnknownProductException.class, () -> sales.create(cash("salt", 1)));
        assertThrows(InvalidInputException.class, () -> sales.create(cash("Rice 25kg", 0)));
        assertThrows(InvalidInputException.class, () -> sales.create(cash(null, 1)));
        assertEquals("payment must be Cash, GCash or Card", assertThrows(InvalidInputException.class,
                () -> sales.create(new CreateSaleRequest("Rice 25kg", 1, null, null, null, "bitcoin"))).getMessage());
        assertThrows(InvalidInputException.class, () -> sales.create(new CreateSaleRequest("Rice 25kg", 1, null, null, "half", null)));
        assertThrows(InvalidInputException.class, () -> sales.create(new CreateSaleRequest("Rice 25kg", 1, "  ", null, null, null)));
        assertEquals(12, shop.quantityOf("Rice 25kg"));
        assertTrue(shop.sales().isEmpty());
    }

    @Test
    void salesCanBeListedFilteredAndFoundByNumber() {
        sales.create(cash("Rice 25kg", 1));
        sales.create(new CreateSaleRequest("Sardines 155g", 10, null, null, null, "Card"));
        assertEquals(2, sales.list(null).size());
        assertEquals(List.of(2), sales.list("card").stream().map(SaleResponse::number).toList());
        assertEquals(Money.of("240.00"), sales.get(2).total());
        assertThrows(UnknownSaleException.class, () -> sales.get(9));
        assertThrows(InvalidInputException.class, () -> sales.list("bitcoin"));
    }

    @Test
    void theStockReportHasEveryProductTheValueAndTheReorderList() {
        StockReportResponse report = reports.stockReport();
        assertEquals(4, report.products().size());
        assertEquals(Money.of("16838.00"), report.totalValue());
        assertEquals(List.of("Cooking Oil 1L", "Sugar 1kg"), report.reorder().stream().map(StockReportResponse.Reorder::product).toList());
    }

    @Test
    void theSalesReportAddsUpTheSales() {
        sales.create(cash("Rice 25kg", 1));
        sales.create(new CreateSaleRequest("Sardines 155g", 10, null, null, null, "GCASH"));
        SalesReportResponse report = reports.salesReport();
        assertEquals(2, report.salesCount());
        assertEquals(Money.of("1490.50"), report.revenue());
        assertEquals(Money.of("745.25"), report.averageSale());
        assertEquals(1, report.biggestSale());
    }
}
