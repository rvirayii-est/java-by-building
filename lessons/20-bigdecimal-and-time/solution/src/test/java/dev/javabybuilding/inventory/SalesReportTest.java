package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SalesReportTest {

    private final Inventory inventory = Inventory.sample();
    private final Customer ana = new Customer("Ana Reyes", "0917");
    private final Customer ben = new Customer("Ben Cruz", "0918");

    /** Four sales: 240.00 cash, 176.40 GCash (after 10% off), 1250.50 cash, 120.00 GCash. */
    private List<Sale> fourSales() {
        inventory.sell("Sardines 155g", 10, ana, new NoDiscount(), PaymentMethod.CASH);
        inventory.sell("Cooking Oil 1L", 2, ana, new PercentDiscount(10), PaymentMethod.GCASH);
        inventory.sell("Rice 25kg", 1, Customer.WALK_IN, new NoDiscount(), PaymentMethod.CASH);
        inventory.sell("Sardines 155g", 5, ben, new NoDiscount(), PaymentMethod.GCASH);
        return inventory.sales();
    }

    @Test
    void needsRestockKeepsOnlyLowAndOutProducts() {
        assertEquals(List.of("Cooking Oil 1L", "Sugar 1kg"), SalesReport.names(SalesReport.needsRestock(inventory.products())));
        assertEquals(2, inventory.needsRestock().size());
    }

    @Test
    void namesTurnsProductsIntoTheirNames() {
        assertEquals(List.of("Rice 25kg", "Cooking Oil 1L", "Sugar 1kg", "Sardines 155g"), SalesReport.names(inventory.products()));
    }

    @Test
    void namesAsTextJoinsWithCommas() {
        assertEquals("Cooking Oil 1L, Sugar 1kg", SalesReport.namesAsText(inventory.needsRestock()));
        assertEquals("", SalesReport.namesAsText(List.of()));
    }

    @Test
    void revenueByPaymentGroupsAndAddsUpEachMethod() {
        Map<PaymentMethod, BigDecimal> revenue = SalesReport.revenueByPayment(fourSales());
        assertEquals(Money.of("1490.50"), revenue.get(PaymentMethod.CASH));
        assertEquals(Money.of("296.40"), revenue.get(PaymentMethod.GCASH));
        assertEquals(2, revenue.size()); // nobody paid by card, so CARD is not there
        assertEquals(List.of(PaymentMethod.CASH, PaymentMethod.GCASH), List.copyOf(revenue.keySet()));
    }

    @Test
    void salesPerCustomerCountsEachCustomersSales() {
        Map<String, Long> counts = SalesReport.salesPerCustomer(fourSales());
        assertEquals(2L, counts.get("Ana Reyes"));
        assertEquals(1L, counts.get("Ben Cruz"));
        assertEquals(1L, counts.get("Walk-in"));
    }

    @Test
    void biggestSaleIsTheOneWithTheHighestTotal() {
        assertEquals(Money.of("1250.50"), SalesReport.biggestSale(fourSales()).orElseThrow().total());
    }

    @Test
    void averageSaleTotalIsTheMeanRoundedToTheCentavo() {
        assertEquals(Money.of("446.73"), SalesReport.averageSaleTotal(fourSales()).orElseThrow()); // 1786.90 / 4 = 446.725, half up
    }

    @Test
    void noSalesMeansEmptyAnswersNotNullAndNotACrash() {
        List<Sale> none = inventory.sales();
        assertTrue(SalesReport.biggestSale(none).isEmpty());
        assertTrue(SalesReport.averageSaleTotal(none).isEmpty());
        assertEquals(Money.ZERO, SalesReport.totalRevenue(none));
        assertTrue(SalesReport.revenueByPayment(none).isEmpty());
    }

    @Test
    void totalRevenueMatchesTheInventorySalesTotal() {
        List<Sale> sales = fourSales();
        assertEquals(Money.of("1786.90"), SalesReport.totalRevenue(sales));
        assertEquals(Money.of("1786.90"), inventory.salesTotal());
    }
}
