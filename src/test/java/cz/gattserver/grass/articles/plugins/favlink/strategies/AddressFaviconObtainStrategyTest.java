package cz.gattserver.grass.articles.plugins.favlink.strategies;

import cz.gattserver.grass.articles.plugins.favlink.FaviconCache;
import cz.gattserver.grass.articles.plugins.favlink.test.StrategyTest;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class AddressFaviconObtainStrategyTest extends StrategyTest {

    @Test
    public void testAddressFaviconObtainStrategy_empty() throws IOException {
        FileSystem fs = fileSystemService.getFileSystem();
        prepareFS(fs);

        AddressFaviconObtainStrategy strategy = new AddressFaviconObtainStrategy(new FaviconCache());
        String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
        assertNull(link);
    }

    @Test
    public void testAddressFaviconObtainStrategy_png() throws IOException {
        byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.png"));

        wireMockServer.stubFor(get(urlEqualTo("/favicon.png")).willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "image/png").withBody(favicon)));

        FileSystem fs = fileSystemService.getFileSystem();
        Path outputDir = prepareFS(fs);

        AddressFaviconObtainStrategy strategy = new AddressFaviconObtainStrategy(new FaviconCache());
        String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
        assertEquals("mycontextroot/articles-favlink-plugin/localhost.png", link);

        assertTrue(Files.exists(outputDir));
        assertTrue(Files.exists(outputDir.resolve("localhost.png")));
    }

    @Test
    public void testAddressFaviconObtainStrategy_ico() throws IOException {
        byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.ico"));

        FileSystem fs = fileSystemService.getFileSystem();
        Path outputDir = prepareFS(fs);

        wireMockServer.stubFor(get(urlEqualTo("/favicon.ico")).willReturn(
                aResponse().withStatus(200).withBody(favicon)));

        AddressFaviconObtainStrategy strategy = new AddressFaviconObtainStrategy(new FaviconCache());
        String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
        assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);

        assertTrue(Files.exists(outputDir));
        assertTrue(Files.exists(outputDir.resolve("localhost.ico")));
    }
}