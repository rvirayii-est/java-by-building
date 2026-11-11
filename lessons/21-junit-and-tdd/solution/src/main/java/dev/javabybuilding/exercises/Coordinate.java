package dev.javabybuilding.exercises;

/** Lesson 15, exercise 2: a spot on a shop floor map (aisle x, shelf y). A record: plain data that never changes. */
public record Coordinate(int x, int y) {

    /** Steps between two spots if you may only walk along aisles: |dx| + |dy|. */
    public int walkingDistanceTo(Coordinate other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }

    /** A NEW coordinate moved by dx and dy. A record never changes, so this one stays as it was. */
    public Coordinate moved(int dx, int dy) {
        return new Coordinate(x + dx, y + dy);
    }
}
