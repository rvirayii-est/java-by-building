package dev.javabybuilding.exercises;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Lesson 18, exercises: answer each question with a stream pipeline: list.stream() ... .toList() (or another ending).
 * Do not use for loops here; the point is to practise filter, map, sorted, collect and friends.
 */
public class StreamDrills {

    /** TODO: the words with at least minLength letters, in capitals, in the same order. Hint: filter, then map(String::toUpperCase), then toList(). */
    public static List<String> shout(List<String> words, int minLength) {
        return null;
    }

    /** TODO: the odd numbers, each squared, added together: [1, 2, 3, 4] gives 1 + 9 = 10. Hint: filter, mapToInt, sum. */
    public static int sumOfSquaresOfOdds(List<Integer> numbers) {
        return -1;
    }

    /** TODO: words grouped by length: ["to", "in", "sun"] gives {2=[to, in], 3=[sun]}. Hint: Collectors.groupingBy(String::length). */
    public static Map<Integer, List<String>> groupByLength(List<String> words) {
        return null;
    }

    /** TODO: the longest word (the first one when tied), or Optional.empty() for an empty list. Hint: max(Comparator.comparingInt(...)). */
    public static Optional<String> longestWord(List<String> words) {
        return null;
    }

    /** TODO: the words without repeats, A to Z, joined with "|": ["b", "a", "b"] gives "a|b". Hint: distinct, sorted, Collectors.joining("|"). */
    public static String joinSorted(List<String> words) {
        return null;
    }

    /** TODO: the average number of letters per word, or 0 when there are no words. Hint: mapToInt, average, orElse(0). */
    public static double averageLength(List<String> words) {
        return -1;
    }
}
