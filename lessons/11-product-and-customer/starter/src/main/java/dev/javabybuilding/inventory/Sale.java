package dev.javabybuilding.inventory;

import java.util.ArrayList;
import java.util.List;

/** One sale (a receipt): a number, the customer, and a list of lines. A Sale holds other objects. */
public class Sale {

    private final int number;
    private final Customer customer;
    private final ArrayList<SaleLine> lines = new ArrayList<>();

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

    /** TODO 2: add the line to the list. */
    public void addLine(SaleLine line) {
    }

    /** TODO 2: how many lines the sale has. */
    public int lineCount() {
        return -1;
    }

    /** A copy of the lines, in the order they were added. (Done for you.) */
    public List<SaleLine> getLines() {
        return List.copyOf(lines);
    }

    /** TODO 2: the whole sale in centavos: loop over the lines and add up line.lineTotal(). */
    public int total() {
        return -1;
    }
}
