package dev.javabybuilding.exercises;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lesson 38 drills: the rules an ORM follows, written by hand. Complete each TODO until Exercises38Test is green.
 */
public class MappingDrills {

    /**
     * The naming rule Spring Boot uses between Java and the database: camelCase becomes snake_case.
     * snake("reorderLevel") is "reorder_level", snake("soldAt") is "sold_at", snake("SaleLine") is "sale_line", snake("name") is "name".
     */
    public static String snake(String camelCase) {
        StringBuilder result = new StringBuilder();
        for (char c : camelCase.toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (result.length() > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /** The SQL an ORM writes to read some columns: selectSql("product", List.of("name", "price")) is "SELECT name, price FROM product". */
    public static String selectSql(String table, List<String> columns) {
        requireColumns(columns);
        return "SELECT " + String.join(", ", columns) + " FROM " + table;
    }

    /** The SQL an ORM writes to store a row: insertSql("product", List.of("name", "price")) is "INSERT INTO product (name, price) VALUES (?, ?)". */
    public static String insertSql(String table, List<String> columns) {
        requireColumns(columns);
        return "INSERT INTO " + table + " (" + String.join(", ", columns) + ") VALUES (" + String.join(", ", Collections.nCopies(columns.size(), "?")) + ")";
    }

    private static void requireColumns(List<String> columns) {
        if (columns.isEmpty()) {
            throw new IllegalArgumentException("At least one column is needed");
        }
    }

    /**
     * What Spring Data does with the NAME of a repository method. The method name starts with findBy, then one or more fields
     * joined with And. A field is written with a capital: Name means the column name, ReorderLevel means reorder_level (use snake,
     * with the first letter lower case). A field may end with IgnoreCase.
     * <pre>
     * findByName                -> WHERE name = ?
     * findByNameAndPhone        -> WHERE name = ? AND phone = ?
     * findByReorderLevel        -> WHERE reorder_level = ?
     * findByNameIgnoreCase      -> WHERE UPPER(name) = UPPER(?)
     * </pre>
     *
     * @throws IllegalArgumentException when the name does not start with findBy or has no field
     */
    public static String derivedWhere(String methodName) {
        if (!methodName.startsWith("findBy") || methodName.length() == "findBy".length()) {
            throw new IllegalArgumentException("Not a findBy method: " + methodName);
        }
        List<String> tests = new ArrayList<>();
        for (String field : methodName.substring("findBy".length()).split("And")) {
            boolean ignoreCase = field.endsWith("IgnoreCase");
            String name = ignoreCase ? field.substring(0, field.length() - "IgnoreCase".length()) : field;
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Not a findBy method: " + methodName);
            }
            String column = snake(Character.toLowerCase(name.charAt(0)) + name.substring(1));
            tests.add(ignoreCase ? "UPPER(" + column + ") = UPPER(?)" : column + " = ?");
        }
        return "WHERE " + String.join(" AND ", tests);
    }

    /**
     * Which table does an entity class name belong to? The class name without the word Entity, as snake case:
     * tableFor("SaleLineEntity") is "sale_line"; a class without the suffix, tableFor("Customer"), is "customer".
     */
    public static String tableFor(String entityClassName) {
        String name = entityClassName.endsWith("Entity") ? entityClassName.substring(0, entityClassName.length() - "Entity".length()) : entityClassName;
        return snake(name);
    }
}
