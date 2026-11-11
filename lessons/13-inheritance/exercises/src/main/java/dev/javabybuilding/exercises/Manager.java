package dev.javabybuilding.exercises;

/** Lesson 13, exercise 2: a manager is an employee who also gets a fixed monthly bonus. */
public class Manager extends Employee {

    private int bonus;

    /** TODO: pass name and basePay up with super(name, basePay); then store the bonus. */
    public Manager(String name, int basePay, int bonus) {
        super(name, 0);
    }

    /** TODO: override monthlyPay: the inherited pay (super.monthlyPay()) plus the bonus. Add @Override above the method. */
    public int monthlyPay() {
        return 0;
    }
}
