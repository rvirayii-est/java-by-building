package dev.javabybuilding.docker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.server.Shutdown;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

/** The application side of "runs well in a container": it stops politely when Docker asks, and its data folder is a setting. */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:mvc-it;DB_CLOSE_DELAY=-1")
class ContainerSettingsTest {

    @Autowired ServerProperties server;
    @Autowired Environment env;

    @Test
    void theServerFinishesRequestsInProgressBeforeItStops() {
        assertEquals(Shutdown.GRACEFUL, server.getShutdown());
        assertEquals("20s", env.getProperty("spring.lifecycle.timeout-per-shutdown-phase"));
    }

    @Test
    void theDatabaseFolderIsASettingTheDockerfileCanChange() {
        // Dockerfile: ENV SHOP_DATA_DIR=/data. Spring Boot maps the environment variable SHOP_DATA_DIR to the property shop.data-dir.
        assertEquals("./shop-data", env.getProperty("shop.data-dir"), "the default for running on your own PC");
    }
}
