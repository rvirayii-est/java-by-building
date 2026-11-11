package dev.javabybuilding.exercises;

/** Lesson 13, exercise 1: an employee paid a fixed amount every month (in centavos). */
public class Employee {

    private String name;
    private int basePay;

    /** TODO: store both values in the fields. */
    public Employee(String name, int basePay) {
    }

    /** TODO: return the name. */
    public String getName() {
        return null;
    }

    /** TODO: the pay for one month: the base pay. Subclasses may override this. */
    public int monthlyPay() {
        return 0;
    }

    /** TODO: twelve months of monthlyPay(). Call monthlyPay() (not the field) so a subclass's version is used. */
    public int annualPay() {
        return 0;
    }

    /** TODO: for example "Ana earns 1500000 a month" (use monthlyPay()). */
    public String describe() {
        return null;
    }
}
