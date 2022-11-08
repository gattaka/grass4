package cz.gattserver.grass.articles.plugins.favlink.strategies;

import cz.gattserver.grass.articles.plugins.favlink.FaviconCache;
import cz.gattserver.grass.articles.plugins.favlink.FaviconUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * Strategie, která získává favicony dle předpokladu, že favicon se nachází vždy
 * v root webu a jmenuje se favicon.ico nebo favicon.png
 * 
 * @author Hynek
 *
 */
public class AddressFaviconObtainStrategy implements FaviconObtainStrategy {

	private static final Logger logger = LoggerFactory.getLogger(AddressFaviconObtainStrategy.class);
	private static final String FAVICON_ICO = "favicon.ico";
	private static final String FAVICON_PNG = "favicon.png";

	private FaviconCache cache;

	public AddressFaviconObtainStrategy(FaviconCache cache) {
		this.cache = cache;
	}

	@Override
	public String obtainFaviconURL(String pageURL, String contextRoot) {
		URL url = FaviconUtils.getPageURL(pageURL);
		String address = url.getProtocol() + "://" + url.getHost();
		if (url.getPort() > 0)
			address += ":" + url.getPort();
		String faviconFilename;

		// root + /favicon.ico
		logger.info("Trying favicon.ico");
		faviconFilename = cache.downloadAndSaveFavicon(url, address + "/" + FAVICON_ICO);
		if (faviconFilename != null)
			return FaviconUtils.createCachedFaviconAddress(contextRoot, faviconFilename);

		// root + /favicon.png
		faviconFilename = cache.downloadAndSaveFavicon(url, address + "/" + FAVICON_PNG);
		if (faviconFilename != null)
			return FaviconUtils.createCachedFaviconAddress(contextRoot, faviconFilename);

		return null;
	}
}
