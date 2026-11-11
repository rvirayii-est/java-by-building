package dev.javabybuilding.inventory;

/**
 * A shop customer who earns loyalty points: 1 point for every full PHP 100 spent. Money is whole centavos.
 * Customer has static members (they belong to the class: Customer.WALK_IN) and instance members (they belong to
 * one customer: ana.getPoints()).
 */
public class Customer {

    /** Centavos that earn one point: PHP 100. A static constant, shared by every customer. */
    public static final int CENTAVOS_PER_POINT = 10000;

    /**
     * The one shared "anonymous customer" for people who do not give a name. It never earns points.
     * shortcut: a shared object is only safe because addPurchase ignores it; never store changing data in a
     * static object.
     */
    public static final Customer WALK_IN = new Customer("Walk-in", "");

    private final String name;
    private final String phone;
    private int points;

    public Customer(String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.points = 0;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public int getPoints() {
        return points;
    }

    /** True for the shared walk-in customer. == asks "is it the very same object?". */
    public boolean isWalkIn() {
        return this == WALK_IN;
    }

    /**
     * How many points a purchase of totalCentavos earns: 1 per full PHP 100.
     * static: it uses no field of any customer, only its parameter, so it can be called as Customer.pointsFor(...).
     */
    public static int pointsFor(int totalCentavos) {
        return totalCentavos / CENTAVOS_PER_POINT;
    }

    /** Records a purchase: adds the points it earns. The walk-in customer earns nothing. */
    public void addPurchase(int totalCentavos) {
        if (isWalkIn()) {
            return;
        }
        points += pointsFor(totalCentavos);
    }

    /** For example "Ana Reyes (12 points)". */
    @Override
    public String toString() {
        return name + " (" + points + " points)";
    }
}
