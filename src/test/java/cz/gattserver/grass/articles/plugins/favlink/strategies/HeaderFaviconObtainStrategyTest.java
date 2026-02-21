package cz.gattserver.grass.articles.plugins.favlink.strategies;

import cz.gattserver.grass.articles.plugins.favlink.FaviconCache;
import cz.gattserver.grass.articles.plugins.favlink.test.StrategyTest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class HeaderFaviconObtainStrategyTest extends StrategyTest {

    @Test
    void testHeaderFaviconObtainStrategy_empty() throws IOException {
        String page =
                IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_empty.html"), "UTF-8");
        wireMockServer.stubFor(get(urlEqualTo("/dummy/site")).willReturn(aResponse().withStatus(200).withBody(page)));

        FileSystem fs = fileSystemService.getFileSystem();
        prepareFS(fs);

        HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy(new FaviconCache());
        String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
        assertNull(link);
    }

    @Test
    void testHeaderFaviconObtainStrategy_http_png() throws IOException {
        String page =
                IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_http_png.html"), "UTF-8");
        wireMockServer.stubFor(get(urlEqualTo("/dummy/site")).willReturn(aResponse().withStatus(200).withBody(page)));

        byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.png"));
        wireMockServer.stubFor(get(urlEqualTo("/imgadr/mockFavicon.png")).willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "image/png").withBody(favicon)));

        FileSystem fs = fileSystemService.getFileSystem();
        Path outputDir = prepareFS(fs);

        HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy(new FaviconCache());
        String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
        assertEquals("mycontextroot/articles-favlink-plugin/localhost.png", link);

        assertTrue(Files.exists(outputDir));
        assertTrue(Files.exists(outputDir.resolve("localhost.png")));
    }

    @Test
    void testHeaderFaviconObtainStrategy_http_meta() throws IOException {
        String page =
                IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_http_meta.html"), "UTF-8");
        wireMockServer.stubFor(get(urlEqualTo("/dummy/site")).willReturn(aResponse().withStatus(200).withBody(page)));

        byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.ico"));
        wireMockServer.stubFor(get(urlEqualTo("/imgadr/mockFavicon.ico")).willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "image/x-icon").withBody(favicon)));

        FileSystem fs = fileSystemService.getFileSystem();
        Path outputDir = prepareFS(fs);

        HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy(new FaviconCache());
        String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
        assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);

        assertTrue(Files.exists(outputDir));
        assertTrue(Files.exists(outputDir.resolve("localhost.ico")));
    }

    @Test
    void testHeaderFaviconObtainStrategy_http_ico() throws IOException {
        String page =
                IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_http_ico.html"), "UTF-8");
        wireMockServer.stubFor(get(urlEqualTo("/dummy/site")).willReturn(aResponse().withStatus(200).withBody(page)));

        byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.ico"));
        wireMockServer.stubFor(get(urlEqualTo("/imgadr/mockFavicon.ico")).willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "image/x-icon").withBody(favicon)));

        FileSystem fs = fileSystemService.getFileSystem();
        Path outputDir = prepareFS(fs);

        HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy(new FaviconCache());
        String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
        assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);

        assertTrue(Files.exists(outputDir));
        assertTrue(Files.exists(outputDir.resolve("localhost.ico")));
    }

    @Test
    void testHeaderFaviconObtainStrategy_http_ico_not_normalized() throws IOException {
        String page = IOUtils.toString(
                this.getClass().getResourceAsStream("headerFaviconMockHTML_http_ico_not_normalized.html"), "UTF-8");
        wireMockServer.stubFor(get(urlEqualTo("/dummy/site")).willReturn(aResponse().withStatus(200).withBody(page)));

        byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.ico"));
        wireMockServer.stubFor(get(urlEqualTo("/imgadr/mockFavicon.ico")).willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "image/x-icon").withBody(favicon)));

        FileSystem fs = fileSystemService.getFileSystem();
        Path outputDir = prepareFS(fs);

        HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy(new FaviconCache());
        String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
        assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);

        assertTrue(Files.exists(outputDir));
        assertTrue(Files.exists(outputDir.resolve("localhost.ico")));
    }

    @Test
    void testHeaderFaviconObtainStrategy_relative_ico() throws IOException {
        String page = IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_relative_ico.html"),
                "UTF-8");
        wireMockServer.stubFor(get(urlEqualTo("/dummy/site")).willReturn(aResponse().withStatus(200).withBody(page)));

        byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.ico"));
        wireMockServer.stubFor(get(urlEqualTo("/dummy/imgadr/mockFavicon.ico")).willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "image/x-icon").withBody(favicon)));

        FileSystem fs = fileSystemService.getFileSystem();
        Path outputDir = prepareFS(fs);

        HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy(new FaviconCache());
        String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
        assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);

        assertTrue(Files.exists(outputDir));
        assertTrue(Files.exists(outputDir.resolve("localhost.ico")));
    }

    @Test
    void testHeaderFaviconObtainStrategy_relative_base_ico() throws IOException {
        String page =
                IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_relative_base_ico.html"),
                        "UTF-8");
        wireMockServer.stubFor(
                get(urlEqualTo("/root/sub/site")).willReturn(aResponse().withStatus(200).withBody(page)));

        byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.ico"));
        wireMockServer.stubFor(get(urlEqualTo("/root/imgadr/mockFavicon.ico")).willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "image/x-icon").withBody(favicon)));

        FileSystem fs = fileSystemService.getFileSystem();
        Path outputDir = prepareFS(fs);

        HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy(new FaviconCache());
        String link = strategy.obtainFaviconURL("http://localhost:1929/root/sub/site", "mycontextroot");
        assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);

        assertTrue(Files.exists(outputDir));
        assertTrue(Files.exists(outputDir.resolve("localhost.ico")));
    }

    @Test
    void testHeaderFaviconObtainStrategy_relative_base2_ico() throws IOException {
        String page =
                IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_relative_base_ico.html"),
                        "UTF-8");
        wireMockServer.stubFor(
                get(urlEqualTo("/root/sub/site")).willReturn(aResponse().withStatus(200).withBody(page)));

        byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.ico"));
        wireMockServer.stubFor(get(urlEqualTo("/root/imgadr/mockFavicon.ico")).willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "image/x-icon").withBody(favicon)));

        FileSystem fs = fileSystemService.getFileSystem();
        Path outputDir = prepareFS(fs);

        HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy(new FaviconCache());
        String link = strategy.obtainFaviconURL("http://localhost:1929/root/sub/site", "mycontextroot");
        assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);

        assertTrue(Files.exists(outputDir));
        assertTrue(Files.exists(outputDir.resolve("localhost.ico")));
    }

    @Test
    void testHeaderFaviconObtainStrategy_slashed_ico() throws IOException {
        String page = IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_slashed_ico.html"),
                "UTF-8");
        wireMockServer.stubFor(get(urlEqualTo("/dummy/site")).willReturn(aResponse().withStatus(200).withBody(page)));

        byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.ico"));
        wireMockServer.stubFor(get(urlEqualTo("/imgadr/mockFavicon.ico")).willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "image/x-icon").withBody(favicon)));

        FileSystem fs = fileSystemService.getFileSystem();
        Path outputDir = prepareFS(fs);

        HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy(new FaviconCache());
        String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
        assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);

        assertTrue(Files.exists(outputDir));
        assertTrue(Files.exists(outputDir.resolve("localhost.ico")));
    }

    @Test
    void testHeaderFaviconObtainStrategy_slashed2_ico() throws IOException {
        String page = IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_slashed2_ico.html"),
                "UTF-8");
        wireMockServer.stubFor(get(urlEqualTo("/dummy/site")).willReturn(aResponse().withStatus(200).withBody(page)));

        byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.ico"));
        wireMockServer.stubFor(get(urlEqualTo("/imgadr/mockFavicon.ico")).willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "image/x-icon").withBody(favicon)));

        FileSystem fs = fileSystemService.getFileSystem();
        Path outputDir = prepareFS(fs);

        HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy(new FaviconCache());
        String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
        assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);

        assertTrue(Files.exists(outputDir));
        assertTrue(Files.exists(outputDir.resolve("localhost.ico")));
    }
}