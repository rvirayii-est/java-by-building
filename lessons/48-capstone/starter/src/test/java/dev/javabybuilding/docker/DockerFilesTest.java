package dev.javabybuilding.docker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

/**
 * The Dockerfile, compose.yaml and .dockerignore are files, so they can be tested as files. These tests do NOT build an image
 * (that needs Docker, which a build server or a classroom PC may not have); they check the properties that make an image good:
 * two stages, a pinned base image, a non-root user, an exec-form entry point, a cache-friendly order, no secrets inside.
 */
class DockerFilesTest {

    private static final Path DOCKERFILE = Path.of("Dockerfile");
    private static final Path COMPOSE = Path.of("compose.yaml");
    private static final Path DOCKERIGNORE = Path.of("..", "..", "..", ".dockerignore");

    private static String dockerfile() throws IOException {
        return Files.readString(DOCKERFILE);
    }

    /** The instructions without comments and blank lines, with "\" continuation lines joined. */
    private static List<String> instructions() throws IOException {
        String joined = dockerfile().replace("\\\r\n", " ").replace("\\\n", " ");
        return joined.lines().map(String::strip).filter(line -> !line.isEmpty() && !line.startsWith("#")).toList();
    }

    @Test
    void itHasTwoStagesTheFirstCalledBuild() throws IOException {
        List<String> from = instructions().stream().filter(i -> i.startsWith("FROM ")).toList();
        assertEquals(2, from.size(), "build stage + runtime stage");
        assertTrue(from.get(0).endsWith(" AS build"), from.get(0));
    }

    @Test
    void theBaseImagesAreNotLatestAndTheRuntimeIsAJreNotAJdk() throws IOException {
        List<String> from = instructions().stream().filter(i -> i.startsWith("FROM ")).toList();
        for (String line : from) {
            String image = line.split("\\s+")[1];
            assertTrue(image.contains(":") && !image.endsWith(":latest"), "pin a tag: " + image);
            assertTrue(image.contains("21"), "Java 21: " + image);
        }
        assertTrue(from.get(1).split("\\s+")[1].endsWith("-jre"), "the runtime image needs a JRE, not a JDK: " + from.get(1));
    }

    @Test
    void theRuntimeStageCopiesTheJarFromTheBuildStageAndNothingElse() throws IOException {
        List<String> copies = instructions().stream().filter(i -> i.startsWith("COPY --from=")).toList();
        assertEquals(1, copies.size());
        assertTrue(copies.get(0).startsWith("COPY --from=build "), copies.get(0));
        assertTrue(copies.get(0).contains("shop-api.jar"));
    }

    @Test
    void theAppDoesNotRunAsRoot() throws IOException {
        List<String> all = instructions();
        int user = all.stream().filter(i -> i.startsWith("USER ")).mapToInt(all::indexOf).max().orElse(-1);
        assertTrue(user > 0, "there must be a USER instruction");
        assertFalse(all.get(user).contains("root"), all.get(user));
        assertTrue(user > all.indexOf(all.stream().filter(i -> i.startsWith("FROM eclipse-temurin:21-jre")).findFirst().orElseThrow()), "USER belongs to the runtime stage");
    }

    @Test
    void itStartsInExecFormOnPort8080() throws IOException {
        List<String> all = instructions();
        assertTrue(all.contains("EXPOSE 8080"));
        String entry = all.stream().filter(i -> i.startsWith("ENTRYPOINT ")).findFirst().orElseThrow();
        assertTrue(entry.startsWith("ENTRYPOINT [\""), "use the exec form (a JSON list) so java receives the stop signal: " + entry);
        assertTrue(entry.contains("\"-jar\"") && entry.contains("/app/shop-api.jar"));
    }

    @Test
    void theLibrariesAreResolvedBeforeTheSourceIsCopiedSoTheCacheSurvivesCodeEdits() throws IOException {
        List<String> all = instructions();
        int pom = indexStartingWith(all, "COPY ${LESSON}/pom.xml");
        int download = indexContaining(all, "dependency:go-offline");
        int src = indexStartingWith(all, "COPY ${LESSON}/src");
        assertTrue(pom >= 0 && download > pom && src > download, "order must be: pom, download libraries, source (was " + pom + ", " + download + ", " + src + ")");
    }

    @Test
    void theDatabaseLivesInAVolumeAndNoSecretIsBakedIn() throws IOException {
        List<String> all = instructions();
        assertTrue(all.contains("VOLUME /data"));
        assertTrue(all.contains("ENV SHOP_DATA_DIR=/data"));
        String text = dockerfile().toLowerCase();
        assertFalse(text.contains("password"), "never put a password in an image: anyone with the image can read it");
        assertFalse(text.contains("admin123"));
    }

    @Test
    void composeStartsTheApiWithAVolumeThePortAndSettingsFromTheEnvironment() throws IOException {
        Map<String, Object> compose = loadCompose();
        @SuppressWarnings("unchecked")
        Map<String, Object> api = (Map<String, Object>) ((Map<String, Object>) compose.get("services")).get("shop-api");
        assertNotNull(api, "a service called shop-api");
        assertTrue(((List<?>) api.get("ports")).contains("8080:8080"));
        assertTrue(((List<?>) api.get("volumes")).contains("shop-data:/data"));
        @SuppressWarnings("unchecked")
        Map<String, Object> build = (Map<String, Object>) api.get("build");
        String dockerfile = String.valueOf(build.get("dockerfile"));
        assertTrue(dockerfile.startsWith("lessons/48-capstone/") && dockerfile.endsWith("/Dockerfile"), "the Dockerfile of this lesson, from the repository root: " + dockerfile);
        @SuppressWarnings("unchecked")
        Map<String, Object> env = (Map<String, Object>) api.get("environment");
        assertEquals("/data", env.get("SHOP_DATA_DIR"));
        env.forEach((name, value) -> {
            if (name.contains("PASSWORD")) {
                assertTrue(String.valueOf(value).startsWith("${"), name + " must come from the environment, not be written in the file");
            }
        });
        assertTrue(((Map<?, ?>) compose.get("volumes")).containsKey("shop-data"), "declare the named volume");
    }

    @Test
    void dockerignoreKeepsBuildOutputGitAndDatabaseFilesOutOfTheContext() throws IOException {
        List<String> lines = Files.readAllLines(DOCKERIGNORE).stream().map(String::strip).toList();
        for (String required : List.of(".git", "**/target", "**/shop-data", "**/*.mv.db", "**/.env")) {
            assertTrue(lines.contains(required), ".dockerignore must list " + required);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> loadCompose() throws IOException {
        return (Map<String, Object>) new Yaml().load(Files.readString(COMPOSE));
    }

    private static int indexStartingWith(List<String> list, String prefix) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).startsWith(prefix)) {
                return i;
            }
        }
        return -1;
    }

    private static int indexContaining(List<String> list, String part) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).contains(part)) {
                return i;
            }
        }
        return -1;
    }
}
