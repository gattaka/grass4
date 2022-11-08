package cz.gattserver.grass.articles.plugins.favlink.strategies;

import cz.gattserver.grass.articles.plugins.favlink.FaviconCache;
import cz.gattserver.grass.articles.plugins.favlink.FaviconUtils;

/**
 * Strategie, která získává favicony z cache již stažených ikon.
 * 
 * @author Hynek
 *
 */
public class CacheFaviconObtainStrategy implements FaviconObtainStrategy {

	private FaviconCache cache;

	public CacheFaviconObtainStrategy(FaviconCache cache) {
		this.cache = cache;
	}

	@Override
	public String obtainFaviconURL(String pageAddress, String contextRoot) {
		String faviconFilename = cache.getFavicon(pageAddress);
		if (faviconFilename != null)
			return FaviconUtils.createCachedFaviconAddress(contextRoot, faviconFilename);
		return null;
	}

}
