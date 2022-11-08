package cz.gattserver.grass.articles.editor.parser.elements;

import cz.gattserver.grass.articles.editor.parser.Context;

import java.util.List;

public interface Element {

	/**
	 * Metoda k implementování skutečným stromem elementu, je volána hlavní
	 * generující metodou, která musí nejprve zapsan odkazující značky na
	 * zdrojový text článku
	 * 
	 * @param ctx
	 */
	void apply(Context ctx);

	/**
	 * Metoda pro procházení podstromu při post-procesingu
	 * 
	 * @return list elementů, nebo <code>null</code>
	 */
	List<Element> getSubElements();
}
