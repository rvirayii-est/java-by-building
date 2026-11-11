package dev.javabybuilding.exercises;

public class Exercises02 {

    /** The discount amount: discount(20000, 10) returns 2000 (10% of 200.00 pesos). */
    public static int discount(int amountCentavos, int percent) {
        return amountCentavos * percent / 100;
    }

    /** Change to give back; returns -1 when the customer has not paid enough. */
    public static int changeDue(int totalCentavos, int paidCentavos) {
        if (paidCentavos < totalCentavos) {
            return -1;
        }
        return paidCentavos - totalCentavos;
    }
}
