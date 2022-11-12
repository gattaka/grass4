package cz.gattserver.grass.articles.plugins.favlink.strategies;

import cz.gattserver.grass.articles.plugins.favlink.FaviconCache;
import cz.gattserver.grass.articles.plugins.favlink.test.StrategyTest;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class CachedFaviconObtainStrategyTest extends StrategyTest {

	@Test
	public void testCacheFaviconObtainStrategy_missed() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		prepareFS(fs);
		
		FaviconCache cache = new FaviconCache();
		CacheFaviconObtainStrategy strategy = new CacheFaviconObtainStrategy(cache);
		String link = strategy.obtainFaviconURL("http://www.test.cz", "mycontextroot");
		assertNull(link);
	}

	@Test
	public void testCacheFaviconObtainStrategy_cached() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		Path outputDir = prepareFS(fs);
		
		FaviconCache cache = new FaviconCache();
		Path cacheDir = cache.getCacheDirectoryPath();
		
		assertEquals(outputDir, cacheDir);
		
		Files.createFile(cacheDir.resolve("www.test.cz.png"));
		CacheFaviconObtainStrategy strategy = new CacheFaviconObtainStrategy(cache);
		String link = strategy.obtainFaviconURL("http://www.test.cz", "mycontextroot");
		assertEquals("mycontextroot/articles-favlink-plugin/www.test.cz.png", link);
	}

}
