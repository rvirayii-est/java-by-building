package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import org.junit.jupiter.api.Test;

class SaleTest {

    @Test
    void aSaleLineKnowsItsTotal() {
        SaleLine line = new SaleLine("Rice 25kg", 125050, 3);
        assertEquals("Rice 25kg", line.getProductName());
        assertEquals(125050, line.getUnitPriceCentavos());
        assertEquals(3, line.getQuantity());
        assertEquals(375150, line.lineTotal());
    }

    @Test
    void aSaleRemembersItsNumberAndCustomer() {
        Customer ana = new Customer("Ana", "");
        Sale sale = new Sale(7, ana);
        assertEquals(7, sale.getNumber());
        assertSame(ana, sale.getCustomer());
    }

    @Test
    void anEmptySaleIsZero() {
        Sale sale = new Sale(1, new Customer("Ana", ""));
        assertEquals(0, sale.total());
        assertEquals(0, sale.lineCount());
    }

    @Test
    void theTotalAddsEveryLine() {
        Sale sale = new Sale(1, new Customer("Ana", ""));
        sale.addLine(new SaleLine("Rice 25kg", 125050, 2));
        sale.addLine(new SaleLine("Sardines 155g", 2400, 5));
        assertEquals(2, sale.lineCount());
        assertEquals(250100 + 12000, sale.total());
    }

    @Test
    void aSaleWithoutADiscountPaysTheSubtotal() {
        Sale sale = new Sale(1, new Customer("Ana", ""));
        sale.addLine(new SaleLine("Rice 25kg", 125050, 2));
        assertEquals(250100, sale.subtotal());
        assertEquals(0, sale.discountAmount());
        assertEquals(250100, sale.total());
    }

    @Test
    void aSaleAppliesWhicheverDiscountItWasGiven() {
        Sale sale = new Sale(1, new Customer("Ana", ""));
        sale.addLine(new SaleLine("Rice 25kg", 125050, 2));
        sale.setDiscount(new SeniorPwdDiscount());
        assertEquals(50020, sale.discountAmount());
        assertEquals(200080, sale.total());
        sale.setDiscount(new FixedDiscount(100));  // swap it: same Sale, different Discount object
        assertEquals(250000, sale.total());
    }

    @Test
    void getLinesIsACopy() {
        Sale sale = new Sale(1, new Customer("Ana", ""));
        sale.addLine(new SaleLine("Sugar 1kg", 7500, 1));
        List<SaleLine> copy = sale.getLines();
        sale.addLine(new SaleLine("Salt", 3500, 1));
        assertEquals(1, copy.size());
        assertEquals(2, sale.getLines().size());
    }
}
