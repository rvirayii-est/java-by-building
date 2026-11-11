package dev.javabybuilding.inventory;

/** A shop customer who earns loyalty points: 1 point for every full PHP 100 spent. Money is whole centavos. */
public class Customer {

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

    /** TODO 3: how many points a purchase of totalCentavos earns: 1 per full PHP 100 (10,000 centavos). Whole-number division. */
    public int pointsFor(int totalCentavos) {
        return -1;
    }

    /** TODO 3: record a purchase: add the points it earns (use pointsFor) to the points field. */
    public void addPurchase(int totalCentavos) {
    }

    /** For example "Ana Reyes (12 points)". (Done for you.) */
    @Override
    public String toString() {
        return name + " (" + points + " points)";
    }
}
