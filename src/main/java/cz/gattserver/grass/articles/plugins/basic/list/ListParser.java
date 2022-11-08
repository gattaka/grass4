package cz.gattserver.grass.articles.plugins.basic.list;

import cz.gattserver.grass.articles.editor.lexer.Token;
import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass.articles.editor.parser.elements.Element;
import cz.gattserver.grass.articles.editor.parser.exceptions.TokenException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gatt
 */
public class ListParser implements Parser {

	private String tag;
	private boolean ordered;

	public ListParser(String tag, boolean ordered) {
		this.ordered = ordered;
		this.tag = tag;
	}

	public Element parse(ParsingProcessor pluginBag) {

		// zpracovat počáteční tag
		String startTag = pluginBag.getStartTag();

		if (!startTag.equals(tag))
			throw new TokenException(tag, startTag);

		// START_TAG byl zpracován
		pluginBag.nextToken();

		// Zpracovat položek listu, nemůžu volat blok, protože ten končí až na
		// mém koncovém tagu, kdežto já potřebuju odlišit položky listu konci
		// řádků
		List<List<Element>> listElements = new ArrayList<>();
		List<Element> elist = new ArrayList<>();
		while (pluginBag.getToken() != Token.END_TAG || !pluginBag.getEndTag().equals(tag)) {
			switch (pluginBag.getToken()) {
			// V elementu listu můžou být jiné pluginy
			case START_TAG:
				elist.add(pluginBag.getElement());
				break;
			// Jinak to načítám jako text
			case END_TAG:
			case TAB:
			case TEXT:
				elist.add(pluginBag.getTextTree());
				break;
			// Konec řádku značí konec položky listu
			case EOL:
				listElements.add(elist);
				elist = new ArrayList<>();
				pluginBag.nextToken();
				break;
			case EOF:
				throw new TokenException(
						new Token[] { Token.START_TAG, Token.END_TAG, Token.TEXT, Token.TAB, Token.EOL });
			}
		}

		// po ukončení může být ještě něco nepřidáno - přidej to
		if (!elist.isEmpty())
			listElements.add(elist);

		// kontrola koncového tagu není potřeba, je již proveden v rámci
		// předchozího cyklu

		// END_TAG byl zpracován
		pluginBag.nextToken();

		// protože za listem je mezera ignoruje se případný <br/>
		if (pluginBag.getToken().equals(Token.EOL))
			pluginBag.nextToken();

		return new ListElement(listElements, ordered);
	}
}
