package cz.gattserver.grass.articles.plugins.basic.table;

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
public class TableParser implements Parser {

	private String tag;
	private boolean withHead;

	/**
	 * Kolik bylo zatím max sloupec ? A kolik je aktuálně sloupců ?
	 */
	private int maxCols = 1;
	private int colsSoFar = 0;

	public TableParser(String tag, boolean withHead) {
		this.withHead = withHead;
		this.tag = tag;
	}

	@Override
	public Element parse(ParsingProcessor pluginBag) {
		// zpracovat počáteční tag
		String startTag = pluginBag.getStartTag();

		if (!startTag.equals(tag))
			throw new TokenException(tag, startTag);

		// START_TAG byl zpracován
		pluginBag.nextToken();

		// Řádky
		List<List<List<Element>>> rows = new ArrayList<>();
		// Buňky v řádku
		List<List<Element>> cells = new ArrayList<>();
		// Elementy ve buňce (může být více elementů vedle sebe)
		List<Element> elements = new ArrayList<>();
		// Nemůžu volat blok, protože ten končí až na mém koncovém tagu, kdežto
		// já potřebuju odlišit položky tabulky přes tabulátory a konce řádků
		while (pluginBag.getToken() != Token.END_TAG || !pluginBag.getEndTag().equals(tag)) {
			switch (pluginBag.getToken()) {
			// V elementu listu můžou být jiné pluginy
			case START_TAG:
				elements.add(pluginBag.getElement());
				break;
			// Jinak to načítám jako text
			case END_TAG:
			case TEXT:
				elements.add(pluginBag.getTextTree());
				break;
			// Tabulátor značí konec buňky tabulky
			case TAB:
				colsSoFar++;
				cells.add(elements);
				elements = new ArrayList<>();
				pluginBag.nextToken();
				break;
			// Konec řádku značí konec řádku tabulky
			case EOL:
				if (!elements.isEmpty()) {
					colsSoFar++;
					maxCols = colsSoFar > maxCols ? colsSoFar : maxCols;
					cells.add(elements);
				}
				if (!cells.isEmpty()) {
					rows.add(cells);
				}
				colsSoFar = 0;
				elements = new ArrayList<>();
				cells = new ArrayList<>();
				pluginBag.nextToken();
				break;
			case EOF:
				throw new TokenException(
						new Token[] { Token.START_TAG, Token.END_TAG, Token.TEXT, Token.TAB, Token.EOL });
			}
		}

		// po ukončení může být ještě něco nepřidáno - přidej to
		if (!elements.isEmpty()) {
			colsSoFar++;
			maxCols = colsSoFar > maxCols ? colsSoFar : maxCols;
			cells.add(elements);
		}

		if (!cells.isEmpty()) {
			rows.add(cells);
		}

		// kontrola koncového tagu není potřeba, je již proveden v rámci
		// předchozího cyklu

		// END_TAG byl zpracován
		pluginBag.nextToken();

		// protože za tabulkou je mezera ignoruje se případný <br/>
		if (pluginBag.getToken().equals(Token.EOL))
			pluginBag.nextToken();

		return new TableElement(rows, withHead, maxCols);
	}
}
