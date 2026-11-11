package dev.javabybuilding.exercises;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/** Lesson 18, exercises: answer each question with a stream pipeline (no for loops). */
public class StreamDrills {

    /** The words with at least minLength letters, in capitals, in the same order. */
    public static List<String> shout(List<String> words, int minLength) {
        return words.stream()
                .filter(word -> word.length() >= minLength)
                .map(String::toUpperCase)
                .toList();
    }

    /** The odd numbers, each squared, added together: [1, 2, 3, 4] gives 1 + 9 = 10. */
    public static int sumOfSquaresOfOdds(List<Integer> numbers) {
        return numbers.stream()
                .filter(n -> n % 2 != 0)
                .mapToInt(n -> n * n)
                .sum();
    }

    /** Words grouped by their length: ["to", "in", "sun"] gives {2=[to, in], 3=[sun]}. */
    public static Map<Integer, List<String>> groupByLength(List<String> words) {
        return words.stream().collect(Collectors.groupingBy(String::length));
    }

    /** The longest word (the first one when tied), or an empty Optional for an empty list. */
    public static Optional<String> longestWord(List<String> words) {
        return words.stream().max(Comparator.comparingInt(String::length));
    }

    /** The words without repeats, A to Z, joined with "|": ["b", "a", "b"] gives "a|b". */
    public static String joinSorted(List<String> words) {
        return words.stream().distinct().sorted().collect(Collectors.joining("|"));
    }

    /** The average number of letters per word, or 0 when there are no words. */
    public static double averageLength(List<String> words) {
        return words.stream().mapToInt(String::length).average().orElse(0);
    }
}
