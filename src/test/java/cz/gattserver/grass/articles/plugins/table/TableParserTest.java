package cz.gattserver.grass.articles.plugins.table;

import cz.gattserver.grass3.articles.editor.lexer.Lexer;
import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.TokenException;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;
import cz.gattserver.grass3.articles.plugins.Plugin;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TableParserTest {

	private ParsingProcessor getParsingProcessorWithText(String text) {
		Lexer lexer = new Lexer(text);
		// ať zná aspoň sebe
		TablePlugin plugin = new TablePlugin();
		Map<String, Plugin> map = new HashMap<>();
		map.put(plugin.getTag(), plugin);
		ParsingProcessor pluginBag = new ParsingProcessor(lexer, "contextRoot", map);
		pluginBag.nextToken(); // musí se inicializovat
		return pluginBag;
	}

	@Test
	public void testEmpty() {
		TableParser parser = new TableParser("CUSTOM_TAG", false);
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG][/CUSTOM_TAG]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("", ctx.getOutput());
	}

	@Test
	public void testEmptyCell() {
		TableParser parser = new TableParser("CUSTOM_TAG", false);
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG]\t[/CUSTOM_TAG]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals(
				"<table bordercolor=\"#888\" cellpadding=\"5px\" rules=\"all\" border=\"0\" style=\"margin-left: auto; margin-right: auto;\">"
						+ "<tr><td></td></tr>" + "</table>",
				ctx.getOutput());
	}

	@Test
	public void testEmptyLine() {
		TableParser parser = new TableParser("CUSTOM_TAG", false);
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG]\n[/CUSTOM_TAG]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("", ctx.getOutput());
	}

	@Test
	public void testEndTagAsText() {
		TableParser parser = new TableParser("CUSTOM_TAG", false);
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG][/STH][/CUSTOM_TAG]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals(
				"<table bordercolor=\"#888\" cellpadding=\"5px\" rules=\"all\" border=\"0\" style=\"margin-left: auto; margin-right: auto;\">"
						+ "<tr><td>[/STH]</td></tr>" + "</table>",
				ctx.getOutput());
	}

	@Test
	public void test() {
		TableParser parser = new TableParser("CUSTOM_TAG", false);
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG]ab c\tde\nfg[/CUSTOM_TAG]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals(
				"<table bordercolor=\"#888\" cellpadding=\"5px\" rules=\"all\" border=\"0\" style=\"margin-left: auto; margin-right: auto;\">"
						+ "<tr><td>ab c</td><td>de</td></tr>" + "<tr><td>fg</td><td></td></tr>" + "</table>",
				ctx.getOutput());
	}

	@Test
	public void testIgnoreAfterBreakline() {
		TableParser parser = new TableParser("CUSTOM_TAG", false);
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG]ab c\tde\nfg[/CUSTOM_TAG]\n"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals(
				"<table bordercolor=\"#888\" cellpadding=\"5px\" rules=\"all\" border=\"0\" style=\"margin-left: auto; margin-right: auto;\">"
						+ "<tr><td>ab c</td><td>de</td></tr>" + "<tr><td>fg</td><td></td></tr>" + "</table>",
				ctx.getOutput());
	}

	@Test
	public void testSquareTable() {
		TableParser parser = new TableParser("CUSTOM_TAG", false);
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG]ab c\tde\nfg\tee[/CUSTOM_TAG]\n"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals(
				"<table bordercolor=\"#888\" cellpadding=\"5px\" rules=\"all\" border=\"0\" style=\"margin-left: auto; margin-right: auto;\">"
						+ "<tr><td>ab c</td><td>de</td></tr>" + "<tr><td>fg</td><td>ee</td></tr>" + "</table>",
				ctx.getOutput());
	}

	@Test
	public void testLineSizeIncreasingTable() {
		TableParser parser = new TableParser("CUSTOM_TAG", false);
		Element element = parser.parse(
				getParsingProcessorWithText("[CUSTOM_TAG]ab c\tde\nfg\tee\tT\ner\tdd\tdffs\tfop[/CUSTOM_TAG]\n"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals(
				"<table bordercolor=\"#888\" cellpadding=\"5px\" rules=\"all\" border=\"0\" style=\"margin-left: auto; margin-right: auto;\">"
						+ "<tr><td>ab c</td><td>de</td><td></td><td></td></tr>"
						+ "<tr><td>fg</td><td>ee</td><td>T</td><td></td></tr>"
						+ "<tr><td>er</td><td>dd</td><td>dffs</td><td>fop</td></tr>" + "</table>",
				ctx.getOutput());
	}

	@Test
	public void testLineSizeChangingTable() {
		TableParser parser = new TableParser("CUSTOM_TAG", false);
		Element element = parser.parse(
				getParsingProcessorWithText("[CUSTOM_TAG]ab c\tde\tfg\nee\tT\ner\tdd\tdffs\tfop[/CUSTOM_TAG]\n"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals(
				"<table bordercolor=\"#888\" cellpadding=\"5px\" rules=\"all\" border=\"0\" style=\"margin-left: auto; margin-right: auto;\">"
						+ "<tr><td>ab c</td><td>de</td><td>fg</td><td></td></tr>"
						+ "<tr><td>ee</td><td>T</td><td></td><td></td></tr>"
						+ "<tr><td>er</td><td>dd</td><td>dffs</td><td>fop</td></tr>" + "</table>",
				ctx.getOutput());
	}

	@Test
	public void testSub() {
		TableParser parser = new TableParser("CUSTOM_TAG", false);
		Element element = parser
				.parse(getParsingProcessorWithText("[CUSTOM_TAG]ab c\tde\nfg \t[TABLE]X[/TABLE][/CUSTOM_TAG]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals(
				"<table bordercolor=\"#888\" cellpadding=\"5px\" rules=\"all\" border=\"0\" style=\"margin-left: auto; margin-right: auto;\">"
						/*	*/ + "<tr>"
						/*		*/ + "<td>ab c</td>"
						/*		*/ + "<td>de</td>"
						/*	*/ + "</tr>"
						/*	*/ + "<tr>"
						/*		*/ + "<td>fg </td>"
						/*		*/ + "<td>"
						/*			*/ + "<table bordercolor=\"#888\" cellpadding=\"5px\" rules=\"all\" border=\"0\" style=\"margin-left: auto; margin-right: auto;\">"
						/*				*/ + "<tr><td>X</td></tr>"
						/*			*/ + "</table>"
						/*		*/ + "</td>"
						/*	*/ + "</tr>"
						/**/ + "</table>", ctx.getOutput());
	}

	@Test
	public void testSub2() {
		TableParser parser = new TableParser("CUSTOM_TAG", false);
		Element element = parser
				.parse(getParsingProcessorWithText("[CUSTOM_TAG]ab c\tde\nfg[TABLE]X[/TABLE][/CUSTOM_TAG]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals(
				"<table bordercolor=\"#888\" cellpadding=\"5px\" rules=\"all\" border=\"0\" style=\"margin-left: auto; margin-right: auto;\">"
						/*	*/ + "<tr>"
						/*		*/ + "<td>ab c</td>"
						/*		*/ + "<td>de</td>"
						/*	*/ + "</tr>"
						/*	*/ + "<tr>"
						/*		*/ + "<td>"
						/*			*/ + "fg<table bordercolor=\"#888\" cellpadding=\"5px\" rules=\"all\" border=\"0\" style=\"margin-left: auto; margin-right: auto;\">"
						/*				*/ + "<tr><td>X</td></tr>"
						/*			*/ + "</table>"
						/*		*/ + "</td>"
						/*		*/ + "<td></td>"
						/*	*/ + "</tr>"
						/**/ + "</table>", ctx.getOutput());
	}

	@Test(expected = TokenException.class)
	public void test_failBadStartTag() {
		TableParser parser = new TableParser("CUSTOM_TAG", false);
		Element element = parser.parse(getParsingProcessorWithText("[BAD_TAG]ab c\tde\nfg[/CUSTOM_TAG]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = TokenException.class)
	public void test_failBadStopTag() {
		TableParser parser = new TableParser("CUSTOM_TAG", false);
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG]ab c\tde\nfg[/BAD_TAG]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

}
