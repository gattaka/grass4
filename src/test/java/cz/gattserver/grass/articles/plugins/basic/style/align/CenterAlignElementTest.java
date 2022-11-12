package cz.gattserver.grass.articles.plugins.basic.style.align;

import cz.gattserver.grass.articles.editor.parser.elements.TextElement;
import cz.gattserver.grass.articles.editor.parser.impl.ContextImpl;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
public class CenterAlignElementTest {

	@Test
	public void test() {
		CenterAlignElement e = new CenterAlignElement(Arrays.asList(new TextElement("neco")));
		ContextImpl ctx = new ContextImpl();
		e.apply(ctx);
		String out = ctx.getOutput();
		assertEquals("<div style='text-align: center'>neco</div>", out);
	}

}
