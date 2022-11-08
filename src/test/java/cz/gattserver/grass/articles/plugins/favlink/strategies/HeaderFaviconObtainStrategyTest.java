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

public class HeaderFaviconObtainStrategyTest extends StrategyTest {

	@Test
	public void testHeaderFaviconObtainStrategy_empty() throws IOException {
		try (MockServerClient msc = new MockServerClient("localhost", 1929)) {

			FileSystem fs = fileSystemService.getFileSystem();
			prepareFS(fs);

			String page = IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_empty.html"),
					"UTF-8");
			msc.when(new HttpRequest().withMethod("GET").withPath("/dummy/site"))
					.respond(new HttpResponse().withStatusCode(200).withBody(page));

			HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy(new FaviconCache());
			String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
			assertNull(link);
		}
	}

	@Test
	public void testHeaderFaviconObtainStrategy_http_png() throws IOException {
		try (MockServerClient msc = new MockServerClient("localhost", 1929)) {

			FileSystem fs = fileSystemService.getFileSystem();
			Path outputDir = prepareFS(fs);

			String page = IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_http_png.html"),
					"UTF-8");
			msc.when(new HttpRequest().withMethod("GET").withPath("/dummy/site"))
					.respond(new HttpResponse().withStatusCode(200).withBody(page));

			byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.png"));
			msc.when(new HttpRequest().withMethod("GET").withPath("/imgadr/mockFavicon.png"))
					.respond(new HttpResponse().withStatusCode(200).withBody(favicon));

			HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy(new FaviconCache());
			String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
			assertEquals("mycontextroot/articles-favlink-plugin/localhost.png", link);

			assertTrue(Files.exists(outputDir));
			assertTrue(Files.exists(outputDir.resolve("localhost.png")));
		}
	}

	@Test
	public void testHeaderFaviconObtainStrategy_http_meta() throws IOException {
		try (MockServerClient msc = new MockServerClient("localhost", 1929)) {

			FileSystem fs = fileSystemService.getFileSystem();
			Path outputDir = prepareFS(fs);

			String page = IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_http_meta.html"),
					"UTF-8");
			msc.when(new HttpRequest().withMethod("GET").withPath("/dummy/site"))
					.respond(new HttpResponse().withStatusCode(200).withBody(page));

			byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.ico"));
			msc.when(new HttpRequest().withMethod("GET").withPath("/imgadr/mockFavicon.ico"))
					.respond(new HttpResponse().withStatusCode(200).withBody(favicon));

			HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy(new FaviconCache());
			String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
			assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);

			assertTrue(Files.exists(outputDir));
			assertTrue(Files.exists(outputDir.resolve("localhost.ico")));
		}
	}

	@Test
	public void testHeaderFaviconObtainStrategy_http_ico() throws IOException {
		try (MockServerClient msc = new MockServerClient("localhost", 1929)) {

			FileSystem fs = fileSystemService.getFileSystem();
			Path outputDir = prepareFS(fs);

			String page = IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_http_ico.html"),
					"UTF-8");
			msc.when(new HttpRequest().withMethod("GET").withPath("/dummy/site"))
					.respond(new HttpResponse().withStatusCode(200).withBody(page));

			byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.ico"));
			msc.when(new HttpRequest().withMethod("GET").withPath("/imgadr/mockFavicon.ico"))
					.respond(new HttpResponse().withStatusCode(200).withBody(favicon));

			HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy(new FaviconCache());
			String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
			assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);

			assertTrue(Files.exists(outputDir));
			assertTrue(Files.exists(outputDir.resolve("localhost.ico")));
		}
	}

	@Test
	public void testHeaderFaviconObtainStrategy_http_ico_not_normalized() throws IOException {
		try (MockServerClient msc = new MockServerClient("localhost", 1929)) {

			FileSystem fs = fileSystemService.getFileSystem();
			Path outputDir = prepareFS(fs);

			String page = IOUtils.toString(
					this.getClass().getResourceAsStream("headerFaviconMockHTML_http_ico_not_normalized.html"), "UTF-8");
			msc.when(new HttpRequest().withMethod("GET").withPath("/dummy/site"))
					.respond(new HttpResponse().withStatusCode(200).withBody(page));

			byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.ico"));
			msc.when(new HttpRequest().withMethod("GET").withPath("/imgadr/mockFavicon.ico"))
					.respond(new HttpResponse().withStatusCode(200).withBody(favicon));

			HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy(new FaviconCache());
			String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
			assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);

			assertTrue(Files.exists(outputDir));
			assertTrue(Files.exists(outputDir.resolve("localhost.ico")));
		}
	}

	@Test
	public void testHeaderFaviconObtainStrategy_relative_ico() throws IOException {
		try (MockServerClient msc = new MockServerClient("localhost", 1929)) {

			FileSystem fs = fileSystemService.getFileSystem();
			Path outputDir = prepareFS(fs);

			String page = IOUtils
					.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_relative_ico.html"), "UTF-8");
			msc.when(new HttpRequest().withMethod("GET").withPath("/dummy/site"))
					.respond(new HttpResponse().withStatusCode(200).withBody(page));

			byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.ico"));
			msc.when(new HttpRequest().withMethod("GET").withPath("/dummy/imgadr/mockFavicon.ico"))
					.respond(new HttpResponse().withStatusCode(200).withBody(favicon));

			HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy(new FaviconCache());
			String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
			assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);

			assertTrue(Files.exists(outputDir));
			assertTrue(Files.exists(outputDir.resolve("localhost.ico")));
		}
	}

	@Test
	public void testHeaderFaviconObtainStrategy_relative_base_ico() throws IOException {
		try (MockServerClient msc = new MockServerClient("localhost", 1929)) {

			FileSystem fs = fileSystemService.getFileSystem();
			Path outputDir = prepareFS(fs);

			String page = IOUtils.toString(
					this.getClass().getResourceAsStream("headerFaviconMockHTML_relative_base_ico.html"), "UTF-8");
			msc.when(new HttpRequest().withMethod("GET").withPath("/root/sub/site"))
					.respond(new HttpResponse().withStatusCode(200).withBody(page));

			byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.ico"));
			msc.when(new HttpRequest().withMethod("GET").withPath("/root/imgadr/mockFavicon.ico"))
					.respond(new HttpResponse().withStatusCode(200).withBody(favicon));

			HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy(new FaviconCache());
			String link = strategy.obtainFaviconURL("http://localhost:1929/root/sub/site", "mycontextroot");
			assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);

			assertTrue(Files.exists(outputDir));
			assertTrue(Files.exists(outputDir.resolve("localhost.ico")));
		}
	}
	
	@Test
	public void testHeaderFaviconObtainStrategy_relative_base2_ico() throws IOException {
		try (MockServerClient msc = new MockServerClient("localhost", 1929)) {

			FileSystem fs = fileSystemService.getFileSystem();
			Path outputDir = prepareFS(fs);

			String page = IOUtils.toString(
					this.getClass().getResourceAsStream("headerFaviconMockHTML_relative_base_ico.html"), "UTF-8");
			msc.when(new HttpRequest().withMethod("GET").withPath("/root/sub/site"))
					.respond(new HttpResponse().withStatusCode(200).withBody(page));

			byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.ico"));
			msc.when(new HttpRequest().withMethod("GET").withPath("/root/imgadr/mockFavicon.ico"))
					.respond(new HttpResponse().withStatusCode(200).withBody(favicon));

			HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy(new FaviconCache());
			String link = strategy.obtainFaviconURL("http://localhost:1929/root/sub/site", "mycontextroot");
			assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);

			assertTrue(Files.exists(outputDir));
			assertTrue(Files.exists(outputDir.resolve("localhost.ico")));
		}
	}

	@Test
	public void testHeaderFaviconObtainStrategy_slashed_ico() throws IOException {
		try (MockServerClient msc = new MockServerClient("localhost", 1929)) {

			FileSystem fs = fileSystemService.getFileSystem();
			Path outputDir = prepareFS(fs);

			String page = IOUtils
					.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_slashed_ico.html"), "UTF-8");
			msc.when(new HttpRequest().withMethod("GET").withPath("/dummy/site"))
					.respond(new HttpResponse().withStatusCode(200).withBody(page));

			byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.ico"));
			msc.when(new HttpRequest().withMethod("GET").withPath("/imgadr/mockFavicon.ico"))
					.respond(new HttpResponse().withStatusCode(200).withBody(favicon));

			HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy(new FaviconCache());
			String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
			assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);

			assertTrue(Files.exists(outputDir));
			assertTrue(Files.exists(outputDir.resolve("localhost.ico")));
		}
	}

	@Test
	public void testHeaderFaviconObtainStrategy_slashed2_ico() throws IOException {
		try (MockServerClient msc = new MockServerClient("localhost", 1929)) {

			FileSystem fs = fileSystemService.getFileSystem();
			Path outputDir = prepareFS(fs);

			String page = IOUtils
					.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_slashed2_ico.html"), "UTF-8");
			msc.when(new HttpRequest().withMethod("GET").withPath("/dummy/site"))
					.respond(new HttpResponse().withStatusCode(200).withBody(page));

			byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.ico"));
			msc.when(new HttpRequest().withMethod("GET").withPath("/imgadr/mockFavicon.ico"))
					.respond(new HttpResponse().withStatusCode(200).withBody(favicon));

			HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy(new FaviconCache());
			String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
			assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);

			assertTrue(Files.exists(outputDir));
			assertTrue(Files.exists(outputDir.resolve("localhost.ico")));
		}
	}

}
