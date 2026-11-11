package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class Exercises10Test {

    @Test
    void aNewWalletIsEmpty() {
        assertEquals(0, new Wallet().getBalance());
    }

    @Test
    void depositAndWithdrawChangeTheBalance() {
        Wallet wallet = new Wallet();
        assertTrue(wallet.deposit(50000));
        assertTrue(wallet.withdraw(12000));
        assertEquals(38000, wallet.getBalance());
    }

    @Test
    void walletRefusesBadAmounts() {
        Wallet wallet = new Wallet();
        wallet.deposit(1000);
        assertFalse(wallet.deposit(0));
        assertFalse(wallet.deposit(-5));
        assertFalse(wallet.withdraw(0));
        assertFalse(wallet.withdraw(1001));
        assertEquals(1000, wallet.getBalance());
    }

    @Test
    void walletMayBeEmptiedExactly() {
        Wallet wallet = new Wallet();
        wallet.deposit(1000);
        assertTrue(wallet.withdraw(1000));
        assertEquals(0, wallet.getBalance());
    }

    @Test
    void gradeAcceptsScoresFromZeroToOneHundred() {
        Grade grade = new Grade();
        assertTrue(grade.setScore(0));
        assertTrue(grade.setScore(100));
        assertEquals(100, grade.getScore());
    }

    @Test
    void gradeRefusesOutOfRangeScoresAndKeepsTheOldOne() {
        Grade grade = new Grade();
        grade.setScore(80);
        assertFalse(grade.setScore(-1));
        assertFalse(grade.setScore(101));
        assertEquals(80, grade.getScore());
        assertTrue(grade.isPassing());
        grade.setScore(74);
        assertFalse(grade.isPassing());
    }
}
