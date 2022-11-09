package cz.gattserver.grass.articles.plugins.abbr;

import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;
import org.junit.Test;

import static org.junit.Assert.*;

public class AbbrElementTest {

	@Test
	public void test() {
		AbbrElement e = new AbbrElement("testText", "testTitle");
		ContextImpl ctx = new ContextImpl();
		e.apply(ctx);
		String out = ctx.getOutput();
		assertEquals("<abbr title=\"testTitle\">testText</abbr>", out);
	}

}
