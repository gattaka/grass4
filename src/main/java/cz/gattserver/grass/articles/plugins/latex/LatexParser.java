package cz.gattserver.grass.articles.plugins.latex;

import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.lexer.Token;
import cz.gattserver.grass.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass.articles.editor.parser.elements.Element;
import cz.gattserver.grass.articles.editor.parser.exceptions.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gatt
 */
public class LatexParser implements Parser {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String tag;

	public LatexParser(String tag) {
		this.tag = tag;
	}

	protected String processFormula(ParsingProcessor pluginBag) {
		StringBuilder formulaBuilder = new StringBuilder();

		/**
		 * Zpracuje vnitřek tagů jako code - jedu dokud nenarazím na svůj
		 * koncový tag - všechno ostatní beru jako obsah latex zdrojáku - text i
		 * potenciální počáteční tagy. Jediná věc, která mne může zastavit je
		 * EOF nebo můj koncový tag.
		 */
		while (true) {
			if ((pluginBag.getToken() == Token.END_TAG && pluginBag.getEndTag().equals(tag))
					|| pluginBag.getToken() == Token.EOF)
				break;
			formulaBuilder.append(pluginBag.getCodeTextTree().getText());
		}

		return formulaBuilder.toString();
	}

	@Override
	public Element parse(ParsingProcessor pluginBag) {

		// zpracovat počáteční tag
		String startTag = pluginBag.getStartTag();

		if (!startTag.equals(tag)) {
			logger.warn("Čekal jsem: [" + tag + "] ne " + startTag);
			throw new ParserException();
		}

		// START_TAG byl zpracován
		pluginBag.nextToken();

		String formula = processFormula(pluginBag);

		// zpracovat koncový tag
		String endTag = pluginBag.getEndTag();
		logger.debug(pluginBag.getToken().toString());

		if (!endTag.equals(tag)) {
			logger.warn("Čekal jsem: [/" + tag + "], ne " + pluginBag.getCode());
			throw new ParserException();
		}

		// END_TAG byl zpracován
		pluginBag.nextToken();

		return new LatexElement(formula);
	}
}
