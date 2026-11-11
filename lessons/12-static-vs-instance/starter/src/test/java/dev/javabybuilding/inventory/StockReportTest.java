package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class StockReportTest {

    private Product product(String name, int price, int quantity, int reorderLevel) {
        Product product = new Product(name, price, reorderLevel);
        product.receive(quantity);
        return product;
    }

    @Test
    void reportHasAHeaderAndOneLinePerProduct() {
        List<Product> products = List.of(product("Rice 25kg", 1000, 3, 5), product("Sugar 1kg", 500, 0, 5));
        String expected = "Product         Qty  Status\n"
                + "Rice 25kg         3  LOW\n"
                + "Sugar 1kg         0  OUT";
        assertEquals(expected, StockReport.build(products));
    }

    @Test
    void emptyShelfHasOnlyTheHeader() {
        assertEquals("Product         Qty  Status", StockReport.build(List.of()));
    }

    @Test
    void totalValueAddsUpEveryLine() {
        List<Product> products = List.of(product("A", 1000, 2, 1), product("B", 500, 3, 1));
        assertEquals(2 * 1000 + 3 * 500, StockReport.totalValue(products));
    }
}
