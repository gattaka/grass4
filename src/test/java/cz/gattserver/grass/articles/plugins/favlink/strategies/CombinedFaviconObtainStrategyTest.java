package cz.gattserver.grass.articles.plugins.favlink.strategies;

import cz.gattserver.grass.articles.plugins.favlink.FaviconCache;
import cz.gattserver.grass.articles.plugins.favlink.test.StrategyTest;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class CombinedFaviconObtainStrategyTest extends StrategyTest {

    @Test
    void testCombinedFaviconObtainStrategyTest_empty() throws IOException {
        String page = IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_empty.html"), "UTF-8");
        wireMockServer.stubFor(get(urlEqualTo("/dummy/site"))
                .willReturn(aResponse().withStatus(200).withBody(page)));

        FileSystem fs = fileSystemService.getFileSystem();
        prepareFS(fs);

        FaviconObtainStrategy strategy = new CombinedFaviconObtainStrategy();
        String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
        assertEquals("mycontextroot/articles/favlink/default.ico", link);
    }

    @Test
    void testCombinedFaviconObtainStrategyTest_cached() throws IOException {
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
    void testCombinedFaviconObtainStrategyTest_address() throws IOException {
        byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.png"));
        wireMockServer.stubFor(get(urlEqualTo("/favicon.png"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "image/png").withBody(favicon)));

        FileSystem fs = fileSystemService.getFileSystem();
        prepareFS(fs);

        FaviconObtainStrategy strategy = new CombinedFaviconObtainStrategy();
        String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
        assertEquals("mycontextroot/articles-favlink-plugin/localhost.png", link);
    }

    @Test
    void testCombinedFaviconObtainStrategyTest_header() throws IOException {
        String page = IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_http_png.html"), "UTF-8");
        wireMockServer.stubFor(get(urlEqualTo("/dummy/site"))
                .willReturn(aResponse().withStatus(200).withBody(page)));

        byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.png"));
        wireMockServer.stubFor(get(urlEqualTo("/imgadr/mockFavicon.png"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "image/png").withBody(favicon)));

        FileSystem fs = fileSystemService.getFileSystem();
        prepareFS(fs);

        FaviconObtainStrategy strategy = new CombinedFaviconObtainStrategy();
        String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
        assertEquals("mycontextroot/articles-favlink-plugin/localhost.png", link);
    }
}