package dev.javabybuilding.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * What a SQL statement gave back: column names and rows of text, or just "this many rows were changed".
 * Every cell is kept as text (NULL becomes the word NULL), so a result is easy to print and easy to compare in a test.
 */
public record QueryResult(List<String> columns, List<List<String>> rows, int updateCount) {

    /** A result for a SELECT: updateCount is -1 because nothing was changed. */
    public static QueryResult ofRows(List<String> columns, List<List<String>> rows) {
        return new QueryResult(List.copyOf(columns), rows.stream().map(List::copyOf).toList(), -1);
    }

    /** A result for INSERT, UPDATE, DELETE or CREATE: no rows to show, only how many rows were changed. */
    public static QueryResult ofUpdate(int updateCount) {
        return new QueryResult(List.of(), List.of(), updateCount);
    }

    public boolean isQuery() {
        return updateCount < 0;
    }

    public int rowCount() {
        return rows.size();
    }

    /** The text in one cell. Row and column numbers start at 0. */
    public String cell(int row, int column) {
        return rows.get(row).get(column);
    }

    /** Every value in one column (found by name, ignoring capitals), top to bottom. */
    public List<String> column(String name) {
        int index = -1;
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).equalsIgnoreCase(name)) {
                index = i;
            }
        }
        if (index < 0) {
            throw new IllegalArgumentException("No column called " + name + " in " + columns);
        }
        List<String> values = new ArrayList<>();
        for (List<String> row : rows) {
            values.add(row.get(index));
        }
        return values;
    }

    /**
     * The result as a small text table, in the style of the command-line tools:
     * <pre>
     * name      | quantity
     * ----------+---------
     * Rice 25kg | 17
     * (1 row)
     * </pre>
     */
    public String toTable() {
        if (!isQuery()) {
            return "(" + updateCount + (updateCount == 1 ? " row" : " rows") + " updated)";
        }
        int[] width = new int[columns.size()];
        for (int c = 0; c < width.length; c++) {
            width[c] = columns.get(c).length();
            for (List<String> row : rows) {
                width[c] = Math.max(width[c], row.get(c).length());
            }
        }
        StringBuilder table = new StringBuilder();
        table.append(line(columns, width)).append('\n');
        StringBuilder rule = new StringBuilder();
        for (int c = 0; c < width.length; c++) {
            rule.append(c == 0 ? "" : "-+-").append("-".repeat(width[c]));
        }
        table.append(rule).append('\n');
        for (List<String> row : rows) {
            table.append(line(row, width)).append('\n');
        }
        table.append('(').append(rows.size()).append(rows.size() == 1 ? " row)" : " rows)");
        return table.toString();
    }

    private static String line(List<String> cells, int[] width) {
        StringBuilder line = new StringBuilder();
        for (int c = 0; c < width.length; c++) {
            if (c > 0) {
                line.append(" | ");
            }
            line.append(cells.get(c));
            if (c < width.length - 1) {
                line.append(" ".repeat(width[c] - cells.get(c).length()));
            }
        }
        return line.toString();
    }
}
