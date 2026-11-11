package dev.javabybuilding.exercises;

/** Lesson 17, exercise 1: two values that belong together, each of its own type. A generic record. */
public record Pair<A, B>(A first, B second) {

    /**
     * TODO: return a new pair with the two values the other way round.
     * Pair<String, Integer> becomes Pair<Integer, String>, so the return type is Pair<B, A>.
     */
    public Pair<B, A> swap() {
        return null;
    }
}
