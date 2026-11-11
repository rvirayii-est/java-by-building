// Run with:  java BugHuntDemo.java
// The debugging method in action: 1. reproduce  2. find the first wrong value  3. form a guess  4. check it  5. fix  6. keep a test.
import java.util.List;

public class BugHuntDemo {

    // BUG: integer division. total and items.size() are both int, so 7 / 2 is 3, not 3.5.
    static double buggyAverage(List<Integer> prices) {
        int total = 0;
        for (int price : prices) {
            total += price;
        }
        return total / prices.size();
    }

    static double fixedAverage(List<Integer> prices) {
        int total = 0;
        for (int price : prices) {
            total += price;
        }
        return (double) total / prices.size();
    }

    // BUG: off by one. The loop never reaches the last item.
    static int buggyCountOver(List<Integer> prices, int limit) {
        int count = 0;
        for (int i = 0; i < prices.size() - 1; i++) {
            if (prices.get(i) > limit) {
                count++;
            }
        }
        return count;
    }

    static int fixedCountOver(List<Integer> prices, int limit) {
        int count = 0;
        for (int i = 0; i < prices.size(); i++) {
            if (prices.get(i) > limit) {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        List<Integer> prices = List.of(3, 4);

        // Step 1: reproduce. The smallest input that shows the problem: two prices.
        System.out.println("Buggy average of " + prices + ": " + buggyAverage(prices) + " (expected 3.5)");

        // Step 2: find the first wrong value. Print what each step computes ("print debugging").
        int total = prices.get(0) + prices.get(1);
        System.out.println("  total=" + total + " size=" + prices.size() + " total/size=" + (total / prices.size()) + " <- already 3, not 3.5");

        // Step 3-5: the guess is "int divided by int drops the decimals"; the fix casts one side to double first.
        System.out.println("Fixed average: " + fixedAverage(prices));

        // A second bug. Expected: two prices are over 100.
        List<Integer> more = List.of(50, 150, 120);
        System.out.println("Buggy count over 100: " + buggyCountOver(more, 100) + " (expected 2)");
        // Step 1 again, smaller: does it work when the LAST item is the one that matters?
        System.out.println("  last item only: " + buggyCountOver(List.of(5, 500), 100) + " (expected 1) <- the last item is skipped");
        System.out.println("Fixed count over 100: " + fixedCountOver(more, 100));

        // Step 6: keep a test, so the bug can never come back silently.
        check(fixedAverage(List.of(3, 4)) == 3.5, "average keeps decimals");
        check(fixedCountOver(List.of(5, 500), 100) == 1, "last item is counted");
    }

    static void check(boolean ok, String name) {
        System.out.println((ok ? "PASS " : "FAIL ") + name);
    }
}
