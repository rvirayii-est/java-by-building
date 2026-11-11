package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class SaleTest {

    private static final LocalDateTime NOON = LocalDateTime.of(2026, 3, 1, 12, 0);

    @Test
    void aSaleLineKnowsItsTotal() {
        SaleLine line = new SaleLine("Rice 25kg", Money.of("1250.50"), 3);
        assertEquals("Rice 25kg", line.productName());
        assertEquals(Money.of("1250.50"), line.unitPrice());
        assertEquals(3, line.quantity());
        assertEquals(Money.of("3751.50"), line.lineTotal());
    }

    @Test
    void saleLinesAreRecordsSoEqualValuesAreEqual() {
        assertEquals(new SaleLine("Salt", Money.of("35.00"), 2), new SaleLine("Salt", Money.of("35.00"), 2));
        assertEquals("SaleLine[productName=Salt, unitPrice=35.00, quantity=2]",
                new SaleLine("Salt", Money.of("35.00"), 2).toString());
    }

    @Test
    void aSalePaysInCashUnlessToldOtherwise() {
        Sale sale = new Sale(1, new Customer("Ana", ""), NOON);
        assertEquals(PaymentMethod.CASH, sale.getPayment());
        sale.setPayment(PaymentMethod.GCASH);
        assertEquals(PaymentMethod.GCASH, sale.getPayment());
    }

    @Test
    void aSaleRemembersItsNumberAndCustomer() {
        Customer ana = new Customer("Ana", "");
        Sale sale = new Sale(7, ana, NOON);
        assertEquals(7, sale.getNumber());
        assertSame(ana, sale.getCustomer());
    }

    @Test
    void anEmptySaleIsZero() {
        Sale sale = new Sale(1, new Customer("Ana", ""), NOON);
        assertEquals(Money.ZERO, sale.total());
        assertEquals(0, sale.lineCount());
    }

    @Test
    void theTotalAddsEveryLine() {
        Sale sale = new Sale(1, new Customer("Ana", ""), NOON);
        sale.addLine(new SaleLine("Rice 25kg", Money.of("1250.50"), 2));
        sale.addLine(new SaleLine("Sardines 155g", Money.of("24.00"), 5));
        assertEquals(2, sale.lineCount());
        assertEquals(Money.of("2621.00"), sale.total());
    }

    @Test
    void aSaleWithoutADiscountPaysTheSubtotal() {
        Sale sale = new Sale(1, new Customer("Ana", ""), NOON);
        sale.addLine(new SaleLine("Rice 25kg", Money.of("1250.50"), 2));
        assertEquals(Money.of("2501.00"), sale.subtotal());
        assertEquals(Money.ZERO, sale.discountAmount());
        assertEquals(Money.of("2501.00"), sale.total());
    }

    @Test
    void aSaleAppliesWhicheverDiscountItWasGiven() {
        Sale sale = new Sale(1, new Customer("Ana", ""), NOON);
        sale.addLine(new SaleLine("Rice 25kg", Money.of("1250.50"), 2));
        sale.setDiscount(new SeniorPwdDiscount());
        assertEquals(Money.of("500.20"), sale.discountAmount());
        assertEquals(Money.of("2000.80"), sale.total());
        sale.setDiscount(new FixedDiscount(Money.of("1.00")));  // swap it: same Sale, different Discount object
        assertEquals(Money.of("2500.00"), sale.total());
    }

    @Test
    void aSaleKnowsWhenItHappenedAndCanPrintItOnAReceiptHeader() {
        Sale sale = new Sale(7, new Customer("Ana", ""), LocalDateTime.of(2026, 3, 1, 9, 30));
        assertEquals(LocalDateTime.of(2026, 3, 1, 9, 30), sale.getDateTime());
        assertEquals("Sale #7 - 01 Mar 2026, 9:30 AM", sale.receiptHeader());
        assertEquals("Sale #8 - 15 Dec 2026, 5:05 PM", new Sale(8, Customer.WALK_IN, LocalDateTime.of(2026, 12, 15, 17, 5)).receiptHeader());
    }

    @Test
    void getLinesIsACopy() {
        Sale sale = new Sale(1, new Customer("Ana", ""), NOON);
        sale.addLine(new SaleLine("Sugar 1kg", Money.of("75.00"), 1));
        List<SaleLine> copy = sale.getLines();
        sale.addLine(new SaleLine("Salt", Money.of("35.00"), 1));
        assertEquals(1, copy.size());
        assertEquals(2, sale.getLines().size());
    }
}
