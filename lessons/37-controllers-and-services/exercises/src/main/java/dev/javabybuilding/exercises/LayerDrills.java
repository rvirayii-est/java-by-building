package dev.javabybuilding.exercises;

import java.math.BigDecimal;
import java.util.List;

/**
 * Lesson 37 drills: small jobs that belong to the SERVICE layer (plain Java, no HTTP in sight): paging, sorting, and telling
 * which layer a class belongs to. Complete each TODO until Exercises37Test is green.
 */
public class LayerDrills {

    /** One line of a product list. */
    public record Row(String name, BigDecimal price, int quantity) {}

    /** A sort order: the field and the direction. */
    public record Sort(String field, boolean ascending) {}

    /**
     * One page of a list. Pages count from 1: slice(list, 1, 10) is the first ten items. The last page may be shorter, and a
     * page after the end is an empty list.
     *
     * @throws IllegalArgumentException when page or size is below 1
     */
    public static <T> List<T> slice(List<T> items, int page, int size) {
        // TODO 1: check page and size, then items.subList(from, to) where from = (page - 1) * size and to = Math.min(from + size, items.size());
        // beware: from can be past the end, which means an empty page.
        return null;
    }

    /** How many pages hold that many items: totalPages(25, 10) is 3, totalPages(0, 10) is 0, totalPages(30, 10) is 3. */
    public static int totalPages(int items, int size) {
        // TODO 2: integer division rounds down; add size - 1 before dividing to round up.
        return -1;
    }

    /**
     * Reads a sort text such as "price,desc": the field, then "asc" or "desc" (any capitals). Without a direction the sort is
     * ascending: "name" is name ascending.
     *
     * @throws IllegalArgumentException for a blank text, an empty field or a direction that is neither asc nor desc
     */
    public static Sort parseSort(String text) {
        // TODO 3: text.split(",") ; trim the pieces ; at most two pieces.
        return null;
    }

    /**
     * The rows in the order of the sort text. The field is name (compared ignoring capitals), price or quantity. A row that ties
     * keeps its place (sorted() on a stream is stable).
     *
     * @throws IllegalArgumentException for a field that is not name, price or quantity
     */
    public static List<Row> sortRows(List<Row> rows, String sortText) {
        // TODO 4: parseSort, then pick a Comparator<Row> for the field (Comparator.comparing(Row::price) ...), call reversed() for desc.
        return null;
    }

    /**
     * Which layer does a class belong to, judging by its name? A name ending in Controller is the "controller" layer, Service the
     * "service" layer, Repository the "repository" layer, Response or Request a "dto"; anything else is "domain".
     * layerOf("ProductController") is "controller"; layerOf("Product") is "domain".
     */
    public static String layerOf(String className) {
        // TODO 5: endsWith checks.
        return null;
    }
}
