package dev.javabybuilding.inventory;

import java.io.PrintStream;
import java.util.Scanner;

public class Main {

    public static String banner() {
        return "=== Inventory & Sales System ===";
    }

    /** Prints the banner, then runs the menu on the given inventory. Tests call this with a fake keyboard. */
    public static void run(Scanner in, PrintStream out, Inventory inventory) {
        out.println(banner());
        out.println("Version 0.8 - Console menu (Milestone M2)");
        Menu.run(in, out, inventory);
    }

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out, Inventory.sample());
    }
}
