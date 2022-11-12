package cz.gattserver.grass.articles.plugins.basic.abbr;


import cz.gattserver.grass.articles.editor.parser.impl.ContextImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
