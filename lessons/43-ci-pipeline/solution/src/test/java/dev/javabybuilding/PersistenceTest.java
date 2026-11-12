package dev.javabybuilding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.javabybuilding.api.CreateSaleRequest;
import dev.javabybuilding.service.ProductService;
import dev.javabybuilding.service.ReportService;
import dev.javabybuilding.service.SaleService;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/** Milestone M5 through the API: start the app, sell, stop it, start it again on the same folder: the sale is still there. */
class PersistenceTest {

    private static ConfigurableApplicationContext start(Path folder) {
        return SpringApplication.run(ShopApplication.class, "--server.port=0", "--spring.main.banner-mode=off", "--logging.level.root=WARN",
                "--shop.data-dir=" + folder.toString().replace('\\', '/')); // H2 URLs use forward slashes, also on Windows
    }

    @Test
    void aSaleSurvivesARestartAndTheSampleIsSeededOnlyOnce(@TempDir Path folder) throws Exception {
        try (ConfigurableApplicationContext first = start(folder)) {
            assertEquals(4, first.getBean(ProductService.class).list(null).size());
            first.getBean(SaleService.class).create(new CreateSaleRequest("Rice 25kg", 2, null, null, null, null));
        }
        assertTrue(Files.exists(folder.resolve("shop.mv.db")), "the database file is in the data folder");

        try (ConfigurableApplicationContext second = start(folder)) {
            assertEquals(4, second.getBean(ProductService.class).list(null).size()); // not seeded again
            assertEquals(10, second.getBean(ProductService.class).get("Rice 25kg").quantity());
            assertEquals(1, second.getBean(ReportService.class).salesReport().salesCount());
        }
    }
}
