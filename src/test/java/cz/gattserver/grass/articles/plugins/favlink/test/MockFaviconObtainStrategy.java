package cz.gattserver.grass.articles.plugins.favlink.test;

import cz.gattserver.grass.articles.plugins.favlink.strategies.FaviconObtainStrategy;

public class MockFaviconObtainStrategy implements FaviconObtainStrategy {

	@Override
	public String obtainFaviconURL(String pageURL, String contextRoot) {
		return "http://mock.neco/favicon.png";
	}

}
