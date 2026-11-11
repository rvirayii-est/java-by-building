// Run with:  java FilesDemo.java
// Everything happens in a temporary folder that is deleted at the end, so running it leaves nothing behind.
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class FilesDemo {

    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("filesdemo");
        try {
            Path file = dir.resolve("products.csv");

            // 1. Write a whole list of lines at once (UTF-8 text, one line each).
            Files.write(file, List.of("name,price,qty", "Rice 25kg,1250.50,12", "\"Rice, premium\",1500.00,3"));
            System.out.println("Wrote " + Files.readAllLines(file).size() + " lines");

            // 2. try-with-resources: the writer is closed for us, even if something goes wrong.
            try (BufferedWriter writer = Files.newBufferedWriter(file, StandardOpenOption.APPEND)) {
                writer.write("Sugar 1kg,75.00,0");
                writer.newLine();
            }

            // 3. Read everything back.
            List<String> lines = Files.readAllLines(file);
            System.out.println("Now " + lines.size() + " lines; the last is: " + lines.get(lines.size() - 1));

            // 4. Split the lines into fields. Splitting on a comma is wrong when a field is quoted: look at line 3.
            for (String line : lines.subList(1, lines.size())) {
                System.out.println(line.split(",").length + " pieces from: " + line);
            }
            System.out.println("Parsed properly: " + parseCsvLine(lines.get(2)));

            // 5. A missing file is a checked IOException. Catch the kind you can handle.
            try {
                Files.readAllLines(dir.resolve("nope.csv"));
            } catch (NoSuchFileException e) {
                System.out.println("No such file: " + dir.relativize(Path.of(e.getFile())));
            }

            // 6. Check before you act.
            System.out.println("products.csv exists: " + Files.exists(file) + ", nope.csv exists: " + Files.exists(dir.resolve("nope.csv")));
        } finally {
            deleteFolder(dir);
            System.out.println("Temporary folder deleted: " + !Files.exists(dir));
        }
    }

    /** Minimal CSV reader that understands "quoted, fields". (lesson 19's Csv class does this properly.) */
    static List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (char c : line.toCharArray()) {
            if (c == '"') {
                quoted = !quoted;
            } else if (c == ',' && !quoted) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields;
    }

    static void deleteFolder(Path folder) throws IOException {
        try (Stream<Path> paths = Files.walk(folder)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) { // files before the folders that hold them
                Files.delete(path);
            }
        }
    }
}
