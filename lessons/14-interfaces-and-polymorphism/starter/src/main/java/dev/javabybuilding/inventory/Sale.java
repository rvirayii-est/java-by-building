package dev.javabybuilding.inventory;

import java.util.ArrayList;
import java.util.List;

/** One sale (a receipt): a number, the customer, a list of lines and a discount. A Sale holds other objects. */
public class Sale {

    private final int number;
    private final Customer customer;
    private final ArrayList<SaleLine> lines = new ArrayList<>();
    private Discount discount = new NoDiscount(); // the type is the interface: any kind of Discount fits

    public Sale(int number, Customer customer) {
        this.number = number;
        this.customer = customer;
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

    /** TODO 5: the lines added up, before any discount, in centavos (this is what total() did in lesson 13). */
    public int subtotal() {
        return -1;
    }

    /** TODO 5: how much the discount takes off the subtotal: discount.discountFor(subtotal()). Sale does not know which kind of Discount it is. */
    public int discountAmount() {
        return -1;
    }

    /** TODO 5: what the customer pays: subtotal minus discountAmount. */
    public int total() {
        return -1;
    }
}
