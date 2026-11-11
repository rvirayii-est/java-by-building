// Run with:  java Variables.java
public class Variables {
    public static void main(String[] args) {
        String product = "Rice 25kg";
        int quantity = 2;
        int unitPriceCentavos = 125050;          // PHP 1,250.50 stored as whole centavos
        int totalCentavos = unitPriceCentavos * quantity;

        System.out.println(product + " x " + quantity + " = " + totalCentavos + " centavos");
        System.out.println("In pesos: " + String.format("%d.%02d", totalCentavos / 100, totalCentavos % 100));

        // Why we do NOT store money in double:
        System.out.println(0.1 + 0.2);           // prints 0.30000000000000004
        System.out.println(7 / 2);               // prints 3 (integer division drops the .5)
    }
}
