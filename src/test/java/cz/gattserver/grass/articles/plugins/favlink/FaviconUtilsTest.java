package cz.gattserver.grass.articles.plugins.favlink;

import cz.gattserver.grass.articles.editor.parser.exceptions.ParserException;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

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
        assertThrows(NullPointerException.class, () ->
                FaviconUtils.createCachedFaviconAddress(null, "favicon-name.png")
        );
    }

    @Test
    public void testGetFaviconFilename() throws MalformedURLException {
        assertEquals("ro-che.info.png",
                FaviconUtils.getFaviconFilename(
                        new URL("https://ro-che.info/articles/2017-03-26-increase-open-files-limit"),
                        "https://ro-che.info/img/favicon.png?v=4"));
    }

    @Test
    public void testCreateCachedFaviconAddress_fail2() {
        assertThrows(IllegalArgumentException.class, () -> FaviconUtils.createCachedFaviconAddress("dd", ""));
    }

    @Test
    public void testCreateFaviconRootFilename() throws MalformedURLException {
        assertEquals("www.testweb.cz", FaviconUtils.createFaviconRootFilename(new URL("https://www.testweb.cz/")));
        assertEquals("testweb.cz", FaviconUtils.createFaviconRootFilename(new URL("https://testweb.cz/")));
        assertEquals("testweb.cz", FaviconUtils.createFaviconRootFilename(new URL("http://testweb.cz/")));
        assertEquals("testweb.cz", FaviconUtils.createFaviconRootFilename(new URL("http://testweb.cz")));
        assertEquals("www.testweb2.org", FaviconUtils.createFaviconRootFilename(new URL(
                "https://www.testweb2.org/policie-evakuovala-mestsky-soud-ve-slezske-kvuli-nahlasene-bombe-p9f-/zpravy-domov.aspx?c=A171207_114412_ln_domov_ele#utm_source=rss&utm_medium=feed&utm_campaign=ln_testweb&utm_content=main")));
        assertEquals("localhost", FaviconUtils.createFaviconRootFilename(new URL(
                "http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy")));
    }

    @Test
    public void testGetPageURL() throws MalformedURLException {
        assertEquals(new URL("https://www.testweb.cz/"), FaviconUtils.getPageURL("https://www.testweb.cz/"));
        assertEquals(new URL("http://www.testweb.cz/"), FaviconUtils.getPageURL("http://www.testweb.cz/"));
        assertEquals(new URL(
                        "http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy"),
                FaviconUtils.getPageURL(
                        "http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy"));
    }

    @Test
    public void testGetPageURL_fail() throws MalformedURLException {
        assertThrows(ParserException.class, () -> FaviconUtils.getPageURL("www.testweb.cz"));
    }

}
