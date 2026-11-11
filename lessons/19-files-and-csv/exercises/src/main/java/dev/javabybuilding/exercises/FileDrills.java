package dev.javabybuilding.exercises;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Lesson 19, exercises: reading and writing text files with java.nio.file.Files.
 * Each method gets the Path it should use. "throws IOException" is already written: reading a file can fail,
 * and IOException is a checked exception, so the compiler makes sure someone deals with it.
 */
public class FileDrills {

    /** TODO: how many lines the file has (a file with no text has 0). Hint: Files.readAllLines(file).size() */
    public static int countLines(Path file) throws IOException {
        return -1;
    }

    /**
     * TODO: the lines with spaces trimmed from both ends, leaving out the lines that are empty after trimming.
     * Hint: Files.readAllLines, then a loop or a stream with trim() and isEmpty().
     */
    public static List<String> readNonBlankTrimmed(Path file) throws IOException {
        return null;
    }

    /**
     * TODO: add one line to the END of the file, creating the file if it does not exist yet.
     * Hint: Files.writeString(file, line + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND)
     */
    public static void appendLine(Path file, String line) throws IOException {
    }

    /**
     * TODO: like countLines, but -1 when the file does not exist (instead of an exception).
     * Hint: call countLines inside try { ... } catch (NoSuchFileException e) { ... }
     */
    public static int countLinesOrMinusOne(Path file) throws IOException {
        return -2;
    }

    /**
     * TODO: add up one column of a simple CSV file (no quotes). The first line is a header and is skipped. Columns count from 0.
     * Empty lines are skipped. Hint: line.split(",")[column].trim(), then Integer.parseInt. A cell that is not a whole
     * number may simply throw NumberFormatException.
     */
    public static int sumColumn(Path csvFile, int column) throws IOException {
        return -1;
    }
}
