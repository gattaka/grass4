package cz.gattserver.grass.articles.plugins.favlink;

import cz.gattserver.grass.articles.editor.parser.exceptions.ParserException;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class FaviconUtilsTest {

    @Test
    public void testCreateCachedFaviconAddress() {
        assertEquals("/test-root/articles-favlink-plugin/favicon-name.png",
                FaviconUtils.createCachedFaviconAddress("/test-root", "favicon-name.png"));
    }

    @Test
    public void testCreateCachedFaviconAddress2() {
        assertEquals("/articles-favlink-plugin/favicon-name.png",
                FaviconUtils.createCachedFaviconAddress("", "favicon-name.png"));
    }

    @Test
    public void testCreateCachedFaviconAddress_fail() {
        assertThrows(NullPointerException.class,
                () -> FaviconUtils.createCachedFaviconAddress(null, "favicon-name.png"));
    }

    @Test
    public void testGetFaviconFilename() throws MalformedURLException, URISyntaxException {
        assertEquals("ro-che.info.png", FaviconUtils.getFaviconFilename(
                new URI("https://ro-che.info/articles/2017-03-26-increase-open-files-limit").toURL(),
                "https://ro-che.info/img/favicon.png?v=4"));
    }

    @Test
    public void testCreateCachedFaviconAddress_fail2() {
        assertThrows(IllegalArgumentException.class, () -> FaviconUtils.createCachedFaviconAddress("dd", ""));
    }

    @Test
    public void testCreateFaviconRootFilename() throws MalformedURLException, URISyntaxException {
        assertEquals("www.testweb.cz",
                FaviconUtils.createFaviconRootFilename(new URI("https://www.testweb.cz/").toURL()));
        assertEquals("testweb.cz", FaviconUtils.createFaviconRootFilename(new URI("https://testweb.cz/").toURL()));
        assertEquals("testweb.cz", FaviconUtils.createFaviconRootFilename(new URI("http://testweb.cz/").toURL()));
        assertEquals("testweb.cz", FaviconUtils.createFaviconRootFilename(new URI("http://testweb.cz").toURL()));
        assertEquals("www.testweb2.org", FaviconUtils.createFaviconRootFilename(
                new URI("https://www.testweb2.org/policie-evakuovala-mestsky-soud-ve-slezske-kvuli-nahlasene-bombe-p9f-/zpravy-domov.aspx?c=A171207_114412_ln_domov_ele#utm_source=rss&utm_medium=feed&utm_campaign=ln_testweb&utm_content=main").toURL()));
        assertEquals("localhost", FaviconUtils.createFaviconRootFilename(
                new URI("http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy").toURL()));
    }

    @Test
    public void testGetPageURL() throws MalformedURLException, URISyntaxException {
        assertEquals(new URI("https://www.testweb.cz/").toURL(), FaviconUtils.getPageURL("https://www.testweb.cz/"));
        assertEquals(new URI("http://www.testweb.cz/").toURL(), FaviconUtils.getPageURL("http://www.testweb.cz/"));
        assertEquals(
                new URI("http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy").toURL(),
                FaviconUtils.getPageURL(
                        "http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy"));
    }

    @Test
    public void testGetPageURL_fail() {
        assertThrows(ParserException.class, () -> FaviconUtils.getPageURL("www.testweb.cz"));
    }
}