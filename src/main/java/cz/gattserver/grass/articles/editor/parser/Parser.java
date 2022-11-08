package cz.gattserver.grass.articles.editor.parser;

import cz.gattserver.grass.articles.editor.parser.elements.Element;

/**
 * @author gatt
 */
public interface Parser {

	/**
	 * Projde článek a vytvoří {@link Element}
	 * 
	 * @param pluginBag
	 *            objekt s daty, která je předáván mezi parser pluginy
	 * @return {@link Element} AST strom pro finální generování výsledného
	 *         článku
	 */
	Element parse(ParsingProcessor pluginBag);

}
