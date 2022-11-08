package cz.gattserver.grass.articles.plugins.favlink.strategies;

import cz.gattserver.grass.articles.plugins.favlink.FaviconCache;
import cz.gattserver.grass.articles.plugins.favlink.FaviconUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

/**
 * Strategie, která získává favicon adresu pomocí parsování HTML stránky a
 * hledání odkazů na favicon soubor.
 * 
 * @author gatt
 */
public class HeaderFaviconObtainStrategy implements FaviconObtainStrategy {

	private static final Logger logger = LoggerFactory.getLogger(HeaderFaviconObtainStrategy.class);
	private static final String HTTP_PREFIX_SHORT = "http:";
	private static final String HTTPS_PREFIX_SHORT = "https:";
	private static final String HTTP_PREFIX = "http://";
	private static final String HTTPS_PREFIX = "https://";

	private FaviconCache cache;

	public HeaderFaviconObtainStrategy(FaviconCache cache) {
		this.cache = cache;
	}

	@Override
	public String obtainFaviconURL(String pageURL, String contextRoot) {
		logger.info("Zkouším hledat v hlavičce");

		URL url = FaviconUtils.getPageURL(pageURL);
		String faviconAddress = findFaviconAddressOnPage(url);
		if (faviconAddress == null)
			return null;

		String faviconFilename = cache.downloadAndSaveFavicon(url, faviconAddress);
		if (faviconFilename != null)
			return FaviconUtils.createCachedFaviconAddress(contextRoot, faviconFilename);

		return null;
	}

	private String createFullFaviconAddress(String faviconAddress, String base, URL pageURL) {

		String rootURL = pageURL.getProtocol() + "://" + pageURL.getHost();
		if (pageURL.getPort() > 0)
			rootURL += ":" + pageURL.getPort();

		// je potřeba z Jsoup doc.baseUri(), protože to může být i vložená
		// stránka a tam se baseURI liší od počátečního
		// url.getHost() hlavní stránky

		String tryMsg = "Zkouším stáhnout favicon z: {}";

		logger.info("Favicon adresa nalezena na: {}", faviconAddress);
		if (faviconAddress.startsWith(HTTP_PREFIX) || faviconAddress.startsWith(HTTPS_PREFIX)) {
			// absolutní cesta pro favicon
			logger.info(tryMsg, faviconAddress);
			return faviconAddress;
		} else if (faviconAddress.startsWith("//")) {
			// absolutní cesta pro favicon, která místo 'http://' začíná jenom
			// '//' tahle to má například stackoverflow
			String prefix = rootURL.startsWith(HTTPS_PREFIX) ? HTTPS_PREFIX_SHORT : HTTP_PREFIX_SHORT;
			String faviconFullAddress = prefix + faviconAddress;
			logger.info(tryMsg, faviconFullAddress);
			return faviconFullAddress;
		} else if (faviconAddress.startsWith("/")) {
			// relativní cesta od kořene webu
			return rootURL + faviconAddress;
		} else {
			// relativní cesta od aktuální stránky
			String pathPart = pageURL.getPath();
			if (pathPart.contains("/"))
				pathPart = pathPart.substring(0, pathPart.lastIndexOf("/"));
			if (base != null) {
				if (base.startsWith(HTTP_PREFIX) || base.startsWith(HTTPS_PREFIX))
					return base + "/" + faviconAddress;
				pathPart += "/" + base;
			}
			String faviconFullAddress = rootURL + (pathPart.isEmpty() ? "" : pathPart)
					+ (faviconAddress.startsWith("/") ? "" : "/") + faviconAddress;
			logger.info(tryMsg, faviconFullAddress);
			return faviconFullAddress;
		}
	}

	private String findFaviconAddressOnPage(URL pageURL) {
		Document doc;

		try {
			// http://en.wikipedia.org/wiki/Favicon
			// need http protocol
			// bez agenta to často hodí 403 Forbidden, protože si myslí, že jsem
			// asi bot ... (což vlastně jsem)
			doc = Jsoup.connect(pageURL.toString()).userAgent("Mozilla").get();

			String ico;
			String base = null;

			// Existuje base?
			// https://stackoverflow.com/questions/24028561/relative-path-in-html
			// https://www.w3schools.com/tags/tag_base.asp
			Element element = doc.head().select("base[href]").first();
			if (element != null)
				base = element.attr("href");

			// link ICO (upřednostňuj)
			logger.info("Zkouším ICO hlavičku");
			element = doc.head().select("link[href~=.*\\.ico]").first();
			if (element != null) {
				ico = element.attr("href");
				if (StringUtils.isNotBlank(ico))
					return createFullFaviconAddress(ico, base, pageURL);
			}

			// link rel=icon
			logger.info("Zkouším rel=icon hlavičku");
			element = doc.head().select("link[rel~=icon]").first();
			if (element != null) {
				ico = element.attr("href");
				if (StringUtils.isNotBlank(ico))
					return createFullFaviconAddress(ico, base, pageURL);
			}

			// link PNG
			logger.info("Zkouším PNG hlavičku");
			element = doc.head().select("link[href~=.*\\.png]").first();
			if (element != null) {
				ico = element.attr("href");
				if (StringUtils.isNotBlank(ico))
					return createFullFaviconAddress(ico, base, pageURL);
			}

			// meta + content
			logger.info("Zkouším META content");
			element = doc.head().select("meta[itemprop=image]").first();
			if (element != null) {
				ico = element.attr("content");
				if (StringUtils.isNotBlank(ico))
					return createFullFaviconAddress(ico, base, pageURL);
			}

		} catch (IOException e) {
			logger.error("Nezdařilo se získat stránku z pageURL: {}", pageURL);
		}

		logger.info("Nezdařilo se získat favicon z: {}", pageURL);
		return null;
	}

}
