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
    public static final Customer WALK_IN = null; // TODO 2: replace null with new Customer("Walk-in", "")

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

    /** TODO 3: true for the shared walk-in customer. Compare with == : "is it the very same object as WALK_IN?". */
    public boolean isWalkIn() {
        return false;
    }

    /**
     * TODO 3: how many points a purchase of totalCentavos earns: 1 per full PHP 100 (divide by CENTAVOS_PER_POINT).
     * It is static because it uses no field of any customer: call it as Customer.pointsFor(...).
     * (In lesson 11 this was an instance method; it did not need to be.)
     */
    public static int pointsFor(int totalCentavos) {
        return -1;
    }

    /**
     * TODO 3: record a purchase: add the points it earns to this customer. The walk-in customer earns nothing,
     * so if isWalkIn() is true, return without adding.
     */
    public void addPurchase(int totalCentavos) {
    }

    /** For example "Ana Reyes (12 points)". */
    @Override
    public String toString() {
        return name + " (" + points + " points)";
    }
}
