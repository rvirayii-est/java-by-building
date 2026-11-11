// Run with:  java MiniTestDemo.java
// What does JUnit actually do? This file builds a tiny test framework (about 30 lines) so you can see the idea:
//   1. find the methods marked @Test   2. run each one   3. report PASS, or FAIL with a message.
// JUnit does the same, with many more features (assertions, parameters, setup, reports).
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

public class MiniTestDemo {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Test {}

    // ---- the code under test: the reorder rule from lesson 21 -----------------------------------------------
    static int suggestedQuantity(int quantity, int reorderLevel) {
        if (reorderLevel == 0 || quantity > reorderLevel) {
            return 0;
        }
        return 2 * reorderLevel - quantity;
    }

    // ---- a mini assertion: throws AssertionError when expected and actual differ ------------------------------
    static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("expected: <" + expected + "> but was: <" + actual + ">");
        }
    }

    // ---- the tests (note the style: Arrange, Act, Assert; the name says the rule) -----------------------------
    @Test
    void aShelfAboveTheLevelNeedsNothing() {
        int quantity = 12;                                   // Arrange
        int suggestion = suggestedQuantity(quantity, 10);    // Act
        assertEquals(0, suggestion);                         // Assert
    }

    @Test
    void aShelfAtTheLevelIsBroughtUpToTwiceTheLevel() {
        assertEquals(10, suggestedQuantity(10, 10));
    }

    @Test
    void anEmptyShelfOrdersTwiceTheLevel() {
        assertEquals(20, suggestedQuantity(0, 10));
    }

    @Test
    void aBrokenExpectationShowsHowAFailureReads() {
        assertEquals(15, suggestedQuantity(4, 10)); // the code says 16: this test is wrong on purpose
    }

    // ---- the runner ---------------------------------------------------------------------------------------------
    public static void main(String[] args) throws Exception {
        int passed = 0;
        int failed = 0;
        Method[] methods = MiniTestDemo.class.getDeclaredMethods();
        Arrays.sort(methods, Comparator.comparing(Method::getName)); // reflection gives no guaranteed order
        for (Method method : methods) {
            if (!method.isAnnotationPresent(Test.class)) {
                continue;
            }
            try {
                method.invoke(new MiniTestDemo()); // JUnit also makes a NEW object for every test, so tests cannot affect each other
                System.out.println("PASS  " + method.getName());
                passed++;
            } catch (InvocationTargetException e) {
                System.out.println("FAIL  " + method.getName() + " -> " + e.getCause().getMessage());
                failed++;
            }
        }
        System.out.println(passed + " passed, " + failed + " failed");
    }
}
