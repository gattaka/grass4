package cz.gattserver.grass.articles.plugins.favlink.strategies;

/**
 * Rozhraní společné pro všechny strategie pro získávání favicon ikony dle URL
 * adresy
 * 
 * @author Hynek
 *
 */
public interface FaviconObtainStrategy {

	/**
	 * Získá favicon dle stránky
	 * 
	 * @param pageURL
	 *            URL stránky, kterou má strategie zpracovat pro získání její
	 *            favicon
	 * @param contextRoot
	 *            contextRoot v rámci kterého se mají generovat interní linky
	 * @return URL favicony, kterou strategie získala, nebo <code>null</code>
	 */
	String obtainFaviconURL(String pageURL, String contextRoot);

}
