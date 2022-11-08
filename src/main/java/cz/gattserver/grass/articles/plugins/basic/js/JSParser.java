package cz.gattserver.grass.articles.plugins.basic.js;

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
public class JSParser implements Parser {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String tag;

	public JSParser(String tag) {
		this.tag = tag;
	}

	@Override
	public Element parse(ParsingProcessor processor) {

		// zpracovat počáteční tag
		String startTag = processor.getStartTag();
		logger.debug(processor.getToken().toString());

		if (!startTag.equals(tag)) {
			logger.warn("Čekal jsem: [" + tag + "] ne " + startTag);
			throw new TokenException(tag, startTag);
		}

		// START_TAG byl zpracován
		processor.nextToken();

		StringBuilder code = new StringBuilder();

		// Zpracuje vnitřek tagů jako code - jedu dokud nenarazím na svůj
		// koncový tag - všechno ostatní beru jako obsah zdrojáku - text i
		// potenciální počáteční tagy. Jediná věc, která mne může zastavit je
		// EOF nebo můj koncový tag. Načítám text po řádcích protože chci
		// zachovat řádkování kódu. Jinak kód by měl být escapován.
		Token currentToken = null;
		while (true) {
			currentToken = processor.getToken();
			if ((Token.END_TAG.equals(currentToken) && processor.getEndTag().equals(tag))
					|| Token.EOF.equals(currentToken))
				break;
			if (Token.EOL.equals(currentToken)) {
				code.append("\n");
			} else {
				code.append(processor.getCode());
			}
			processor.nextToken();
		}

		// zpracovat koncový tag
		// END_TAG byl zpracován, ověř, že to byl můj ukončovací tag
		processor.getEndTag();

		// END_TAG byl zpracován
		processor.nextToken();

		// ignoruj případný <br/>
		if (processor.getToken().equals(Token.EOL))
			processor.nextToken();

		return new JSElement(code.toString());
	}
}
