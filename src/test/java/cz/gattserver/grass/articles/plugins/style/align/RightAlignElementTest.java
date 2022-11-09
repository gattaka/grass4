package cz.gattserver.grass.articles.plugins.style.align;

import cz.gattserver.grass3.articles.editor.parser.elements.TextElement;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class RightAlignElementTest {

	@Test
	public void test() {
		RightAlignElement e = new RightAlignElement(Arrays.asList(new TextElement("neco")));
		ContextImpl ctx = new ContextImpl();
		e.apply(ctx);
		String out = ctx.getOutput();
		assertEquals("<div style='text-align: right'>neco</div>", out);
	}

}
