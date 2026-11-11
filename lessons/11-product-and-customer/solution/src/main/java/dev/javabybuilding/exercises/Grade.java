package dev.javabybuilding.exercises;

/** Lesson 10, exercise 2: a quiz score from 0 to 100 that refuses impossible values. */
public class Grade {

    private int score; // starts at 0

    public int getScore() {
        return score;
    }

    /** Sets the score. Returns false (and keeps the old score) unless the new score is from 0 to 100. */
    public boolean setScore(int newScore) {
        if (newScore < 0 || newScore > 100) {
            return false;
        }
        score = newScore;
        return true;
    }

    /** True when the score is 75 or more. */
    public boolean isPassing() {
        return score >= 75;
    }
}
