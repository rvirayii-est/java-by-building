package dev.javabybuilding.exercises;

/** Lesson 13, exercise 1: an employee paid a fixed amount every month (in centavos). */
public class Employee {

    private final String name;
    private final int basePay;

    public Employee(String name, int basePay) {
        this.name = name;
        this.basePay = basePay;
    }

    public String getName() {
        return name;
    }

    /** The pay for one month. Subclasses may override this. */
    public int monthlyPay() {
        return basePay;
    }

    /** Twelve months of monthlyPay(). Because it calls monthlyPay(), a subclass's version is used automatically. */
    public int annualPay() {
        return monthlyPay() * 12;
    }

    /** For example "Ana earns 1500000 a month". */
    public String describe() {
        return name + " earns " + monthlyPay() + " a month";
    }
}
