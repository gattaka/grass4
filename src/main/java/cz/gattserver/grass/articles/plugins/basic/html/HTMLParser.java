package cz.gattserver.grass.articles.plugins.basic.html;

import cz.gattserver.grass.articles.editor.lexer.Token;
import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass.articles.editor.parser.elements.Element;
import cz.gattserver.grass.articles.editor.parser.exceptions.TokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author gatt
 */
public class HTMLParser implements Parser {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String tag;

	public HTMLParser(String tag) {
		this.tag = tag;
	}

	@Override
	public Element parse(ParsingProcessor pluginBag) {

		// zpracovat počáteční tag
		String startTag = pluginBag.getStartTag();
		logger.debug(pluginBag.getToken().toString());

		if (!startTag.equals(tag)) {
			logger.warn("Čekal jsem: [" + tag + "] ne " + startTag);
			throw new TokenException(tag, startTag);
		}

		// START_TAG byl zpracován
		pluginBag.nextToken();

		StringBuilder code = new StringBuilder();

		// Zpracuje vnitřek tagů jako code - jedu dokud nenarazím na svůj
		// koncový tag - všechno ostatní beru jako obsah zdrojáku - text i
		// potenciální počáteční tagy. Jediná věc, která mne může zastavit je
		// EOF nebo můj koncový tag. Načítám text po řádcích protože chci
		// zachovat řádkování kódu. Jinak kód by měl být escapován.
		Token currentToken = null;
		while (true) {
			currentToken = pluginBag.getToken();
			if ((Token.END_TAG.equals(currentToken) && pluginBag.getEndTag().equals(tag))
					|| Token.EOF.equals(currentToken))
				break;
			if (Token.EOL.equals(currentToken)) {
				code.append("\n");
			} else {
				code.append(pluginBag.getCode());
			}
			pluginBag.nextToken();
		}

		// zpracovat koncový tag
		// END_TAG byl zpracován, ověř, že to byl můj ukončovací tag
		pluginBag.getEndTag();
		pluginBag.nextToken();

		// protože za CODE je většinou mezera ignoruje se případný <br/>
		if (pluginBag.getToken().equals(Token.EOL))
			pluginBag.nextToken();

		return new HTMLElement(code.toString());
	}
}
