// Run with:  java Decisions.java
public class Decisions {
    public static void main(String[] args) {
        int[] quantities = {12, 4, 0, 60};
        int reorderLevel = 10;

        for (int i = 0; i < quantities.length; i++) {
            String status;
            if (quantities[i] <= 0) {
                status = "OUT";
            } else if (quantities[i] <= reorderLevel) {
                status = "LOW";
            } else {
                status = "OK";
            }
            System.out.println("Item " + i + ": " + quantities[i] + " -> " + status);
        }
    }
}
