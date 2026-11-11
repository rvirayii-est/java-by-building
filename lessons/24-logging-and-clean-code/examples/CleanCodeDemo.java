// Run with:  java CleanCodeDemo.java
// The same job done twice: first the way it is easy to write, then the way it is easy to READ. Both give identical results.
// Code is read many more times than it is written, usually by someone who has forgotten (you, in six months).
import java.util.List;

public class CleanCodeDemo {

    // ---------------------------------------------------------------------------------------------------------
    // BEFORE: what does it do? Why 12? Why 1000? What are a, b and c?
    static double f(int a, int b, boolean c, String d) {
        double x = a * b;
        if (c) {
            if (d.equals("S")) {
                x = x - x * 0.2;
            } else {
                if (d.equals("P")) {
                    x = x - x * 0.2;
                } else {
                    if (x > 1000) {
                        x = x - 50;
                    }
                }
            }
        }
        return x + x * 12 / 112 * 0; // (vat is included in the price, so it is not added; leftover from an old idea)
    }

    // ---------------------------------------------------------------------------------------------------------
    // AFTER: names say what things are, constants explain the numbers, small methods do one job each,
    // guard clauses replace the pyramid of ifs, and the dead code is gone.
    private static final double SENIOR_PWD_DISCOUNT = 0.20;
    private static final double COUPON_MINIMUM = 1000;
    private static final double COUPON_AMOUNT = 50;

    enum Customer { REGULAR, SENIOR, PWD }

    static double totalToPay(int unitPrice, int quantity, boolean hasDiscountId, Customer customer) {
        double subtotal = unitPrice * quantity;
        if (!hasDiscountId) {
            return subtotal;
        }
        if (isSeniorOrPwd(customer)) {
            return subtotal - subtotal * SENIOR_PWD_DISCOUNT;
        }
        return subtotal - couponFor(subtotal);
    }

    private static boolean isSeniorOrPwd(Customer customer) {
        return customer == Customer.SENIOR || customer == Customer.PWD;
    }

    private static double couponFor(double subtotal) {
        return subtotal > COUPON_MINIMUM ? COUPON_AMOUNT : 0;
    }

    public static void main(String[] args) {
        record Case(int price, int qty, boolean id, String oldCode, Customer customer) {}
        List<Case> cases = List.of(
                new Case(100, 5, false, "R", Customer.REGULAR),
                new Case(100, 5, true, "S", Customer.SENIOR),
                new Case(100, 5, true, "P", Customer.PWD),
                new Case(100, 5, true, "R", Customer.REGULAR),
                new Case(300, 5, true, "R", Customer.REGULAR));

        for (Case c : cases) {
            double before = f(c.price(), c.qty(), c.id(), c.oldCode());
            double after = totalToPay(c.price(), c.qty(), c.id(), c.customer());
            System.out.println(c.qty() + " x " + c.price() + " " + c.customer() + " id=" + c.id()
                    + " -> before " + before + ", after " + after + (before == after ? "  same" : "  DIFFERENT"));
        }
    }
}
