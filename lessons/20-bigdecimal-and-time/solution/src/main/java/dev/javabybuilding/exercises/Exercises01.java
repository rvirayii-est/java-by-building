package dev.javabybuilding.exercises;

public class Exercises01 {

    /** greet("Ana") returns "Hello, Ana!" */
    public static String greet(String name) {
        return "Hello, " + name + "!";
    }

    /** receiptHeader("Mang Tomas Store") returns "*** MANG TOMAS STORE ***" */
    public static String receiptHeader(String shopName) {
        return "*** " + shopName.toUpperCase() + " ***";
    }
}
