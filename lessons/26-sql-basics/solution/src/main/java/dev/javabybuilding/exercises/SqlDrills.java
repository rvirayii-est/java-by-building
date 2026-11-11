package dev.javabybuilding.exercises;

/**
 * Lesson 26 drills: write the SQL. Every method returns SQL text for the table
 * product (id, name, price, reorder_level, quantity). The tests run your text on an in-memory database.
 * Write SQL keywords in capitals and put each clause where you can read it.
 */
public class SqlDrills {

    /** Name and price of the products that cost less than 100 pesos, the dearest of them first. */
    public static String cheapProducts() {
        return "SELECT name, price FROM product WHERE price < 100 ORDER BY price DESC";
    }

    /** The name of every product whose name contains "oil", in any capitals ("Cooking Oil 1L", "Olive OIL"...). */
    public static String namesWithOil() {
        return "SELECT name FROM product WHERE LOWER(name) LIKE '%oil%'";
    }

    /** One number named low_count: how many products are LOW (more than 0 in stock, but at or below the reorder level). */
    public static String lowCount() {
        return "SELECT COUNT(*) AS low_count FROM product WHERE quantity > 0 AND quantity <= reorder_level";
    }

    /** One number named average_price: the average price of all products. */
    public static String averagePrice() {
        return "SELECT AVG(price) AS average_price FROM product";
    }

    /** The three most expensive products: name and price, dearest first (use LIMIT). */
    public static String threeDearest() {
        return "SELECT name, price FROM product ORDER BY price DESC LIMIT 3";
    }

    /** A delivery arrived: add 10 to the quantity of Soy Sauce 500ml (change only that row). */
    public static String receiveSoySauce() {
        return "UPDATE product SET quantity = quantity + 10 WHERE name = 'Soy Sauce 500ml'";
    }

    /** Raise by 10 percent the price of every product that costs less than 50 pesos (round to 2 decimals with ROUND(x, 2)). */
    public static String raiseCheapPrices() {
        return "UPDATE product SET price = ROUND(price * 1.10, 2) WHERE price < 50";
    }

    /** Add a new product: Coffee Sachet, price 8.50, reorder level 50, quantity 0 (the id is numbered for you). */
    public static String addCoffee() {
        return "INSERT INTO product (name, price, reorder_level, quantity) VALUES ('Coffee Sachet', 8.50, 50, 0)";
    }

    /** Remove the products that are OUT of stock (quantity 0 or less). */
    public static String removeOutOfStock() {
        return "DELETE FROM product WHERE quantity <= 0";
    }
}
