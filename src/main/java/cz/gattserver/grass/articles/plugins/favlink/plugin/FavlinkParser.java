package cz.gattserver.grass.articles.plugins.favlink.plugin;


import cz.gattserver.grass.articles.plugins.favlink.strategies.FaviconObtainStrategy;
import cz.gattserver.grass.articles.editor.lexer.Token;
import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass.articles.editor.parser.elements.Element;
import cz.gattserver.grass.articles.editor.parser.exceptions.TokenException;

/**
 * @author gatt
 */
public class FavlinkParser implements Parser {

	private String tag;
	private FaviconObtainStrategy strategy;

	public FavlinkParser(String tag, FaviconObtainStrategy strategy) {
		this.tag = tag;
		this.strategy = strategy;
	}

	@Override
	public Element parse(ParsingProcessor parsingProcessor) {

		// zpracovat počáteční tag
		String startTag = parsingProcessor.getStartTag();

		if (!startTag.equals(tag))
			throw new TokenException(tag, startTag);

		// START_TAG byl zpracován
		parsingProcessor.nextToken();

		// zpracovat text - musím zahazovat anotace pozic, střetly by se
		StringBuilder builder = new StringBuilder();
		while ((parsingProcessor.getToken() != Token.END_TAG || !parsingProcessor.getEndTag().equals(tag))
				&& parsingProcessor.getToken() != Token.EOF) {
			builder.append(parsingProcessor.getText());
			parsingProcessor.nextToken();
		}
		String text = builder.toString();

		String pageURL;
		String description = "";
		int lastSpace = text.trim().lastIndexOf("http");
		if (lastSpace > 0) {
			description = text.substring(0, lastSpace).trim();
			pageURL = text.substring(lastSpace);
		} else {
			pageURL = text;
		}

		String faviconURL = null;
		faviconURL = strategy.obtainFaviconURL(pageURL, parsingProcessor.getContextRoot());

		// zpracovat koncový tag
		// END_TAG byl zpracován
		parsingProcessor.getEndTag();
		parsingProcessor.nextToken();

		return new FavlinkElement(faviconURL, description, pageURL);
	}
}
