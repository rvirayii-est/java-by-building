package dev.javabybuilding.exercises;

/**
 * Lesson 26 drills: write the SQL. Every method returns SQL text for the table
 * product (id, name, price, reorder_level, quantity). The tests run your text on an in-memory database.
 * Write SQL keywords in capitals. Hints: lessons README, section 2; examples/SqlTour.java has one of each kind.
 */
public class SqlDrills {

    /** Name and price of the products that cost less than 100 pesos, the dearest of them first. */
    public static String cheapProducts() {
        return null; // TODO 1
    }

    /** The name of every product whose name contains "oil", in any capitals ("Cooking Oil 1L", "Olive OIL"...). */
    public static String namesWithOil() {
        return null; // TODO 2
    }

    /** One number named low_count: how many products are LOW (more than 0 in stock, but at or below the reorder level). */
    public static String lowCount() {
        return null; // TODO 3
    }

    /** One number named average_price: the average price of all products. */
    public static String averagePrice() {
        return null; // TODO 4
    }

    /** The three most expensive products: name and price, dearest first (use LIMIT). */
    public static String threeDearest() {
        return null; // TODO 5
    }

    /** A delivery arrived: add 10 to the quantity of Soy Sauce 500ml (change only that row). */
    public static String receiveSoySauce() {
        return null; // TODO 6
    }

    /** Raise by 10 percent the price of every product that costs less than 50 pesos (round to 2 decimals with ROUND(x, 2)). */
    public static String raiseCheapPrices() {
        return null; // TODO 7
    }

    /** Add a new product: Coffee Sachet, price 8.50, reorder level 50, quantity 0 (the id is numbered for you). */
    public static String addCoffee() {
        return null; // TODO 8
    }

    /** Remove the products that are OUT of stock (quantity 0 or less). */
    public static String removeOutOfStock() {
        return null; // TODO 9
    }
}
