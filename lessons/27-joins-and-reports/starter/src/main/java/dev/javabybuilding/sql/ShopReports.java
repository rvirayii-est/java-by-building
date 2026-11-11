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
        return null; // TODO 1: write the SQL (see the description above)
    }

    /**
     * One row per sale: id, customer (the name, or Walk-in when the sale has no customer), payment and subtotal
     * (all its lines added up), oldest sale first. LEFT JOIN keeps the walk-in sales, which have no customer row.
     */
    public static String salesList() {
        return null; // TODO 2: write the SQL (see the description above)
    }

    /**
     * One row per product that has been sold: product, units (all quantities added up) and revenue (unit price x
     * quantity added up), the biggest revenue first.
     */
    public static String revenueByProduct() {
        return null; // TODO 3: write the SQL (see the description above)
    }

    /** The names of the n products with the most units sold, best first. One column: product. */
    public static String bestSellers(int n) {
        return null; // TODO 4: write the SQL (see the description above)
    }

    /** The names of the products that appear in no sale line at all, A to Z. One column: product. */
    public static String neverSold() {
        return null; // TODO 5: write the SQL (see the description above)
    }

    /** The names of the customers who have made no sale, A to Z. One column: customer. */
    public static String customersWithoutPurchases() {
        return null; // TODO 6: write the SQL (see the description above)
    }

    /** One row per day with sales: sale_day, sales (how many) and revenue (the lines added up), oldest day first. */
    public static String dailySales() {
        return null; // TODO 7: write the SQL (see the description above)
    }

    /**
     * The health check of Inventory.audit(), in SQL: the products whose shelf quantity differs from RECEIVED minus
     * SOLD in the stock log. Columns: product, shelf, logged. The result must be EMPTY.
     */
    public static String stockAudit() {
        return null; // TODO 8: write the SQL (see the description above)
    }
}
