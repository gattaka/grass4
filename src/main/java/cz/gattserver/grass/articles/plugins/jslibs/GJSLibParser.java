package cz.gattserver.grass.articles.plugins.jslibs;

import cz.gattserver.grass.articles.editor.lexer.Token;
import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass.articles.editor.parser.elements.Element;
import cz.gattserver.grass.articles.editor.parser.exceptions.TokenException;

/**
 * @author gatt
 */
public abstract class GJSLibParser implements Parser {

	private String tag;

	protected abstract Element createElement();

	public GJSLibParser(String tag) {
		this.tag = tag;
	}

	@Override
	public Element parse(ParsingProcessor processor) {
		parseStartTag(processor, tag);

		// zpracovat koncový tag
		parseEndTag(processor, tag);

		// ignoruj případný <br/>
		if (processor.getToken().equals(Token.EOL))
			processor.nextToken();

		return createElement();
	}

	private void parseStartTag(ParsingProcessor processor, String tag) {
		String startTag = processor.getStartTag();
		if (!startTag.equals(tag))
			throw new TokenException(tag, startTag);
		processor.nextToken();
	}

	private void parseEndTag(ParsingProcessor processor, String tag) {
		String endTag = processor.getEndTag();
		if (!endTag.equals(tag))
			throw new TokenException(tag, processor.getCode());
		processor.nextToken();
	}
}
