package cz.gattserver.grass.articles.plugins.style;

import cz.gattserver.grass3.articles.editor.parser.elements.TextElement;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class MonospaceElementTest {

	@Test
	public void test() {
		MonospaceElement e = new MonospaceElement(Arrays.asList(new TextElement("neco")));
		ContextImpl ctx = new ContextImpl();
		e.apply(ctx);
		String out = ctx.getOutput();
		assertEquals("<span class=\"articles-basic-monospaced\">neco</span>", out);
		assertTrue(ctx.getCSSResources().contains("articles/basic/style.css"));
	}

}
