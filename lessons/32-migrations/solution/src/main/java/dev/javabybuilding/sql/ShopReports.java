package dev.javabybuilding.sql;

/**
 * The shop's reports, written as SQL that joins the tables back together. Each method returns SQL text.
 * Money here is the raw sum of unit_price x quantity: discounts are stored as short codes such as percent:10
 * (see DiscountCodes), so applying them stays the job of Java.
 *
 * <p>Two methods take an int and put it into the text. That is safe ONLY because an int cannot contain SQL.
 * Never do this with text a person typed: lesson 29 shows the right tool, the PreparedStatement.
 */
public final class ShopReports {

    private ShopReports() {}

    /**
     * The lines of one receipt: product, quantity, unit_price and line_total (unit price x quantity), in the order
     * they were added to the sale. Joins sale_line to product to get the name.
     */
    public static String receipt(int saleId) {
        return "SELECT p.name AS product, l.quantity, l.unit_price, l.unit_price * l.quantity AS line_total "
                + "FROM sale_line l JOIN product p ON p.id = l.product_id "
                + "WHERE l.sale_id = " + saleId + " ORDER BY l.id";
    }

    /**
     * One row per sale: id, customer (the name, or Walk-in when the sale has no customer), payment and subtotal
     * (all its lines added up), oldest sale first. LEFT JOIN keeps the walk-in sales, which have no customer row.
     */
    public static String salesList() {
        return "SELECT s.id, COALESCE(c.name, 'Walk-in') AS customer, s.payment, SUM(l.unit_price * l.quantity) AS subtotal "
                + "FROM sale s "
                + "LEFT JOIN customer c ON c.id = s.customer_id "
                + "JOIN sale_line l ON l.sale_id = s.id "
                + "GROUP BY s.id, c.name, s.payment "
                + "ORDER BY s.id";
    }

    /**
     * One row per product that has been sold: product, units (all quantities added up) and revenue (unit price x
     * quantity added up), the biggest revenue first.
     */
    public static String revenueByProduct() {
        return "SELECT p.name AS product, SUM(l.quantity) AS units, SUM(l.unit_price * l.quantity) AS revenue "
                + "FROM sale_line l JOIN product p ON p.id = l.product_id "
                + "GROUP BY p.name ORDER BY revenue DESC";
    }

    /** The names of the n products with the most units sold, best first. One column: product. */
    public static String bestSellers(int n) {
        return "SELECT p.name AS product FROM sale_line l JOIN product p ON p.id = l.product_id "
                + "GROUP BY p.name ORDER BY SUM(l.quantity) DESC, p.name LIMIT " + n;
    }

    /** The names of the products that appear in no sale line at all, A to Z. One column: product. */
    public static String neverSold() {
        return "SELECT p.name AS product FROM product p LEFT JOIN sale_line l ON l.product_id = p.id "
                + "WHERE l.id IS NULL ORDER BY p.name";
    }

    /** The names of the customers who have made no sale, A to Z. One column: customer. */
    public static String customersWithoutPurchases() {
        return "SELECT c.name AS customer FROM customer c LEFT JOIN sale s ON s.customer_id = c.id "
                + "WHERE s.id IS NULL ORDER BY c.name";
    }

    /** One row per day with sales: sale_day, sales (how many) and revenue (the lines added up), oldest day first. */
    public static String dailySales() {
        return "SELECT CAST(s.sold_at AS DATE) AS sale_day, COUNT(DISTINCT s.id) AS sales, SUM(l.unit_price * l.quantity) AS revenue "
                + "FROM sale s JOIN sale_line l ON l.sale_id = s.id "
                + "GROUP BY CAST(s.sold_at AS DATE) ORDER BY sale_day";
    }

    /**
     * The health check of Inventory.audit(), in SQL: the products whose shelf quantity differs from RECEIVED minus
     * SOLD in the stock log. Columns: product, shelf, logged. The result must be EMPTY.
     */
    public static String stockAudit() {
        return "SELECT p.name AS product, p.quantity AS shelf, COALESCE(m.net, 0) AS logged "
                + "FROM product p LEFT JOIN ("
                + "  SELECT product_id, SUM(CASE type WHEN 'RECEIVED' THEN quantity ELSE -quantity END) AS net "
                + "  FROM stock_movement GROUP BY product_id) m ON m.product_id = p.id "
                + "WHERE p.quantity <> COALESCE(m.net, 0) ORDER BY p.name";
    }
}
