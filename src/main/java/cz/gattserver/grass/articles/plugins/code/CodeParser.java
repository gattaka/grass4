package cz.gattserver.grass.articles.plugins.code;

import cz.gattserver.grass.articles.editor.lexer.Token;
import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass.articles.editor.parser.elements.Element;
import cz.gattserver.grass.articles.editor.parser.exceptions.TokenException;
import cz.gattserver.grass.articles.util.HTMLEscaper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author gatt
 */
public class CodeParser implements Parser {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String tag;
	private String description;
	private String lib;
	private String mimetype;

	public CodeParser(String tag, String description, String lib, String mimetype) {
		this.tag = tag;
		this.description = description;
		this.lib = lib;
		this.mimetype = mimetype;
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
		Token lastToken = null;
		Token currentToken = null;
		while (true) {
			currentToken = pluginBag.getToken();
			if ((Token.END_TAG.equals(currentToken) && pluginBag.getEndTag().equals(tag))
					|| Token.EOF.equals(currentToken))
				break;
			// Pokud načteš TEXT, tak přidej jeho obsah, pokud pak načtečeš EOL,
			// tak nepřidávej prázdný řádek, ledaže by jsi načetl EOL EOL - pak
			// je to prázdný řádek
			if (Token.TEXT.equals(currentToken) || Token.END_TAG.equals(currentToken)
					|| Token.START_TAG.equals(currentToken)) {
				code.append(HTMLEscaper.stringToHTMLString(pluginBag.getCode()));
			} else if (Token.TAB.equals(currentToken)) {
				code.append('\t');
			} else if (Token.EOL.equals(currentToken)) {
				// prázdné řádky je potřeba prokládat mezerou, kterou si JS záhy
				// uzavře do <p></p> elementů - bez této mezery by <p></p>
				// element neudělal odřádkování nutné a zmizeli by tak prázdné
				// řádky - po stránce korektnosti zpětného čtení z webu je vše v
				// pořádku, protože <p></p> přebytečnou mezeru záhy zase vynechá
				// ...
				if (lastToken == currentToken)
					code.append(" ");
				code.append('\n');
			}
			pluginBag.nextToken();
			lastToken = currentToken;
		}

		// zpracovat koncový tag
		// END_TAG byl zpracován, ověř, že to byl můj ukončovací tag
		pluginBag.getEndTag();
		pluginBag.nextToken();

		// protože za CODE je většinou mezera ignoruje se případný <br/>
		if (pluginBag.getToken().equals(Token.EOL))
			pluginBag.nextToken();

		// position 1, position 2, link odkazu, text odkazu (optional), ikona
		// (optional), default ikona
		return new CodeElement(code.toString(), description, lib, mimetype);
	}
}