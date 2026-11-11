// Run with:  java GitSandbox.java      (needs Git installed: it is part of the course setup)
// Builds a throw-away Git repository in a temporary folder, makes two branches change the same line, merges them,
// shows the conflict, resolves it, and prints the history. Your own repository is never touched, and the folder is
// deleted at the end. The output has no commit ids, so it is the same on every computer.
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class GitSandbox {

    static Path repo;
    static Path emptyConfig;

    public static void main(String[] args) throws Exception {
        repo = Files.createTempDirectory("git-sandbox");
        emptyConfig = Files.createTempFile("git-sandbox-config", ".txt"); // so your own Git settings cannot change the demo
        try {
            git("init", "-q", "-b", "main");
            git("config", "user.name", "Demo Student");
            git("config", "user.email", "demo@example.com");
            git("config", "core.autocrlf", "false");

            // 1. First commit on main.
            Path menu = repo.resolve("menu.txt");
            Files.writeString(menu, "1. Add product\n2. Receive stock\n3. Record sale\n4. Stock report\n5. Quit\n");
            git("add", "menu.txt");
            git("commit", "-q", "-m", "feat: add the menu");
            System.out.println("1. main has 1 commit");

            // 2. A feature branch changes line 4.
            git("switch", "-q", "-c", "feature/reorder-report");
            Files.writeString(menu, Files.readString(menu).replace("4. Stock report", "4. Stock and reorder report"));
            git("commit", "-q", "-am", "feat: show reorder suggestions in the report");
            System.out.println("2. branch " + git("branch", "--show-current").trim() + " changed line 4");

            // 3. Meanwhile main changes the SAME line differently.
            git("switch", "-q", "main");
            Files.writeString(menu, Files.readString(menu).replace("4. Stock report", "4. Show stock report"));
            git("commit", "-q", "-am", "fix: clearer wording for the report choice");
            System.out.println("3. main changed line 4 too");

            // 4. Merge: Git cannot decide, so it stops and marks the file.
            String result = git("merge", "feature/reorder-report", "-m", "merge: reorder report");
            System.out.println("4. merge result: " + (result.contains("CONFLICT") ? "CONFLICT in menu.txt" : result.trim()));
            System.out.println("   status: " + git("status", "--short").trim());
            System.out.println("--- menu.txt now contains ---");
            System.out.print(Files.readString(menu));
            System.out.println("-----------------------------");

            // 5. Resolve: YOU decide the final text, delete the three marker lines, then add and commit.
            Files.writeString(menu, "1. Add product\n2. Receive stock\n3. Record sale\n4. Show stock and reorder report\n5. Quit\n");
            git("add", "menu.txt");
            git("commit", "-q", "--no-edit");
            System.out.println("5. resolved. menu.txt line 4: " + Files.readAllLines(menu).get(3));

            // 6. The history, as messages (the shape is a graph: two lines of work joined by the merge commit).
            System.out.println("6. history:");
            System.out.print(git("log", "--graph", "--pretty=format:%s").replace("\r", ""));
            System.out.println();
        } finally {
            deleteFolder(repo);
            Files.deleteIfExists(emptyConfig);
            System.out.println("Sandbox deleted: " + !Files.exists(repo));
        }
    }

    /** Runs "git <args>" inside the sandbox and returns everything it printed. */
    static String git(String... args) throws IOException, InterruptedException {
        List<String> command = new java.util.ArrayList<>(List.of("git"));
        command.addAll(List.of(args));
        ProcessBuilder builder = new ProcessBuilder(command).directory(repo.toFile()).redirectErrorStream(true);
        builder.environment().put("GIT_CONFIG_GLOBAL", emptyConfig.toString());
        builder.environment().put("GIT_CONFIG_NOSYSTEM", "1");
        builder.environment().put("LC_ALL", "C"); // English messages
        Process process = builder.start();
        String output = new String(process.getInputStream().readAllBytes());
        process.waitFor(); // a merge with a conflict exits with 1: that is expected here, so we do not treat it as an error
        return output;
    }

    static void deleteFolder(Path folder) throws IOException {
        try (Stream<Path> paths = Files.walk(folder)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                path.toFile().setWritable(true); // Git marks some files read-only on Windows
                Files.delete(path);
            }
        }
    }
}
