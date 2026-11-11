// Run with:  java EnumRecordDemo.java
public class EnumRecordDemo {

    enum Level { LOW, OK, HIGH }                       // a fixed list of names

    record Point(int x, int y) {                        // a data class in one line
        int total() {
            return x + y;
        }
    }

    public static void main(String[] args) {
        Level level = Level.OK;
        System.out.println(level);                      // OK
        System.out.println(level.ordinal());            // 1: the position in the list
        for (Level each : Level.values()) {
            System.out.println(each + " -> " + advice(each));
        }
        System.out.println(Level.valueOf("HIGH"));      // find a constant by its exact name

        Point a = new Point(2, 3);
        Point b = new Point(2, 3);
        System.out.println(a);                          // toString for free
        System.out.println(a.x() + " " + a.total());    // accessors are x() and y(), no "get"
        System.out.println(a == b);                     // false: two different objects
        System.out.println(a.equals(b));                // true: records compare their values
    }

    static String advice(Level level) {
        return switch (level) {                         // the compiler checks every constant is covered
            case LOW -> "reorder";
            case OK -> "fine";
            case HIGH -> "pause orders";
        };
    }
}
