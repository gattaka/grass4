package cz.gattserver.grass.articles.plugins.abbr;

import cz.gattserver.grass3.articles.editor.lexer.Lexer;
import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.TokenException;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;
import cz.gattserver.grass3.articles.plugins.basic.abbr.AbbrParser;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class AbbrParserTest {

	private ParsingProcessor getParsingProcessorWithText(String text) {
		Lexer lexer = new Lexer(text);
		ParsingProcessor parsingProcessor = new ParsingProcessor(lexer, "contextRoot", new HashMap<>());
		parsingProcessor.nextToken(); // musí se inicializovat
		return parsingProcessor;
	}

	@Test
	public void test() {
		AbbrParser parser = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
		Element element = parser.parse(getParsingProcessorWithText(
				"[CUSTOM_TAG]HTML[CUSTOM_TAG2]Hypertext Markup Language[/CUSTOM_TAG2][/CUSTOM_TAG]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<abbr title=\"Hypertext Markup Language\">HTML</abbr>", ctx.getOutput());
	}

	@Test(expected = TokenException.class)
	public void test_failAbbrEOF() {
		AbbrParser parser = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG]HTML[CUSTOM_TAG2]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = TokenException.class)
	public void test_failTitleEOF() {
		AbbrParser parser = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = TokenException.class)
	public void test_failBadAbbrStartTag() {
		AbbrParser parser = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
		Element element = parser.parse(getParsingProcessorWithText(
				"[BAD_TAG]HTML[CUSTOM_TAG2]Hypertext Markup Language[/CUSTOM_TAG2][/CUSTOM_TAG]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = TokenException.class)
	public void test_failBadAbbrEndTag() {
		AbbrParser parser = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
		Element element = parser.parse(getParsingProcessorWithText(
				"[CUSTOM_TAG]HTML[CUSTOM_TAG2]Hypertext Markup Language[/CUSTOM_TAG2][/BAD_TAG]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = TokenException.class)
	public void test_failBadTitleStartTag() {
		AbbrParser parser = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
		Element element = parser.parse(getParsingProcessorWithText(
				"[CUSTOM_TAG]HTML[BAD_TAG2]Hypertext Markup Language[/CUSTOM_TAG2][/CUSTOM_TAG]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = TokenException.class)
	public void test_failBadTitleEndTag() {
		AbbrParser parser = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
		Element element = parser.parse(getParsingProcessorWithText(
				"[CUSTOM_TAG]HTML[CUSTOM_TAG2]Hypertext Markup Language[/BAD_TAG2][/CUSTOM_TAG]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = TokenException.class)
	public void test_failMandatoryAbbr() {
		AbbrParser parser = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
		Element element = parser
				.parse(getParsingProcessorWithText("[CUSTOM_TAG][CUSTOM_TAG2][/CUSTOM_TAG2][/CUSTOM_TAG]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = TokenException.class)
	public void test_failMandatoryTitle() {
		AbbrParser parser = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
		Element element = parser
				.parse(getParsingProcessorWithText("[CUSTOM_TAG] [CUSTOM_TAG2][/CUSTOM_TAG2][/CUSTOM_TAG]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = TokenException.class)
	public void test_failMandatorySubtag() {
		AbbrParser parser = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG][/CUSTOM_TAG]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

}
