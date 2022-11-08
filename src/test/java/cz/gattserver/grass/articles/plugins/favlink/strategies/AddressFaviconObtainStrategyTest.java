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

import static org.junit.Assert.*;

public class AddressFaviconObtainStrategyTest extends StrategyTest {

	@Test
	public void testAddressFaviconObtainStrategy_empty() throws IOException {
		try (MockServerClient msc = new MockServerClient("localhost", 1929)) {

			FileSystem fs = fileSystemService.getFileSystem();
			prepareFS(fs);

			AddressFaviconObtainStrategy strategy = new AddressFaviconObtainStrategy(new FaviconCache());
			String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
			assertNull(link);
		}
	}

	@Test
	public void testAddressFaviconObtainStrategy_png() throws IOException {
		try (MockServerClient msc = new MockServerClient("localhost", 1929)) {

			FileSystem fs = fileSystemService.getFileSystem();
			Path outputDir = prepareFS(fs);

			byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.png"));
			msc.when(new HttpRequest().withMethod("GET").withPath("/favicon.png"))
					.respond(new HttpResponse().withStatusCode(200).withBody(favicon));

			AddressFaviconObtainStrategy strategy = new AddressFaviconObtainStrategy(new FaviconCache());
			String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
			assertEquals("mycontextroot/articles-favlink-plugin/localhost.png", link);

			assertTrue(Files.exists(outputDir));
			assertTrue(Files.exists(outputDir.resolve("localhost.png")));
		}
	}

	@Test
	public void testAddressFaviconObtainStrategy_ico() throws IOException {
		try (MockServerClient msc = new MockServerClient("localhost", 1929)) {

			FileSystem fs = fileSystemService.getFileSystem();
			Path outputDir = prepareFS(fs);

			byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.ico"));
			msc.when(new HttpRequest().withMethod("GET").withPath("/favicon.ico"))
					.respond(new HttpResponse().withStatusCode(200).withBody(favicon));

			AddressFaviconObtainStrategy strategy = new AddressFaviconObtainStrategy(new FaviconCache());
			String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
			assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);

			assertTrue(Files.exists(outputDir));
			assertTrue(Files.exists(outputDir.resolve("localhost.ico")));
		}
	}

}
