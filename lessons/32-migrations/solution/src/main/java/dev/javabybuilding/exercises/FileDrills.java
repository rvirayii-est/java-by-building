package dev.javabybuilding.exercises;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

/** Lesson 19, exercises: reading and writing text files with java.nio.file.Files. Each method gets the Path it should use. */
public class FileDrills {

    /** How many lines the file has. A file with no text has 0. */
    public static int countLines(Path file) throws IOException {
        return Files.readAllLines(file).size();
    }

    /** The lines with spaces trimmed from both ends, leaving out the lines that are empty after trimming. */
    public static List<String> readNonBlankTrimmed(Path file) throws IOException {
        return Files.readAllLines(file).stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();
    }

    /** Adds one line to the end of the file. The file is created when it does not exist yet. */
    public static void appendLine(Path file, String line) throws IOException {
        Files.writeString(file, line + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    /** Like countLines, but -1 when the file does not exist (instead of an exception). */
    public static int countLinesOrMinusOne(Path file) throws IOException {
        try {
            return countLines(file);
        } catch (NoSuchFileException e) {
            return -1;
        }
    }

    /**
     * Adds up one column of a simple CSV file (no quotes). The first line is a header and is skipped.
     * Columns are counted from 0. Empty lines are skipped.
     *
     * @throws NumberFormatException when a cell in that column is not a whole number
     */
    public static int sumColumn(Path csvFile, int column) throws IOException {
        List<String> lines = Files.readAllLines(csvFile);
        int sum = 0;
        for (int i = 1; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) {
                sum += Integer.parseInt(lines.get(i).split(",")[column].trim());
            }
        }
        return sum;
    }
}
