package cz.gattserver.grass.articles.plugins.jslibs;

import cz.gattserver.grass.articles.editor.lexer.Token;
import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass.articles.editor.parser.elements.Element;
import cz.gattserver.grass.articles.editor.parser.exceptions.TokenException;

/**
 * @author gatt
 */
public class JSLibParser implements Parser {

	private String tag;

	public JSLibParser(String tag) {
		this.tag = tag;
	}

	@Override
	public Element parse(ParsingProcessor processor) {
		parseStartTag(processor, tag);

		// zpracovat text
		StringBuilder link = new StringBuilder();
		if (Token.TEXT.equals(processor.getToken()))
			link.append(processor.getTextTree().getText());
		else
			throw new TokenException(Token.TEXT, processor.getToken(), processor.getText());

		// zpracovat koncový tag
		parseEndTag(processor, tag);

		// ignoruj případný <br/>
		if (processor.getToken().equals(Token.EOL))
			processor.nextToken();

		return new JSLibElement(link.toString());
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
