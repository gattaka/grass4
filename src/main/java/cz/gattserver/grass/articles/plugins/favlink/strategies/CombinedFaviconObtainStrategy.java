package cz.gattserver.grass.articles.plugins.favlink.strategies;

import cz.gattserver.grass.articles.plugins.favlink.FaviconCache;
import cz.gattserver.grass.articles.plugins.favlink.FaviconUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategie kombinující ostatníc strategie v pořadí dle náročnosti.
 * 
 * @author gatt
 */
public class CombinedFaviconObtainStrategy implements FaviconObtainStrategy {

	@Override
	public String obtainFaviconURL(String pageURL, String contextRoot) {
		FaviconCache cache = new FaviconCache();
		List<FaviconObtainStrategy> strategies = new ArrayList<>();
		strategies.add(new CacheFaviconObtainStrategy(cache));
		strategies.add(new HeaderFaviconObtainStrategy(cache));
		strategies.add(new AddressFaviconObtainStrategy(cache));

		for (FaviconObtainStrategy s : strategies) {
			String faviconURL = s.obtainFaviconURL(pageURL, contextRoot);
			if (faviconURL != null)
				return faviconURL;
		}

		return FaviconUtils.createDefaultFaviconAddress(contextRoot);
	}

}
