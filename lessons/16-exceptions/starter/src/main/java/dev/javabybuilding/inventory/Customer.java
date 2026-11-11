package dev.javabybuilding.inventory;

/**
 * A shop customer who earns loyalty points: 1 point for every full PHP 100 spent. Money is whole centavos.
 * A Customer IS-A Person: name and phone now live in Person (lesson 12 had them here), and Customer adds the points.
 */
public class Customer extends Person {

    /** Centavos that earn one point: PHP 100. A static constant, shared by every customer. */
    public static final int CENTAVOS_PER_POINT = 10000;

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

    /** How many points a purchase of totalCentavos earns: 1 per full PHP 100. */
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

    /** For example "Ana Reyes (12 points)". Printing a Customer uses this, because Person.toString calls describe(). */
    @Override
    public String describe() {
        return getName() + " (" + points + " points)";
    }
}
