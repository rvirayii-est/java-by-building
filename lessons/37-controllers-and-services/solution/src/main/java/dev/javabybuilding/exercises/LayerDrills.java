package dev.javabybuilding.exercises;

import java.math.BigDecimal;
import java.util.Comparator;
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
        if (page < 1 || size < 1) {
            throw new IllegalArgumentException("page and size must be 1 or more");
        }
        int from = (page - 1) * size;
        if (from >= items.size()) {
            return List.of();
        }
        return items.subList(from, Math.min(from + size, items.size()));
    }

    /** How many pages hold that many items: totalPages(25, 10) is 3, totalPages(0, 10) is 0, totalPages(30, 10) is 3. */
    public static int totalPages(int items, int size) {
        return (items + size - 1) / size;
    }

    /**
     * Reads a sort text such as "price,desc": the field, then "asc" or "desc" (any capitals). Without a direction the sort is
     * ascending: "name" is name ascending.
     *
     * @throws IllegalArgumentException for a blank text, an empty field or a direction that is neither asc nor desc
     */
    public static Sort parseSort(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("A sort needs a field");
        }
        String[] pieces = text.split(",");
        String field = pieces[0].trim();
        if (field.isEmpty() || pieces.length > 2) {
            throw new IllegalArgumentException("Not a sort: " + text);
        }
        if (pieces.length == 1) {
            return new Sort(field, true);
        }
        String direction = pieces[1].trim().toLowerCase();
        if (!direction.equals("asc") && !direction.equals("desc")) {
            throw new IllegalArgumentException("Direction must be asc or desc, not " + pieces[1].trim());
        }
        return new Sort(field, direction.equals("asc"));
    }

    /**
     * The rows in the order of the sort text. The field is name (compared ignoring capitals), price or quantity. A row that ties
     * keeps its place (sorted() on a stream is stable).
     *
     * @throws IllegalArgumentException for a field that is not name, price or quantity
     */
    public static List<Row> sortRows(List<Row> rows, String sortText) {
        Sort sort = parseSort(sortText);
        Comparator<Row> comparator = switch (sort.field().toLowerCase()) {
            case "name" -> Comparator.comparing(Row::name, String.CASE_INSENSITIVE_ORDER);
            case "price" -> Comparator.comparing(Row::price);
            case "quantity" -> Comparator.comparingInt(Row::quantity);
            default -> throw new IllegalArgumentException("Cannot sort by " + sort.field());
        };
        return rows.stream().sorted(sort.ascending() ? comparator : comparator.reversed()).toList();
    }

    /**
     * Which layer does a class belong to, judging by its name? A name ending in Controller is the "controller" layer, Service the
     * "service" layer, Repository the "repository" layer, Response or Request a "dto"; anything else is "domain".
     * layerOf("ProductController") is "controller"; layerOf("Product") is "domain".
     */
    public static String layerOf(String className) {
        if (className.endsWith("Controller")) {
            return "controller";
        } else if (className.endsWith("Service")) {
            return "service";
        } else if (className.endsWith("Repository")) {
            return "repository";
        } else if (className.endsWith("Response") || className.endsWith("Request")) {
            return "dto";
        }
        return "domain";
    }
}
