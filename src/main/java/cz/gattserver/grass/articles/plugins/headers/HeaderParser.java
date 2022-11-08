package cz.gattserver.grass.articles.plugins.headers;

import cz.gattserver.grass.articles.editor.lexer.Token;
import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass.articles.editor.parser.elements.Element;
import cz.gattserver.grass.articles.editor.parser.exceptions.TokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gatt
 */
public class HeaderParser implements Parser {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private int level;
	private String tag;

	public HeaderParser(int level, String tag) {
		this.level = level;
		this.tag = tag;
	}

	@Override
	public Element parse(ParsingProcessor pluginBag) {

		// zpracovat počáteční tag
		String startTag = pluginBag.getStartTag();
		logger.debug("{}", pluginBag.getToken());

		if (!startTag.equals(tag))
			throw new TokenException(tag, startTag);

		// START_tag byl zpracován
		pluginBag.nextToken();

		// zpracovat text
		List<Element> elist = new ArrayList<>();
		pluginBag.getBlock(elist, tag);
		// nextToken() - je již voláno v text() !!!

		// zpracovat koncový tag
		String endTag = pluginBag.getEndTag();
		if (!endTag.equals(tag))
			throw new TokenException(tag, endTag);

		// END_tag byl zpracován
		pluginBag.nextToken();

		// protože H je zalamovací ignoruje se případný <br/>
		while (pluginBag.getToken().equals(Token.EOL))
			pluginBag.nextToken();

		return new HeaderElement(elist, level);
	}
}
