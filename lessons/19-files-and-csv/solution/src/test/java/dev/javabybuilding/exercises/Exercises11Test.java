package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class Exercises11Test {

    private Classroom classroom() {
        Classroom classroom = new Classroom();
        classroom.add(new Student("Ana", 80));
        classroom.add(new Student("Ben", 95));
        classroom.add(new Student("Cleo", 70));
        return classroom;
    }

    @Test
    void aStudentRemembersNameAndScore() {
        Student ana = new Student("Ana", 80);
        assertEquals("Ana", ana.getName());
        assertEquals(80, ana.getScore());
    }

    @Test
    void classroomCountsItsStudents() {
        assertEquals(0, new Classroom().size());
        assertEquals(3, classroom().size());
    }

    @Test
    void averageScoreIsRoundedDown() {
        assertEquals(81, classroom().averageScore()); // (80 + 95 + 70) / 3 = 81.67
    }

    @Test
    void averageOfAnEmptyClassroomIsZero() {
        assertEquals(0, new Classroom().averageScore());
    }

    @Test
    void topIsTheHighestScore() {
        assertEquals("Ben", classroom().top().getName());
    }

    @Test
    void topOfAnEmptyClassroomIsNull() {
        assertNull(new Classroom().top());
    }
}
