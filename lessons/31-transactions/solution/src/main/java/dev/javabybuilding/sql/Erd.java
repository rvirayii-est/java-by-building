package dev.javabybuilding.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Draws the Entity-Relationship Diagram (ERD) of a database by ASKING the database what it contains.
 * A diagram you draw by hand goes out of date; one produced from the real tables cannot lie.
 * The output is Mermaid text, which GitHub shows as a picture.
 */
public final class Erd {

    private Erd() {}

    /**
     * One relationship: every row of the child table points at one row of the parent table through a foreign key.
     * optional is true when the foreign key column may be NULL (a sale may have no customer).
     */
    public record Relationship(String parent, String child, String column, boolean optional) {}

    /** The names of all tables, in A-Z order, in lower case. */
    public static List<String> tables(Connection connection) throws SQLException {
        List<String> names = new ArrayList<>();
        try (ResultSet rs = connection.getMetaData().getTables(null, "PUBLIC", "%", new String[] {"BASE TABLE"})) {
            while (rs.next()) {
                names.add(rs.getString("TABLE_NAME").toLowerCase());
            }
        }
        names.sort(Comparator.naturalOrder());
        return names;
    }

    /** Every foreign key in the database as a Relationship, sorted by parent, then child, then column. */
    public static List<Relationship> relationships(Connection connection) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        List<Relationship> found = new ArrayList<>();
        for (String child : tables(connection)) {
            // getImportedKeys: "which other tables does this table point at?"
            try (ResultSet keys = meta.getImportedKeys(null, "PUBLIC", child.toUpperCase())) {
                while (keys.next()) {
                    String column = keys.getString("FKCOLUMN_NAME");
                    found.add(new Relationship(keys.getString("PKTABLE_NAME").toLowerCase(), child,
                            column.toLowerCase(), isNullable(meta, child, column)));
                }
            }
        }
        found.sort(Comparator.comparing(Relationship::parent)
                .thenComparing(Relationship::child)
                .thenComparing(Relationship::column));
        return found;
    }

    private static boolean isNullable(DatabaseMetaData meta, String table, String column) throws SQLException {
        try (ResultSet rs = meta.getColumns(null, "PUBLIC", table.toUpperCase(), column)) {
            return rs.next() && rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
        }
    }

    /**
     * The whole diagram as Mermaid text: first the relationships, then each table with its columns.
     * Crow's-foot symbols: || exactly one, |o zero or one, o{ zero or many.
     * The text after the colon is the foreign key column that creates the link.
     */
    public static String mermaid(Connection connection) throws SQLException {
        StringBuilder text = new StringBuilder("erDiagram");
        for (Relationship r : relationships(connection)) {
            text.append("\n    ").append(r.parent()).append(r.optional() ? " |o--o{ " : " ||--o{ ")
                    .append(r.child()).append(" : ").append(r.column());
        }
        DatabaseMetaData meta = connection.getMetaData();
        for (String table : tables(connection)) {
            Set<String> primary = columnNames(meta.getPrimaryKeys(null, "PUBLIC", table.toUpperCase()), "COLUMN_NAME");
            Set<String> foreign = new HashSet<>();
            for (Relationship r : relationships(connection)) {
                if (r.child().equals(table)) {
                    foreign.add(r.column());
                }
            }
            text.append("\n    ").append(table).append(" {");
            try (ResultSet columns = meta.getColumns(null, "PUBLIC", table.toUpperCase(), "%")) {
                while (columns.next()) {
                    String name = columns.getString("COLUMN_NAME").toLowerCase();
                    text.append("\n        ").append(shortType(columns.getString("TYPE_NAME"))).append(' ').append(name);
                    if (primary.contains(name)) {
                        text.append(" PK");
                    } else if (foreign.contains(name)) {
                        text.append(" FK");
                    }
                }
            }
            text.append("\n    }");
        }
        return text.toString();
    }

    private static Set<String> columnNames(ResultSet rs, String label) throws SQLException {
        Set<String> names = new HashSet<>();
        try (rs) {
            while (rs.next()) {
                names.add(rs.getString(label).toLowerCase());
            }
        }
        return names;
    }

    /** Mermaid wants one word for a type: "CHARACTER VARYING" becomes "string". */
    static String shortType(String h2TypeName) {
        return switch (h2TypeName) {
            case "INTEGER" -> "int";
            case "CHARACTER VARYING", "CHARACTER" -> "string";
            case "DECIMAL", "NUMERIC" -> "decimal";
            case "TIMESTAMP" -> "timestamp";
            default -> h2TypeName.toLowerCase().replace(' ', '_');
        };
    }
}
