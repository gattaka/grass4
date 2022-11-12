package cz.gattserver.grass.articles.plugins.favlink.test;

import cz.gattserver.grass.articles.plugins.favlink.config.FavlinkConfiguration;
import cz.gattserver.grass.core.mock.MockFileSystemService;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.util.DBCleanTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;

public abstract class StrategyTest extends DBCleanTest {

	@Autowired
	protected MockFileSystemService fileSystemService;

	@Autowired
	protected ConfigurationService configurationService;

	protected ClientAndServer mockServer;

	@BeforeEach
	public void init() {
		fileSystemService.init();
		mockServer = startClientAndServer(1929);
	}

	@AfterEach
	public void stopProxy() {
		mockServer.stop();
	}

	protected Path prepareFS(FileSystem fs) throws IOException {
		Path outputDir = fs.getPath("/some/path/favlink/cache/");
		Files.createDirectories(outputDir);

		FavlinkConfiguration configuration = new FavlinkConfiguration();
		configuration.setOutputPath(outputDir.toString());
		configurationService.saveConfiguration(configuration);

		return outputDir;
	}

}
