import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Small maintenance tool for the lesson READMEs. One file, JDK only, same on Windows (Git Bash, PowerShell) and Linux.
 * Run it from anywhere inside the repository:
 *
 *   java scripts/LessonTool.java check            every lessons/NN-slug/README.md has valid front matter and the 8 numbered sections
 *   java scripts/LessonTool.java index            (re)write lessons/index.json from the front matter of every lesson
 *   java scripts/LessonTool.java index --check    fail if lessons/index.json is not exactly what "index" would write (CI uses this)
 *
 * The contract of index.json is described in docs/WEBSITE-INTEGRATION.md.
 */
public class LessonTool {

    static final String REPOSITORY = "rvirayii-est/java-by-building";
    static final String BRANCH = "main";
    static final List<String> REQUIRED_KEYS = List.of("title", "slug", "week", "hours", "level", "milestone", "objectives");
    static final Set<String> LEVELS = Set.of("beginner", "intermediate", "advanced");

    record Lesson(Path dir, Map<String, Object> front, String body) {
        String slug() {
            return dir.getFileName().toString();
        }
    }

    public static void main(String[] args) throws IOException {
        Path root = findRoot();
        String command = args.length > 0 ? args[0] : "";
        boolean checkOnly = args.length > 1 && args[1].equals("--check");
        switch (command) {
            case "check" -> exit(check(root));
            case "index" -> exit(checkOnly ? checkIndex(root) : writeIndex(root));
            default -> {
                System.err.println("Usage: java scripts/LessonTool.java check | index | index --check");
                System.exit(2);
            }
        }
    }

    static void exit(boolean ok) {
        System.exit(ok ? 0 : 1);
    }

    /** The repository root: the closest folder (here or above) that has a "lessons" folder. */
    static Path findRoot() {
        for (Path dir = Path.of("").toAbsolutePath(); dir != null; dir = dir.getParent()) {
            if (Files.isDirectory(dir.resolve("lessons"))) {
                return dir;
            }
        }
        throw new IllegalStateException("Run this from inside the repository: no 'lessons' folder found");
    }

    static List<Path> lessonDirs(Path root) throws IOException {
        try (Stream<Path> dirs = Files.list(root.resolve("lessons"))) {
            return dirs.filter(Files::isDirectory).filter(d -> d.getFileName().toString().matches("\\d\\d-.+")).sorted().toList();
        }
    }

    // ------------------------------------------------------------------ front matter

