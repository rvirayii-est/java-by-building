package dev.javabybuilding.exercises;

/**
 * Lesson 27 drills: join the tables and summarise. Every method returns SQL text for these tables:
 * product(id, name, price, ...), customer(id, name, phone), sale(id, customer_id, sold_at, ...) and
 * sale_line(id, sale_id, product_id, unit_price, quantity). The tests run your text on an in-memory database.
 * Money is the raw sum of unit_price x quantity.
 */
public class JoinDrills {

    /** Two columns, customer (the name) and sale_id, one row per sale made by a known customer, by sale id. (JOIN) */
    public static String customerSales() {
        return null; // TODO 1
    }

    /** Two columns, sale_id and customer: every sale, with the customer's name or the text Walk-in when there is none. (LEFT JOIN) */
    public static String allSalesWithWalkIns() {
        return null; // TODO 2
    }

    /** Two columns, product and units: the units sold of each product that has sold, the most units first. (JOIN + GROUP BY) */
    public static String unitsPerProduct() {
        return null; // TODO 3
    }

    /** One column, product: the names of the products that no sale line mentions, A to Z. (LEFT JOIN ... IS NULL) */
    public static String productsNeverSold() {
        return null; // TODO 4
    }

    /**
     * Two columns, customer and spent: the customers whose purchases add up to 1000 pesos or more
     * (unit_price x quantity over all their sale lines), the biggest spender first. (3 tables + GROUP BY + HAVING)
     */
    public static String bigSpenders() {
        return null; // TODO 5
    }

    /** Two columns, sale_day and sales: how many sales were made on each calendar day, oldest day first. (CAST(sold_at AS DATE)) */
    public static String salesPerDay() {
        return null; // TODO 6
    }
}
