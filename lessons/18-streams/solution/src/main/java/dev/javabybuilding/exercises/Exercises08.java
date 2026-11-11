package dev.javabybuilding.exercises;

public class Exercises08 {

    /** One numbered line per option, joined with newlines (no newline at the end). numberedMenu({"Add", "Sell"}) is "1. Add\n2. Sell". */
    public static String numberedMenu(String[] options) {
        StringBuilder menu = new StringBuilder();
        for (int i = 0; i < options.length; i++) {
            if (i > 0) {
                menu.append("\n");
            }
            menu.append(i + 1).append(". ").append(options[i]);
        }
        return menu.toString();
    }

    /** The option for a choice number (1 = first), or "Invalid choice" when the number is not in 1..options.length. */
    public static String optionAt(String[] options, int choice) {
        if (choice < 1 || choice > options.length) {
            return "Invalid choice";
        }
        return options[choice - 1];
    }

    /** True when the text means "quit": q, quit or exit, any capitals, spaces at the ends ignored. */
    public static boolean isQuitCommand(String text) {
        String t = text.trim();
        return t.equalsIgnoreCase("q") || t.equalsIgnoreCase("quit") || t.equalsIgnoreCase("exit");
    }
}
