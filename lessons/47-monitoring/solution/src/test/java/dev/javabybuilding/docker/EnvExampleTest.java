package dev.javabybuilding.docker;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/** Configuration is a contract between three files: the properties that READ a variable, the example that DOCUMENTS it, the compose file that PASSES it. */
class EnvExampleTest {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([A-Z][A-Z0-9_]*)[:}]");

    private static Set<String> variablesIn(Path file) throws IOException {
        Set<String> names = new TreeSet<>();
        String withoutComments = Files.readAllLines(file).stream().filter(line -> !line.strip().startsWith("#")).collect(Collectors.joining("\n"));
        Matcher m = PLACEHOLDER.matcher(withoutComments);
        while (m.find()) {
            names.add(m.group(1));
        }
        return names;
    }

    @Test
    void everyVariableTheProdProfileReadsIsDocumentedInDotEnvExample() throws IOException {
        Set<String> read = variablesIn(Path.of("src", "main", "resources", "application-prod.properties"));
        assertTrue(read.containsAll(Set.of("SHOP_ADMIN_PASSWORD", "SHOP_CASHIER_PASSWORD", "SHOP_DATA_DIR", "PORT")), "sanity: " + read);
        String example = Files.readString(Path.of(".env.example"));
        for (String name : read) {
            if (name.equals("SHOP_DATA_DIR")) {
                continue; // set by the Dockerfile, not by the person who deploys
            }
            assertTrue(example.contains(name + "="), name + " is read by application-prod.properties but is missing from .env.example");
        }
    }

    @Test
    void composePassesTheRequiredSecretsAndSwitchesTheProdProfileOn() throws IOException {
        String compose = Files.readString(Path.of("compose.yaml"));
        assertTrue(compose.contains("SPRING_PROFILES_ACTIVE: prod"));
        assertTrue(compose.contains("${SHOP_ADMIN_PASSWORD:?"), "a required variable fails loudly when missing");
        assertTrue(compose.contains("${SHOP_CASHIER_PASSWORD:?"));
        assertFalse(compose.contains("admin123"), "no learning password in the deployment file");
    }

    @Test
    void thePrivateEnvFileIsNeverCommittedOrSentToDocker() throws IOException {
        assertTrue(Files.readAllLines(Path.of("..", "..", "..", ".gitignore")).stream().map(String::strip).anyMatch(l -> l.equals(".env")),
                ".env must be in .gitignore");
        assertTrue(Files.readAllLines(Path.of("..", "..", "..", ".dockerignore")).stream().map(String::strip).anyMatch(l -> l.equals("**/.env")),
                ".env must be in .dockerignore");
    }

    @Test
    void theExampleFileHoldsNoRealSecret() throws IOException {
        Files.readAllLines(Path.of(".env.example")).stream().map(String::strip)
                .filter(l -> l.contains("PASSWORD=")).forEach(l -> assertTrue(l.contains("change-me"), "only placeholders belong here: " + l));
    }
}
