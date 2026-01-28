package cz.gattserver.grass.core.modules.register;

import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;

public interface PageFactoriesRegister {

	/**
	 * Dělá prakticky to samé jako původní get, až na to, že pakliže není
	 * nalezena factory pro daný klíč, je vrácena factory homepage
	 * 
	 * @param key
	 *            klíč, pod kterým je hledaná factory zaregistrována
	 * @return factory nebo <code>null</code>
	 */
	public PageFactory get(String key);

	/**
	 * Původní put metoda - má prakticky jediné použití a tím je tvorba aliasů
	 * 
	 * @param pageName
	 *            název stránky pro kterou je registrován alias
	 * @param factory
	 *            factory stránky
	 * @return zaregistrovaná factory stránky
	 */
	public PageFactory putAlias(String pageName, PageFactory factory);

}
