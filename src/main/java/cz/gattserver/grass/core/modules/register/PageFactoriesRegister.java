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
	PageFactory get(String key);

}