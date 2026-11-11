package dev.javabybuilding.exercises;

/** Lesson 15, exercise 2: a spot on a shop floor map (aisle x, shelf y). A record: plain data that never changes. */
public record Coordinate(int x, int y) {

    /** TODO: steps between two spots if you may only walk along aisles: |dx| + |dy| (Math.abs). */
    public int walkingDistanceTo(Coordinate other) {
        return -1;
    }

    /** TODO: return a NEW coordinate moved by dx and dy. A record never changes, so the old one stays as it was. */
    public Coordinate moved(int dx, int dy) {
        return null;
    }
}
