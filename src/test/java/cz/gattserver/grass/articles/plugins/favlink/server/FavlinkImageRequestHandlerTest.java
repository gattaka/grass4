package cz.gattserver.grass.articles.plugins.favlink.server;

import cz.gattserver.grass.articles.plugins.favlink.config.FavlinkConfiguration;
import cz.gattserver.grass3.mock.MockFileSystemService;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.grass3.test.AbstractContextAwareTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FavlinkImageRequestHandlerTest extends AbstractContextAwareTest {

	@Autowired
	private MockFileSystemService fileSystemService;

	@Autowired
	private ConfigurationService configurationService;

	@Before
	public void init() {
		fileSystemService.init();
	}

	private Path prepareFS(FileSystem fs) throws IOException {
		Path outputDir = fs.getPath("/some/path/favlink/cache/");
		Files.createDirectories(outputDir);

		FavlinkConfiguration configuration = new FavlinkConfiguration();
		configuration.setOutputPath(outputDir.toString());
		configurationService.saveConfiguration(configuration);

		return outputDir;
	}

	@Test
	public void test() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		Path outputDir = prepareFS(fs);
		Path testFile = Files.createFile(outputDir.resolve("testFile"));
		Files.write(testFile, new byte[] { 1, 1, 1 });

		Path file = new FavlinkImageRequestHandler().getPath("testFile");
		assertTrue(Files.exists(file));
		assertEquals(3L, Files.size(file));
		assertEquals("testFile", file.getFileName().toString());
		assertEquals("/some/path/favlink/cache/testFile", file.toString());
	}

}
