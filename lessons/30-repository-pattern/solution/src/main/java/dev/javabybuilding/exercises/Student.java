package dev.javabybuilding.exercises;

/** Lesson 11, exercise 1: one trainee and their score. Never changes after it is created. */
public class Student {

    private final String name;
    private final int score;

    public Student(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}
