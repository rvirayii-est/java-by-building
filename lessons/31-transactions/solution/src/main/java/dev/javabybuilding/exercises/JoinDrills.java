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
        return "SELECT c.name AS customer, s.id AS sale_id FROM sale s JOIN customer c ON c.id = s.customer_id ORDER BY s.id";
    }

    /** Two columns, sale_id and customer: every sale, with the customer's name or the text Walk-in when there is none. (LEFT JOIN) */
    public static String allSalesWithWalkIns() {
        return "SELECT s.id AS sale_id, COALESCE(c.name, 'Walk-in') AS customer "
                + "FROM sale s LEFT JOIN customer c ON c.id = s.customer_id ORDER BY s.id";
    }

    /** Two columns, product and units: the units sold of each product that has sold, the most units first. (JOIN + GROUP BY) */
    public static String unitsPerProduct() {
        return "SELECT p.name AS product, SUM(l.quantity) AS units FROM sale_line l JOIN product p ON p.id = l.product_id "
                + "GROUP BY p.name ORDER BY units DESC, p.name";
    }

    /** One column, product: the names of the products that no sale line mentions, A to Z. (LEFT JOIN ... IS NULL) */
    public static String productsNeverSold() {
        return "SELECT p.name AS product FROM product p LEFT JOIN sale_line l ON l.product_id = p.id WHERE l.id IS NULL ORDER BY p.name";
    }

    /**
     * Two columns, customer and spent: the customers whose purchases add up to 1000 pesos or more
     * (unit_price x quantity over all their sale lines), the biggest spender first. (3 tables + GROUP BY + HAVING)
     */
    public static String bigSpenders() {
        return "SELECT c.name AS customer, SUM(l.unit_price * l.quantity) AS spent "
                + "FROM customer c JOIN sale s ON s.customer_id = c.id JOIN sale_line l ON l.sale_id = s.id "
                + "GROUP BY c.name HAVING SUM(l.unit_price * l.quantity) >= 1000 ORDER BY spent DESC";
    }

    /** Two columns, sale_day and sales: how many sales were made on each calendar day, oldest day first. (CAST(sold_at AS DATE)) */
    public static String salesPerDay() {
        return "SELECT CAST(sold_at AS DATE) AS sale_day, COUNT(*) AS sales FROM sale GROUP BY CAST(sold_at AS DATE) ORDER BY sale_day";
    }
}
