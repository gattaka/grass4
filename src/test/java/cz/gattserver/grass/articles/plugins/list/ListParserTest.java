package cz.gattserver.grass.articles.plugins.list;

import cz.gattserver.grass3.articles.editor.lexer.Lexer;
import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.TokenException;
import cz.gattserver.grass3.articles.editor.parser.impl.ArticleParser;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;
import cz.gattserver.grass3.articles.plugins.Plugin;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ListParserTest {

	private static final String TAG = "CUSTOM_TAG";
	private static final String START_TAG = "[" + TAG + "]";
	private static final String END_TAG = "[/" + TAG + "]";

	private ParsingProcessor getParsingProcessorWithText(String text) {
		Lexer lexer = new Lexer(text);
		// ať zná aspoň sebe
		AbstractListPlugin plugin = new AbstractListPlugin(TAG, "test-img", true) {
		};
		Map<String, Plugin> map = new HashMap<>();
		map.put(plugin.getTag(), plugin);
		ParsingProcessor pluginProcessor = new ParsingProcessor(lexer, "contextRoot", map);
		return pluginProcessor;
	}

	@Test
	public void test() {
		ArticleParser parser = new ArticleParser();
		Element element = parser.parse(getParsingProcessorWithText(START_TAG + "test" + END_TAG));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<ol><li>test</li></ol>", ctx.getOutput());
	}

	@Test
	public void testMultiline() {
		ArticleParser parser = new ArticleParser();
		Element element = parser.parse(getParsingProcessorWithText(START_TAG + "test\nhehe\naa" + END_TAG));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<ol><li>test</li><li>hehe</li><li>aa</li></ol>", ctx.getOutput());
	}

	@Test
	public void testEmptyline() {
		ArticleParser parser = new ArticleParser();
		Element element = parser.parse(getParsingProcessorWithText(START_TAG + "test\n" + END_TAG));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<ol><li>test</li></ol>", ctx.getOutput());
	}

	@Test
	public void testEmptyAfterline() {
		ArticleParser parser = new ArticleParser();
		Element element = parser.parse(getParsingProcessorWithText(START_TAG + "test\n" + END_TAG + "\nddd"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<ol><li>test</li></ol>ddd", ctx.getOutput());
	}
	
	@Test
	public void testEmptyAfterline2() {
		ArticleParser parser = new ArticleParser();
		Element element = parser.parse(getParsingProcessorWithText(START_TAG + "test\n" + END_TAG + "\nddd\nqqq"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<ol><li>test</li></ol>ddd<br/>qqq", ctx.getOutput());
	}

	@Test
	public void testSub() {
		ArticleParser parser = new ArticleParser();
		Element element = parser
				.parse(getParsingProcessorWithText(START_TAG + START_TAG + "test\nhehe" + END_TAG + END_TAG));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<ol><li><ol><li>test</li><li>hehe</li></ol></li></ol>", ctx.getOutput());
	}

	@Test
	public void testBreaklineAfterBlock() {
		ArticleParser parser = new ArticleParser();
		Element element = parser.parse(getParsingProcessorWithText(START_TAG + "test" + END_TAG + "\n"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<ol><li>test</li></ol>", ctx.getOutput());
	}

	@Test(expected = TokenException.class)
	public void test_failBadStartTag() {
		ListParser parser = new ListParser(TAG, true);
		ParsingProcessor processor = getParsingProcessorWithText("[BAD_TAG]test" + END_TAG);
		processor.nextToken(); // init
		Element element = parser.parse(processor);
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = TokenException.class)
	public void test_failBadEndTag() {
		ListParser parser = new ListParser(TAG, true);
		ParsingProcessor processor = getParsingProcessorWithText(START_TAG + "test[/BAD_TAG]");
		processor.nextToken(); // init
		Element element = parser.parse(processor);
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = TokenException.class)
	public void test_failMissingEnd() {
		ListParser parser = new ListParser(TAG, true);
		ParsingProcessor processor = getParsingProcessorWithText(START_TAG + "test");
		processor.nextToken(); // init
		Element element = parser.parse(processor);
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

}
