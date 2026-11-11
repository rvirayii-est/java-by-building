package dev.javabybuilding.inventory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** One sale (a receipt): a number, the customer, a list of lines, a discount and a payment method. */
public class Sale {

    private static final DateTimeFormatter RECEIPT_TIME = DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a", Locale.US);

    private final int number;
    private final LocalDateTime at;
    private final Customer customer;
    private final ArrayList<SaleLine> lines = new ArrayList<>();
    private Discount discount = new NoDiscount(); // the type is the interface: any kind of Discount fits
    private PaymentMethod payment = PaymentMethod.CASH;

    /** at is when the sale happened, as the shop's local date and time (see Inventory, which reads its Clock). */
    public Sale(int number, Customer customer, LocalDateTime at) {
        this.number = number;
        this.customer = customer;
        this.at = at;
    }

    /**
     * A copy of this sale with another number. A database numbers new rows itself, so a repository takes a draft
     * (number 0) and returns the stored sale with its real number. Customer, time, lines, discount and payment are kept.
     */
    public Sale withNumber(int newNumber) {
        Sale copy = new Sale(newNumber, customer, at);
        lines.forEach(copy::addLine);
        copy.setDiscount(discount);
        copy.setPayment(payment);
        return copy;
    }

    public LocalDateTime getDateTime() {
        return at;
    }

    /** For example "Sale #7 - 01 Mar 2026, 9:30 AM". DateTimeFormatter turns a date and time into text, with a pattern. */
    public String receiptHeader() {
        return "Sale #" + number + " - " + RECEIPT_TIME.format(at);
    }

    public int getNumber() {
        return number;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public PaymentMethod getPayment() {
        return payment;
    }

    public void setPayment(PaymentMethod payment) {
        this.payment = payment;
    }

    public void addLine(SaleLine line) {
        lines.add(line);
    }

    public int lineCount() {
        return lines.size();
    }

    /** A copy of the lines, in the order they were added. */
    public List<SaleLine> getLines() {
        return List.copyOf(lines);
    }

    /** The lines added up, before any discount, in pesos. */
    public BigDecimal subtotal() {
        return lines.stream().map(SaleLine::lineTotal).reduce(Money.ZERO, BigDecimal::add);
    }

    /** How much the discount takes off the subtotal. Sale does not know which kind of Discount it is. */
    public BigDecimal discountAmount() {
        return discount.discountFor(subtotal());
    }

    /** What the customer pays: subtotal minus discount. (Before lesson 14 this was just the lines added up.) */
    public BigDecimal total() {
        return subtotal().subtract(discountAmount()); // BigDecimal has no minus sign: a.subtract(b)
    }
}
