// Run with:  java StackTraceDemo.java
// A stack trace is the list of method calls that were "open" when the problem happened, newest first.
// Real stack traces also list Java's own code; to keep this output short and repeatable we print only OUR frames.
public class StackTraceDemo {

    static String nickname; // never set, so it is null

    static int parseQuantity(String text) {
        return Integer.parseInt(text);              // the problem happens here...
    }

    static int totalFor(String[] quantities) {
        int total = 0;
        for (String quantity : quantities) {
            total += parseQuantity(quantity);       // ...called from here...
        }
        return total;
    }

    static void printReceipt(String[] quantities) {
        System.out.println("Total: " + totalFor(quantities));   // ...called from here...
    }

    static void printOurFrames(Throwable problem) {
        for (StackTraceElement frame : problem.getStackTrace()) {
            if (frame.getClassName().equals("StackTraceDemo")) {
                System.out.println("  at " + frame.getMethodName() + " (line " + frame.getLineNumber() + ")");
            }
        }
    }

    static void loadSettings() {
        try {
            Integer.parseInt("12.5");
        } catch (NumberFormatException e) {
            // Wrapping keeps the original problem as the CAUSE, so nothing is lost.
            throw new IllegalStateException("Could not load the settings", e);
        }
    }

    public static void main(String[] args) {
        // 1. Read it from the TOP: what happened, then the first line that is YOUR code.
        try {
            printReceipt(new String[] {"2", "x3"});
        } catch (NumberFormatException e) {
            System.out.println("1. " + e.getClass().getSimpleName() + ": " + e.getMessage());
            printOurFrames(e);
        }

        // 2. "Caused by": the exception inside the exception is usually the real story.
        try {
            loadSettings();
        } catch (IllegalStateException e) {
            System.out.println("2. " + e.getMessage());
            System.out.println("   caused by " + e.getCause().getClass().getSimpleName() + ": " + e.getCause().getMessage());
        }

        // 3. NullPointerException: modern Java says exactly what was null.
        try {
            System.out.println(nickname.length());
        } catch (NullPointerException e) {
            System.out.println("3. " + e.getMessage());
        }

        // 4. IndexOutOfBoundsException says the index and the length.
        try {
            java.util.List.of("a", "b", "c").get(3);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("4. " + e.getMessage());
        }

        // 5. ArithmeticException is just as direct.
        try {
            System.out.println(10 / (args.length));
        } catch (ArithmeticException e) {
            System.out.println("5. " + e.getMessage());
        }
    }
}
