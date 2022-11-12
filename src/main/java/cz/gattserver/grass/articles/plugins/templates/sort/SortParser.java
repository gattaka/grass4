package cz.gattserver.grass.articles.plugins.templates.sort;

import cz.gattserver.grass.articles.editor.lexer.Token;
import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass.articles.editor.parser.elements.Element;
import cz.gattserver.grass.articles.editor.parser.elements.TextElement;
import cz.gattserver.grass.articles.editor.parser.exceptions.TokenException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gatt
 */
public class SortParser implements Parser {

	private String tag;

	public SortParser(String tag) {
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
		List<SortElementsLine> listElements = new ArrayList<>();
		SortElementsLine line = new SortElementsLine();
		while (pluginBag.getToken() != Token.END_TAG || !pluginBag.getEndTag().equals(tag)) {
			switch (pluginBag.getToken()) {
			// V elementu listu můžou být jiné pluginy
			case START_TAG:
				line.getElements().add(pluginBag.getElement());
				break;
			// Jinak to načítám jako text
			case END_TAG:
			case TAB:
			case TEXT:
				TextElement textElement = pluginBag.getTextTree();
				if (line.getComparable() == null)
					line.setComparable(textElement.getText());
				line.getElements().add(textElement);
				break;
			// Konec řádku značí konec položky listu
			case EOL:
				listElements.add(line);
				// pokud nebyl uvozovací řetězec, dle kterého se musí řadit,
				// projdi elementy a najdi první text, který se na tohle použije
				if (line.getComparable() == null)
					line.setComparable(findText(line.getElements()));
				line = new SortElementsLine();
				pluginBag.nextToken();
				break;
			case EOF:
				throw new TokenException(
						new Token[] { Token.START_TAG, Token.END_TAG, Token.TEXT, Token.TAB, Token.EOL });
			}
		}

		// po ukončení může být ještě něco nepřidáno - přidej to
		if (!line.getElements().isEmpty()) {
			listElements.add(line);
			if (line.getComparable() == null)
				line.setComparable(findText(line.getElements()));
		}

		// kontrola koncového tagu není potřeba, je již proveden v rámci
		// předchozího cyklu

		// END_TAG byl zpracován
		pluginBag.nextToken();

		// protože za listem je mezera ignoruje se případný <br/>
		if (pluginBag.getToken().equals(Token.EOL))
			pluginBag.nextToken();

		return new SortElement(listElements);
	}

	private String findText(List<Element> elements) {
		for (Element e : elements) {
			if (e instanceof TextElement) {
				return ((TextElement) e).getText();
			} else {
				String val = findText(e.getSubElements());
				if (StringUtils.isNotBlank(val))
					return val;
			}
		}
		return null;
	}

}
