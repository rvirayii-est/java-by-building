package dev.javabybuilding.exercises;

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
        // TODO 1: put an underscore before every capital letter that is not the first character, and make everything lower case.
        // A loop over the characters (Character.isUpperCase) or camelCase.replaceAll("([A-Z])", "_$1") then trim a leading "_".
        return null;
    }

    /** The SQL an ORM writes to read some columns: selectSql("product", List.of("name", "price")) is "SELECT name, price FROM product". */
    public static String selectSql(String table, List<String> columns) {
        // TODO 2: String.join(", ", columns). An empty list of columns must throw IllegalArgumentException.
        return null;
    }

    /** The SQL an ORM writes to store a row: insertSql("product", List.of("name", "price")) is "INSERT INTO product (name, price) VALUES (?, ?)". */
    public static String insertSql(String table, List<String> columns) {
        // TODO 3: one ? for every column, joined with ", ". An empty list must throw IllegalArgumentException.
        return null;
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
        // TODO 4: check the prefix "findBy"; split the rest on "And"; for each field: if it ends with "IgnoreCase" use the UPPER(...) form;
        // turn the field into a column with snake (it starts with a capital: snake("ReorderLevel") is "_reorder_level" in a naive
        // version, so lower-case the first letter first, or remove the leading underscore).
        return null;
    }

    /**
     * Which table does an entity class name belong to? The class name without the word Entity, as snake case:
     * tableFor("SaleLineEntity") is "sale_line"; a class without the suffix, tableFor("Customer"), is "customer".
     */
    public static String tableFor(String entityClassName) {
        // TODO 5: remove a trailing "Entity" (endsWith, substring) and use snake.
        return null;
    }
}
