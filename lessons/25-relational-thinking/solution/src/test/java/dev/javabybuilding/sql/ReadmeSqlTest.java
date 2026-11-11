package dev.javabybuilding.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Keeps the lesson honest: every SQL example in this lesson's README is run against the practice database, and
 * the output printed under it must be exactly what the database really gives.
 *
 * <p>The README rules (so that this test can read it):
 * <ul>
 *   <li>a code block starting with three backticks and the word sql is RUN, each on its own fresh database;</li>
 *   <li>if the block's first line is the comment "-- empty database", it runs on a database with no tables;</li>
 *   <li>a text block directly under a sql block is the EXPECTED output (the last statement's table);</li>
 *   <li>a block marked "sql skip" is shown but not run (for example SQL with ? holes, which only JDBC can fill);</li>
 *   <li>a mermaid block is compared with the diagram Erd.mermaid draws from the real tables.</li>
 * </ul>
 */
class ReadmeSqlTest {

    private record Block(String info, String body, int startLine, int endLine) {}

    private static List<Block> blocks() throws IOException {
        List<String> lines = Files.readAllLines(Path.of("..", "README.md")); // the module folder is the working directory
        List<Block> blocks = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith("```") && lines.get(i).length() > 3) {
                String info = lines.get(i).substring(3).strip();
                int end = i + 1;
                StringBuilder body = new StringBuilder();
                while (!lines.get(end).startsWith("```")) {
                    body.append(lines.get(end)).append('\n');
                    end++;
                }
                blocks.add(new Block(info, body.toString().strip(), i + 1, end + 1));
                i = end;
            } else if (lines.get(i).startsWith("```")) {
                throw new IllegalStateException("README line " + (i + 1) + ": a code block must say its language, for example ```text");
            }
        }
        return blocks;
    }

    /** The expected-output block right under blocks.get(i), or null when there is none. */
    private static String expectedFor(List<Block> blocks, int i) throws IOException {
        if (i + 1 >= blocks.size() || !blocks.get(i + 1).info().equals("text")) {
            return null;
        }
        List<String> lines = Files.readAllLines(Path.of("..", "README.md"));
        for (int line = blocks.get(i).endLine() + 1; line < blocks.get(i + 1).startLine(); line++) {
            if (!lines.get(line - 1).isBlank()) {
                return null; // something is written in between, so the text block is not this block's output
            }
        }
        return blocks.get(i + 1).body();
    }

    @Test
    void everySqlExampleRunsAndPrintsWhatTheReadmeShows() throws IOException, SQLException {
        List<Block> blocks = blocks();
        int run = 0;
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            if (!block.info().equals("sql")) {
                continue;
            }
            boolean empty = block.body().startsWith("-- empty database");
            try (Connection db = empty ? PracticeDb.openEmpty() : PracticeDb.open()) {
                QueryResult result = SqlRunner.run(db, block.body());
                String expected = expectedFor(blocks, i);
                if (expected != null) {
                    assertEquals(expected, result.toTable(), "README line " + block.startLine() + ": the printed output is not what H2 gives");
                }
            } catch (SQLException e) {
                throw new AssertionError("README line " + block.startLine() + ": the SQL fails: " + e.getMessage(), e);
            }
            run++;
        }
        assertFalse(run == 0 && Files.readString(Path.of("..", "README.md")).contains("```sql"), "no sql block was run");
    }

    @Test
    void theErdsInTheReadmeAreDrawnFromTheRealTables() throws IOException, SQLException {
        try (Connection db = PracticeDb.open()) {
            String drawn = Erd.mermaid(db);
            for (Block block : blocks()) {
                if (block.info().equals("mermaid")) {
                    assertEquals(drawn, block.body(), "README line " + block.startLine() + ": the ERD is out of date. Paste Erd.mermaid(...) here.");
                }
            }
        }
    }
}
