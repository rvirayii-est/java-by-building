package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Exercises13Test {

    @Test
    void anEmployeeIsPaidTheBasePay() {
        Employee ana = new Employee("Ana", 1500000);
        assertEquals("Ana", ana.getName());
        assertEquals(1500000, ana.monthlyPay());
    }

    @Test
    void anEmployeeDescribesThePay() {
        assertEquals("Ana earns 1500000 a month", new Employee("Ana", 1500000).describe());
    }

    @Test
    void aManagerGetsTheBonusOnTop() {
        Manager joy = new Manager("Joy", 2000000, 300000);
        assertEquals(2300000, joy.monthlyPay());
    }

    @Test
    void aManagerKeepsTheInheritedName() {
        assertEquals("Joy", new Manager("Joy", 2000000, 300000).getName());
    }

    @Test
    void inheritedMethodsUseTheOverriddenPay() {
        Manager joy = new Manager("Joy", 2000000, 300000);
        assertEquals("Joy earns 2300000 a month", joy.describe());
        assertEquals(2300000 * 12, joy.annualPay());
    }

    @Test
    void aManagerCanBeHeldInAnEmployeeVariable() {
        Employee someone = new Manager("Joy", 1000000, 100000);
        assertEquals(1100000, someone.monthlyPay()); // the Manager version runs, even through an Employee variable
    }
}
