package dev.javabybuilding.inventory;

import java.math.BigDecimal;

/**
 * A shop customer who earns loyalty points: 1 point for every full PHP 100 spent. Money is BigDecimal pesos.
 * A Customer IS-A Person: name and phone now live in Person (lesson 12 had them here), and Customer adds the points.
 */
public class Customer extends Person {

    /** Pesos that earn one point: PHP 100. A static constant, shared by every customer. */
    public static final BigDecimal PESOS_PER_POINT = new BigDecimal("100");

    /**
     * The one shared "anonymous customer" for people who do not give a name. It never earns points.
     * shortcut: a shared object is only safe because addPurchase ignores it; never store changing data in a
     * static object.
     */
    public static final Customer WALK_IN = new Customer("Walk-in", "");

    private int points;

    public Customer(String name, String phone) {
        super(name, phone); // run the Person constructor first
        this.points = 0;
    }

    public int getPoints() {
        return points;
    }

    /** True for the shared walk-in customer. == asks "is it the very same object?". */
    public boolean isWalkIn() {
        return this == WALK_IN;
    }

    /** How many points a purchase earns: 1 per full PHP 100 (the rest is dropped). */
    public static int pointsFor(BigDecimal total) {
        // TODO 3: the whole number of times PHP 100 fits in total: total.divideToIntegralValue(PESOS_PER_POINT).intValue()
        return 0;
    }

    /** Records a purchase: adds the points it earns. The walk-in customer earns nothing. */
    public void addPurchase(BigDecimal total) {
        if (isWalkIn()) {
            return;
        }
        points += pointsFor(total);
    }

    /** For example "Ana Reyes (12 points)". Printing a Customer uses this, because Person.toString calls describe(). */
    @Override
    public String describe() {
        return getName() + " (" + points + " points)";
    }
}
