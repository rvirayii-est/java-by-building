package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class Exercises19Test {

    @TempDir
    Path dir; // a fresh empty folder for each test, deleted afterwards

    @Test
    void countLinesCountsEveryLine() throws IOException {
        Path file = dir.resolve("notes.txt");
        Files.writeString(file, "one\ntwo\n\nfour\n");
        assertEquals(4, FileDrills.countLines(file));
    }

    @Test
    void countLinesOfAnEmptyFileIsZero() throws IOException {
        Path file = dir.resolve("empty.txt");
        Files.writeString(file, "");
        assertEquals(0, FileDrills.countLines(file));
    }

    @Test
    void countLinesOfAMissingFileThrows() {
        assertThrows(NoSuchFileException.class, () -> FileDrills.countLines(dir.resolve("missing.txt")));
    }

    @Test
    void readNonBlankTrimmedDropsBlankLinesAndTrimsTheRest() throws IOException {
        Path file = dir.resolve("notes.txt");
        Files.writeString(file, "  rice  \n\n   \noil\n");
        assertEquals(List.of("rice", "oil"), FileDrills.readNonBlankTrimmed(file));
    }

    @Test
    void appendLineCreatesTheFileThenKeepsAdding() throws IOException {
        Path file = dir.resolve("log.txt");
        FileDrills.appendLine(file, "first");
        FileDrills.appendLine(file, "second");
        assertEquals(List.of("first", "second"), Files.readAllLines(file));
    }

    @Test
    void countLinesOrMinusOneAnswersMinusOneForAMissingFile() throws IOException {
        Path file = dir.resolve("log.txt");
        assertEquals(-1, FileDrills.countLinesOrMinusOne(file));
        FileDrills.appendLine(file, "hello");
        assertEquals(1, FileDrills.countLinesOrMinusOne(file));
    }

    @Test
    void sumColumnAddsOneColumnAndSkipsTheHeader() throws IOException {
        Path file = dir.resolve("stock.csv");
        Files.writeString(file, "name,qty,price\nrice,12,100\n\noil, 4 ,98\n");
        assertEquals(16, FileDrills.sumColumn(file, 1));
        assertEquals(198, FileDrills.sumColumn(file, 2));
    }

    @Test
    void sumColumnWithATextCellThrowsNumberFormatException() throws IOException {
        Path file = dir.resolve("stock.csv");
        Files.writeString(file, "name,qty\nrice,many\n");
        assertThrows(NumberFormatException.class, () -> FileDrills.sumColumn(file, 1));
    }
}