    /** Reads the front matter: "key: value" lines, and "key:" followed by "  - item" lines. Values may be wrapped in double quotes. */
    static Map<String, Object> parseFrontMatter(List<String> lines, List<String> problems, String where) {
        Map<String, Object> front = new LinkedHashMap<>();
        if (lines.isEmpty() || !lines.get(0).strip().equals("---")) {
            problems.add(where + ": the file must start with a line '---' (YAML front matter)");
            return front;
        }
        int end = -1;
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).strip().equals("---")) {
                end = i;
                break;
            }
        }
        if (end < 0) {
            problems.add(where + ": the front matter is never closed with a second '---'");
            return front;
        }
        String listKey = null;
        for (String raw : lines.subList(1, end)) {
            String line = raw.stripTrailing();
            if (line.isBlank()) {
                continue;
            }
            if (line.stripLeading().startsWith("- ")) {
                if (listKey == null) {
                    problems.add(where + ": a list item without a key above it: " + line.strip());
                    continue;
                }
                @SuppressWarnings("unchecked")
                List<String> items = (List<String>) front.get(listKey);
                items.add(unquote(line.stripLeading().substring(2).strip()));
                continue;
            }
            int colon = line.indexOf(':');
            if (colon <= 0 || Character.isWhitespace(line.charAt(0))) {
                problems.add(where + ": cannot read the front matter line: " + line);
                continue;
            }
            String key = line.substring(0, colon).strip();
            String value = line.substring(colon + 1).strip();
            if (value.isEmpty()) {
                front.put(key, new ArrayList<String>());
                listKey = key;
            } else {
                front.put(key, unquote(value));
                listKey = null;
            }
        }
        return front;
    }

    static String unquote(String value) {
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1).replace("\\\"", "\"").replace("\\\\", "\\");
        }
        return value;
    }

    static Lesson read(Path dir, List<String> problems) throws IOException {
        Path readme = dir.resolve("README.md");
        String where = "lessons/" + dir.getFileName() + "/README.md";
        if (!Files.isRegularFile(readme)) {
            problems.add(where + ": the file is missing");
            return null;
        }
        List<String> lines = Files.readAllLines(readme, StandardCharsets.UTF_8); // splits on \n and \r\n alike
        Map<String, Object> front = parseFrontMatter(lines, problems, where);
        return new Lesson(dir, front, String.join("\n", lines));
    }

    // ------------------------------------------------------------------ check

    static boolean check(Path root) throws IOException {
        List<String> problems = new ArrayList<>();
        List<Path> dirs = lessonDirs(root);
        for (Path dir : dirs) {
            Lesson lesson = read(dir, problems);
            if (lesson != null) {
                checkLesson(lesson, problems);
            }
        }
        // Lessons are numbered 01..N without gaps: a gap is almost always a typo in a folder name.
        for (int i = 0; i < dirs.size(); i++) {
            if (!dirs.get(i).getFileName().toString().startsWith(String.format("%02d-", i + 1))) {
                problems.add("lessons: expected a folder starting with " + String.format("%02d-", i + 1) + " but found " + dirs.get(i).getFileName());
                break;
            }
        }
        if (problems.isEmpty()) {
            System.out.println("OK: " + dirs.size() + " lesson READMEs have valid front matter and all 8 numbered sections");
            return true;
        }
        problems.forEach(p -> System.out.println("PROBLEM " + p));
        System.out.println(problems.size() + " problem(s)");
        return false;
    }

    static void checkLesson(Lesson lesson, List<String> problems) {
        String where = "lessons/" + lesson.slug() + "/README.md";
        Map<String, Object> front = lesson.front();
        for (String key : REQUIRED_KEYS) {
            if (!front.containsKey(key)) {
                problems.add(where + ": front matter has no '" + key + "'");
            }
        }
        if (front.containsKey("slug") && !front.get("slug").equals(lesson.slug())) {
            problems.add(where + ": slug '" + front.get("slug") + "' must equal the folder name '" + lesson.slug() + "' (slugs are stable URLs)");
        }
        for (String numeric : List.of("week", "hours")) {
            if (front.get(numeric) instanceof String text && !text.matches("\\d+")) {
                problems.add(where + ": '" + numeric + "' must be a whole number, not '" + text + "'");
            }
        }
        if (front.get("level") instanceof String level && !LEVELS.contains(level)) {
            problems.add(where + ": 'level' must be one of " + LEVELS + ", not '" + level + "'");
        }
        if (front.get("objectives") instanceof List<?> objectives && objectives.isEmpty()) {
            problems.add(where + ": 'objectives' must list at least one objective");
        }
        // The 8-part lesson contract (docs/SPEC.md section 5): "## 1. ..." to "## 8. ...", each once, in order.
        List<Integer> found = new ArrayList<>();
        boolean inFence = false;
        for (String line : lesson.body().split("\n")) {
            if (line.startsWith("```")) {
                inFence = !inFence;
            }
            if (!inFence && line.matches("## [1-8]\\. .+")) {
                found.add(line.charAt(3) - '0');
            }
        }
        List<Integer> expected = List.of(1, 2, 3, 4, 5, 6, 7, 8);
        if (!found.equals(expected)) {
            problems.add(where + ": the numbered sections must be '## 1. ' to '## 8. ' once each, in order; found " + found);
        }
    }

    // ------------------------------------------------------------------ index.json

    static boolean writeIndex(Path root) throws IOException {
        String json = buildIndex(root);
        Files.writeString(root.resolve("lessons").resolve("index.json"), json, StandardCharsets.UTF_8);
        System.out.println("Wrote lessons/index.json");
        return true;
    }

    static boolean checkIndex(Path root) throws IOException {
        Path file = root.resolve("lessons").resolve("index.json");
        String expected = buildIndex(root);
        String actual = Files.isRegularFile(file) ? Files.readString(file, StandardCharsets.UTF_8).replace("\r\n", "\n") : "";
        if (expected.equals(actual)) {
            System.out.println("OK: lessons/index.json is up to date");
            return true;
        }
        System.out.println("PROBLEM lessons/index.json is " + (actual.isEmpty() ? "missing" : "stale")
                + ". Run:  java scripts/LessonTool.java index   and commit the result.");
        return false;
    }

    static String buildIndex(Path root) throws IOException {
        List<String> problems = new ArrayList<>();
        List<Lesson> lessons = new ArrayList<>();
        for (Path dir : lessonDirs(root)) {
            Lesson lesson = read(dir, problems);
            if (lesson != null) {
                checkLesson(lesson, problems);
                lessons.add(lesson);
            }
        }
        if (!problems.isEmpty()) {
            problems.forEach(p -> System.out.println("PROBLEM " + p));
            throw new IllegalStateException("Fix the lesson READMEs first: java scripts/LessonTool.java check");
        }
        StringBuilder out = new StringBuilder();
        out.append("{\n");
        out.append("  \"schema\": 1,\n");
        out.append("  \"repository\": ").append(quote(REPOSITORY)).append(",\n");
        out.append("  \"branch\": ").append(quote(BRANCH)).append(",\n");
        out.append("  \"count\": ").append(lessons.size()).append(",\n");
        out.append("  \"lessons\": [\n");
        for (int i = 0; i < lessons.size(); i++) {
            Lesson lesson = lessons.get(i);
            Map<String, Object> f = lesson.front();
            String slug = lesson.slug();
            String path = "lessons/" + slug;
            out.append("    {\n");
            out.append("      \"number\": ").append(Integer.parseInt(slug.substring(0, 2))).append(",\n");
            out.append("      \"slug\": ").append(quote(slug)).append(",\n");
            out.append("      \"title\": ").append(quote((String) f.get("title"))).append(",\n");
            out.append("      \"week\": ").append(Integer.parseInt((String) f.get("week"))).append(",\n");
            out.append("      \"hours\": ").append(Integer.parseInt((String) f.get("hours"))).append(",\n");
            out.append("      \"level\": ").append(quote((String) f.get("level"))).append(",\n");
            out.append("      \"milestone\": ").append(quote((String) f.get("milestone"))).append(",\n");
            out.append("      \"objectives\": [");
            @SuppressWarnings("unchecked")
            List<String> objectives = (List<String>) f.get("objectives");
            for (int j = 0; j < objectives.size(); j++) {
                out.append(j == 0 ? "\n" : ",\n").append("        ").append(quote(objectives.get(j)));
            }
            out.append("\n      ],\n");
            out.append("      \"path\": ").append(quote(path)).append(",\n");
            out.append("      \"readme\": ").append(quote(path + "/README.md")).append(",\n");
            out.append("      \"rawUrl\": ").append(quote("https://raw.githubusercontent.com/" + REPOSITORY + "/" + BRANCH + "/" + path + "/README.md")).append(",\n");
            out.append("      \"url\": ").append(quote("https://github.com/" + REPOSITORY + "/tree/" + BRANCH + "/" + path)).append("\n");
            out.append("    }").append(i < lessons.size() - 1 ? "," : "").append("\n");
        }
        out.append("  ]\n}\n");
        return out.toString();
    }

    /** A JSON string literal. Non-ASCII characters (such as the long dash in titles) are written as they are, in UTF-8. */
    static String quote(String text) {
        StringBuilder out = new StringBuilder("\"");
        for (char c : text.toCharArray()) {
            switch (c) {
                case '"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default -> {
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
                }
            }
        }
        return out.append('"').toString();
    }
}
