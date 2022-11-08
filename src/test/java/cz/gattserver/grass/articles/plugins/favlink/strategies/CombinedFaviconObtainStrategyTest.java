package cz.gattserver.grass.articles.plugins.favlink.strategies;

import cz.gattserver.grass.articles.plugins.favlink.FaviconCache;
import cz.gattserver.grass.articles.plugins.favlink.test.StrategyTest;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

public class CombinedFaviconObtainStrategyTest extends StrategyTest {

	@Test
	public void testCombinedFaviconObtainStrategyTest_empty() throws IOException {
		try (MockServerClient msc = new MockServerClient("localhost", 1929)) {

			FileSystem fs = fileSystemService.getFileSystem();
			prepareFS(fs);

			String page = IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_empty.html"),
					"UTF-8");
			msc.when(new HttpRequest().withMethod("GET").withPath("/dummy/site"))
					.respond(new HttpResponse().withStatusCode(200).withBody(page));

			FaviconObtainStrategy strategy = new CombinedFaviconObtainStrategy();
			String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
			assertEquals("mycontextroot/frontend/favlink/default.ico", link);
		}
	}

	@Test
	public void testCombinedFaviconObtainStrategyTest_cached() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		Path outputDir = prepareFS(fs);

		FaviconCache cache = new FaviconCache();
		Path cacheDir = cache.getCacheDirectoryPath();

		assertEquals(outputDir, cacheDir);

		Files.createFile(cacheDir.resolve("localhost.ico"));

		FaviconObtainStrategy strategy = new CombinedFaviconObtainStrategy();
		String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
		assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);
	}

	@Test
	public void testCombinedFaviconObtainStrategyTest_address() throws IOException {
		try (MockServerClient msc = new MockServerClient("localhost", 1929)) {

			FileSystem fs = fileSystemService.getFileSystem();
			prepareFS(fs);

			byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.png"));
			msc.when(new HttpRequest().withMethod("GET").withPath("/favicon.png"))
					.respond(new HttpResponse().withStatusCode(200).withBody(favicon));

			FaviconObtainStrategy strategy = new CombinedFaviconObtainStrategy();
			String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
			assertEquals("mycontextroot/articles-favlink-plugin/localhost.png", link);
		}
	}

	@Test
	public void testCombinedFaviconObtainStrategyTest_header() throws IOException {
		try (MockServerClient msc = new MockServerClient("localhost", 1929)) {

			FileSystem fs = fileSystemService.getFileSystem();
			prepareFS(fs);

			String page = IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_http_png.html"),
					"UTF-8");
			msc.when(new HttpRequest().withMethod("GET").withPath("/dummy/site"))
					.respond(new HttpResponse().withStatusCode(200).withBody(page));

			byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.png"));
			msc.when(new HttpRequest().withMethod("GET").withPath("/imgadr/mockFavicon.png"))
					.respond(new HttpResponse().withStatusCode(200).withBody(favicon));

			FaviconObtainStrategy strategy = new CombinedFaviconObtainStrategy();
			String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
			assertEquals("mycontextroot/articles-favlink-plugin/localhost.png", link);
		}
	}

}
