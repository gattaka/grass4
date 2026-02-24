package cz.gattserver.grass.articles.plugins.favlink.test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import cz.gattserver.grass.core.mock.MockFileSystemService;
import cz.gattserver.grass.core.util.DBCleanTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class StrategyTest extends DBCleanTest {

    @Autowired
    protected MockFileSystemService fileSystemService;

    protected WireMockServer wireMockServer;

    @BeforeEach
    public void init() {
        fileSystemService.init();
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(1929));
        wireMockServer.start();
    }

    @AfterEach
    public void stopProxy() {
        wireMockServer.stop();
    }

    protected Path prepareFS(FileSystem fs) throws IOException {
        Path outputDir = fs.getPath("files/favlink");
        Files.createDirectories(outputDir);
        return outputDir;
    }

}
