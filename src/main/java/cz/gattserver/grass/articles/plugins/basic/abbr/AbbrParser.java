package cz.gattserver.grass.articles.plugins.basic.abbr;

import cz.gattserver.grass.articles.editor.lexer.Token;
import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass.articles.editor.parser.elements.Element;
import cz.gattserver.grass.articles.editor.parser.exceptions.TokenException;

/**
 * @author gatt
 */
public class AbbrParser implements Parser {

	private String tag;
	private String titleTag;
	private String title;
	private String text;

	public AbbrParser(String tag, String titleTag) {
		this.tag = tag;
		this.titleTag = titleTag;
	}

	@Override
	public Element parse(ParsingProcessor processor) {
		// zpracovat počáteční tag
		parseStartTag(processor);

		// zpracuje zkratku (text)
		parseAbbreviation(processor);

		// zpracovat počáteční tag textu
		parseTextStartTag(processor);

		// zpracuje title (popis, vysvětlení zkratky)
		parseTitle(processor);

		// zpracuje koncový tag textu
		parseTextEndTag(processor);

		// zpracovat koncový tag
		parseEndTag(processor);

		return new AbbrElement(text, title);
	}

	private void parseStartTag(ParsingProcessor processor) {
		String startTag = processor.getStartTag();
		if (!startTag.equals(tag))
			throw new TokenException(tag, startTag);
		processor.nextToken();
	}

	private void parseAbbreviation(ParsingProcessor processor) {
		if (processor.getToken() != Token.EOF)
			text = processor.getText();
		else
			throw new TokenException(Token.TEXT);
		processor.nextToken();
	}

	private void parseTextStartTag(ParsingProcessor processor) {
		String startTag = processor.getStartTag();
		if (!startTag.equals(titleTag))
			throw new TokenException(titleTag, startTag);
		processor.nextToken();
	}

	private void parseTitle(ParsingProcessor processor) {
		if (processor.getToken() != Token.EOF)
			title = processor.getText();
		else
			throw new TokenException(Token.TEXT);
		processor.nextToken();
	}

	private void parseTextEndTag(ParsingProcessor processor) {
		String endTag = processor.getEndTag();
		if (!endTag.equals(titleTag))
			throw new TokenException(titleTag, processor.getCode());
		processor.nextToken();
	}

	private void parseEndTag(ParsingProcessor processor) {
		String endTag = processor.getEndTag();
		if (!endTag.equals(tag))
			throw new TokenException(tag, processor.getCode());
		processor.nextToken();
	}
}
