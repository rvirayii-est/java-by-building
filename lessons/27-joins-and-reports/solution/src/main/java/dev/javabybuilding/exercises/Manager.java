package dev.javabybuilding.exercises;

/** Lesson 13, exercise 2: a manager is an employee who also gets a fixed monthly bonus. */
public class Manager extends Employee {

    private final int bonus;

    public Manager(String name, int basePay, int bonus) {
        super(name, basePay);
        this.bonus = bonus;
    }

    /** The base pay (from Employee, reached with super) plus the bonus. */
    @Override
    public int monthlyPay() {
        return super.monthlyPay() + bonus;
    }
}
