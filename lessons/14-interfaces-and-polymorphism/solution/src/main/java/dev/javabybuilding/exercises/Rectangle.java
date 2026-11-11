package dev.javabybuilding.exercises;

/** Lesson 09, exercise 1: a rectangle shelf label with a width and a height (in centimetres). */
public class Rectangle {

    public int width;
    public int height;

    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /** Width x height. */
    public int area() {
        return width * height;
    }

    /** Distance around the rectangle: 2 x (width + height). */
    public int perimeter() {
        return 2 * (width + height);
    }

    /** True when width and height are the same. */
    public boolean isSquare() {
        return width == height;
    }
}
