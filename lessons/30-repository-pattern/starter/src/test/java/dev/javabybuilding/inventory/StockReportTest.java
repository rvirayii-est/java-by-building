package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class StockReportTest {

    private Product product(String name, String price, int quantity, int reorderLevel) {
        Product product = new Product(name, Money.of(price), reorderLevel);
        if (quantity > 0) {
            product.receive(quantity); // receive(0) now throws (lesson 16)
        }
        return product;
    }

    @Test
    void reportHasAHeaderAndOneLinePerProduct() {
        List<Product> products = List.of(product("Rice 25kg", "10.00", 3, 5), product("Sugar 1kg", "5.00", 0, 5));
        String expected = "Product         Qty  Status\n"
                + "Rice 25kg         3  LOW\n"
                + "Sugar 1kg         0  OUT";
        assertEquals(expected, StockReport.build(products));
    }

    @Test
    void totalValueOfNothingIsZeroPesos() {
        assertEquals(Money.ZERO, StockReport.totalValue(List.of()));
    }

    @Test
    void emptyShelfHasOnlyTheHeader() {
        assertEquals("Product         Qty  Status", StockReport.build(List.of()));
    }

    @Test
    void totalValueAddsUpEveryLine() {
        List<Product> products = List.of(product("A", "10.00", 2, 1), product("B", "5.00", 3, 1));
        assertEquals(Money.of("35.00"), StockReport.totalValue(products));
    }
}
