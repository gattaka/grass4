package cz.gattserver.grass.articles.plugins.list;

import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.elements.TextElement;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ListElementTest {

	@Test
	public void testOrdered() {
		List<List<Element>> list = new ArrayList<>();
		List<Element> line1 = new ArrayList<>();
		list.add(line1);
		line1.add(new TextElement("aaa"));
		line1.add(new TextElement("bbb"));
		List<Element> line2 = new ArrayList<>();
		list.add(line2);
		line2.add(new TextElement("ccc"));

		ListElement e = new ListElement(list, true);
		ContextImpl ctx = new ContextImpl();
		e.apply(ctx);
		String out = ctx.getOutput();
		assertEquals("<ol><li>aaabbb</li><li>ccc</li></ol>", out);
	}
	
	@Test
	public void testUnordered() {
		List<List<Element>> list = new ArrayList<>();
		List<Element> line1 = new ArrayList<>();
		list.add(line1);
		line1.add(new TextElement("aaa"));
		line1.add(new TextElement("bbb"));
		List<Element> line2 = new ArrayList<>();
		list.add(line2);
		line2.add(new TextElement("ccc"));

		ListElement e = new ListElement(list, false);
		ContextImpl ctx = new ContextImpl();
		e.apply(ctx);
		String out = ctx.getOutput();
		assertEquals("<ul><li>aaabbb</li><li>ccc</li></ul>", out);
	}

}
