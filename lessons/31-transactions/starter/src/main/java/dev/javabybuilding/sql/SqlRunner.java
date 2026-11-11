package dev.javabybuilding.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * The practice harness: runs SQL text against a database and gives back the answer as a {@link QueryResult}.
 * It is deliberately tiny. Lesson 29 replaces it with real JDBC code in the app (PreparedStatement and friends).
 */
public final class SqlRunner {

    private SqlRunner() {}

    /**
     * Cuts a script into single statements at each semicolon. A semicolon inside 'quotes' does not count, and a
     * comment (from -- to the end of the line) is dropped. Blank statements are skipped.
     */
    public static List<String> split(String script) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;
        for (int i = 0; i < script.length(); i++) {
            char c = script.charAt(i);
            if (!insideQuotes && c == '-' && i + 1 < script.length() && script.charAt(i + 1) == '-') {
                while (i < script.length() && script.charAt(i) != '\n') {
                    i++; // skip the comment
                }
                current.append('\n');
                continue;
            }
            if (c == '\'') {
                insideQuotes = !insideQuotes; // two quotes in a row ('') flip twice, so they cancel out
            }
            if (c == ';' && !insideQuotes) {
                addIfNotBlank(statements, current);
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        addIfNotBlank(statements, current);
        return statements;
    }

    private static void addIfNotBlank(List<String> statements, StringBuilder statement) {
        String text = statement.toString().strip();
        if (!text.isEmpty()) {
            statements.add(text);
        }
    }

    /**
     * Runs every statement in the script, in order, and returns the answer of the LAST one: its rows when it is a
     * SELECT, or the number of rows it changed. A script with no statements gives "0 rows updated".
     */
    public static QueryResult run(Connection connection, String script) throws SQLException {
        QueryResult last = QueryResult.ofUpdate(0);
        try (Statement statement = connection.createStatement()) {
            for (String sql : split(script)) {
                if (statement.execute(sql)) {
                    try (ResultSet rows = statement.getResultSet()) {
                        last = read(rows);
                    }
                } else {
                    last = QueryResult.ofUpdate(statement.getUpdateCount());
                }
            }
        }
        return last;
    }

    /** Runs a script and throws the answer away. Handy for building tables and inserting seed data. */
    public static void execute(Connection connection, String script) throws SQLException {
        run(connection, script);
    }

    /**
     * Runs one SELECT and returns its rows.
     *
     * @throws IllegalArgumentException when the statement is not a SELECT
     */
    public static QueryResult query(Connection connection, String sql) throws SQLException {
        QueryResult result = run(connection, sql);
        if (!result.isQuery()) {
            throw new IllegalArgumentException("Not a SELECT: " + sql);
        }
        return result;
    }

    private static QueryResult read(ResultSet rows) throws SQLException {
        ResultSetMetaData meta = rows.getMetaData();
        List<String> columns = new ArrayList<>();
        for (int c = 1; c <= meta.getColumnCount(); c++) {
            columns.add(meta.getColumnLabel(c).toLowerCase()); // H2 writes names in capitals; the course writes them in lower case
        }
        List<List<String>> data = new ArrayList<>();
        while (rows.next()) {
            List<String> row = new ArrayList<>();
            for (int c = 1; c <= columns.size(); c++) {
                String value = rows.getString(c);
                row.add(value == null ? "NULL" : value);
            }
            data.add(row);
        }
        return QueryResult.ofRows(columns, data);
    }
}
