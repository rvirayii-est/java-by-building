package dev.javabybuilding.exercises;

import java.util.ArrayList;

/** Lesson 11, exercise 2: a class of students. A Classroom object holds many Student objects. */
public class Classroom {

    private final ArrayList<Student> students = new ArrayList<>();

    public void add(Student student) {
        students.add(student);
    }

    public int size() {
        return students.size();
    }

    /** The average score, rounded down (whole-number division). 0 when the classroom is empty. */
    public int averageScore() {
        if (students.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (Student student : students) {
            sum += student.getScore();
        }
        return sum / students.size();
    }

    /** The student with the highest score (the first one if there is a tie), or null when the classroom is empty. */
    public Student top() {
        Student best = null;
        for (Student student : students) {
            if (best == null || student.getScore() > best.getScore()) {
                best = student;
            }
        }
        return best;
    }
}
