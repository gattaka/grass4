package cz.gattserver.grass.articles.plugins.templates.sources;

import cz.gattserver.grass.articles.editor.lexer.Token;
import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass.articles.editor.parser.elements.Element;
import cz.gattserver.grass.articles.editor.parser.exceptions.ParserException;
import cz.gattserver.grass.articles.plugins.favlink.strategies.FaviconObtainStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gatt
 */
public class SourcesParser implements Parser {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String tag;
	private List<String> faviconURLs = new ArrayList<>();
	private List<String> descriptions = new ArrayList<>();
	private List<String> pageURLs = new ArrayList<>();
	private FaviconObtainStrategy strategy;
	private boolean header;
	private boolean numbers;

	public SourcesParser(String tag, FaviconObtainStrategy strategy, boolean header, boolean numbers) {
		this.tag = tag;
		this.strategy = strategy;
		this.header = header;
		this.numbers = numbers;
	}

	@Override
	public Element parse(ParsingProcessor pluginBag) {
		// zpracovat počáteční tag
		parseStartTag(pluginBag);

		// zpracuje zdroje
		parseSources(pluginBag);

		// zpracovat koncový tag
		parseEndTag(pluginBag);

		// ignoruje se případný <br/>
		if (pluginBag.getToken().equals(Token.EOL))
			pluginBag.nextToken();

		return new SourcesElement(faviconURLs, descriptions, pageURLs, header, numbers);
	}

	private void parseStartTag(ParsingProcessor pluginBag) {
		String startTag = pluginBag.getStartTag();

		if (!startTag.equals(tag)) {
			logger.warn("Čekal jsem: [{}] ne {}", tag, startTag);
			throw new ParserException();
		}

		pluginBag.nextToken();
	}

	private void parseSources(ParsingProcessor parsingProcessor) {
		// dokud není konec souboru nebo můj uzavírací tag
		StringBuilder builder = new StringBuilder();
		while (parsingProcessor.getToken() != Token.EOF
				&& (parsingProcessor.getToken() != Token.END_TAG || !parsingProcessor.getEndTag().equals(tag))) {
			if (parsingProcessor.getToken() == Token.EOL && builder.length() != 0) {
				processText(builder.toString(), parsingProcessor);
				builder = new StringBuilder();
			} else {
				String text = parsingProcessor.getText();
				if (text != null && !text.isEmpty())
					builder.append(text);
			}
			parsingProcessor.nextToken();
		}
		if (builder.length() != 0)
			processText(builder.toString(), parsingProcessor);
	}

	private void processText(String text, ParsingProcessor parsingProcessor) {
		String pageURL;
		String description = "";
		int lastSpace = text.trim().lastIndexOf(' ');
		if (lastSpace > 0) {
			description = text.substring(0, lastSpace);
			pageURL = text.substring(lastSpace + 1);
		} else {
			pageURL = text;
		}

		String faviconURL = strategy.obtainFaviconURL(pageURL, parsingProcessor.getContextRoot());
		pageURLs.add(pageURL);
		descriptions.add(description);
		faviconURLs.add(faviconURL);
	}

	private void parseEndTag(ParsingProcessor pluginBag) {
		String endTag = pluginBag.getEndTag();

		if (!endTag.equals(tag)) {
			logger.warn("Čekal jsem: [/{}] ne {}", tag, pluginBag.getCode());
			throw new ParserException();
		}

		pluginBag.nextToken();
	}

}
