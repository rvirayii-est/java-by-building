package dev.javabybuilding.exercises;

import java.util.ArrayList;

/** Lesson 11, exercise 2: a class of students. A Classroom object holds many Student objects. */
public class Classroom {

    private final ArrayList<Student> students = new ArrayList<>();

    /** TODO: add the student to the list. */
    public void add(Student student) {
    }

    /** TODO: how many students are in the classroom. */
    public int size() {
        return -1;
    }

    /** TODO: the average score, rounded down (whole-number division). 0 when the classroom is empty. */
    public int averageScore() {
        return -1;
    }

    /** TODO: the student with the highest score (the first one if there is a tie), or null when the classroom is empty. */
    public Student top() {
        return null;
    }
}
