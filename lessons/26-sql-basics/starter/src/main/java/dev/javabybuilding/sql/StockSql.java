package dev.javabybuilding.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The stock report, asked of the database instead of computed in Java. Each method returns the SQL TEXT; the caller
 * decides when and where to run it. The same questions were answered in Java by StockReport, StockChecker and
 * SalesReport in lessons 03-18: the tests check that SQL and Java give the same answers.
 */
public final class StockSql {

    private StockSql() {}

    /** Every product with its price and quantity, A to Z by name. */
    public static String allProducts() {
        return null; // TODO 1: SELECT ... FROM product ORDER BY ...
    }

    /**
     * The products that are LOW or OUT (quantity at or below the reorder level): name, quantity and reorder_level,
     * the emptiest first and A to Z when the quantity is equal.
     */
    public static String lowOrOut() {
        return null; // TODO 2: WHERE quantity <= reorder_level; ORDER BY accepts several columns: ORDER BY quantity, name
    }

    /**
     * Name, quantity and status of every product in the order they were added (by id). The status is the text OUT
     * (quantity 0 or less), LOW (at or below the reorder level) or OK: the rule of StockChecker.status written as CASE.
     */
    public static String statusReport() {
        return null; // TODO 3: CASE WHEN ... THEN ... WHEN ... THEN ... ELSE ... END AS status
    }

    /** One number named total_value: the value of everything on the shelves (price x quantity, added up). */
    public static String totalStockValue() {
        return null; // TODO 4: SUM(price * quantity) AS total_value
    }

    /**
     * The stock report as text, in the same layout StockReport.build uses for Java objects. SQL decides the status;
     * Java only lines up the columns.
     */
    public static String report(Connection connection) throws SQLException {
        QueryResult result = SqlRunner.query(connection, statusReport());
        StringBuilder report = new StringBuilder(String.format("%-14s%5s  %s", "Product", "Qty", "Status"));
        for (int row = 0; row < result.rowCount(); row++) {
            report.append("\n").append(String.format("%-14s%5d  %s", result.cell(row, 0), Integer.parseInt(result.cell(row, 1)), result.cell(row, 2)));
        }
        return report.toString();
    }
}
