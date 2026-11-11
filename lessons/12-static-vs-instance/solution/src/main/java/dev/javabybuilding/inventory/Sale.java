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

    /** The whole sale in centavos: the sum of every line total. */
    public int total() {
        int total = 0;
        for (SaleLine line : lines) {
            total += line.lineTotal();
        }
        return total;
    }
}
