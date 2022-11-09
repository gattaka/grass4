package cz.gattserver.grass.articles.plugins.image;

import cz.gattserver.grass3.articles.editor.lexer.Lexer;
import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.TokenException;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class ImageParserTest {

	private ParsingProcessor getParsingProcessorWithText(String text) {
		Lexer lexer = new Lexer(text);
		ParsingProcessor pluginBag = new ParsingProcessor(lexer, "contextRoot", new HashMap<>());
		pluginBag.nextToken(); // musí se inicializovat
		return pluginBag;
	}

	@Test
	public void test() {
		ImageParser parser = new ImageParser("CUSTOM_TAG");
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG]http://test/image.png[/CUSTOM_TAG]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals(
				"<a target=\"_blank\" href=\"http://test/image.png\"><img class=\"articles-basic-img\" src=\"http://test/image.png\" alt=\"http://test/image.png\" /></a>",
				ctx.getOutput());
	}

	@Test(expected = TokenException.class)
	public void test_failBadStartTag() {
		ImageParser parser = new ImageParser("CUSTOM_TAG");
		Element element = parser.parse(getParsingProcessorWithText("[BAD_TAG]http://test/image.png[/CUSTOM_TAG]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = TokenException.class)
	public void test_failBadEndTag() {
		ImageParser parser = new ImageParser("CUSTOM_TAG");
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG]http://test/image.png[/BAD_TAG]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = TokenException.class)
	public void test_failMandatoryImg() {
		ImageParser parser = new ImageParser("CUSTOM_TAG");
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG][/CUSTOM_TAG]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = TokenException.class)
	public void test_failMissingEnd() {
		ImageParser parser = new ImageParser("CUSTOM_TAG");
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG]http://test/image.png"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

}
