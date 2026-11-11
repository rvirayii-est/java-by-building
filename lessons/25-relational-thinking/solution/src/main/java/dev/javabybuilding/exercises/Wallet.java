package dev.javabybuilding.exercises;

/** Lesson 10, exercise 1: a cash drawer that can never go below zero. Money is whole centavos. */
public class Wallet {

    private int balance;

    /** The money inside, in centavos. */
    public int getBalance() {
        return balance;
    }

    /** Adds money. Returns false (and changes nothing) when amount is 0 or less. */
    public boolean deposit(int amount) {
        if (amount <= 0) {
            return false;
        }
        balance += amount;
        return true;
    }

    /** Takes money out. Returns false (and changes nothing) when amount is 0 or less or more than the balance. */
    public boolean withdraw(int amount) {
        if (amount <= 0 || amount > balance) {
            return false;
        }
        balance -= amount;
        return true;
    }
}
