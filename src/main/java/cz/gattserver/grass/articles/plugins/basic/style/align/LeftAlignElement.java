package cz.gattserver.grass.articles.plugins.basic.style.align;

import cz.gattserver.grass.articles.plugins.basic.style.AbstractStyleElement;
import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;

import java.util.List;

public class LeftAlignElement extends AbstractStyleElement {

	public LeftAlignElement(List<Element> elist) {
		super(elist);
	}

	@Override
	public void generateStartTag(Context ctx) {
		ctx.print("<div style='text-align: left'>");
	}

	@Override
	public void generateEndTag(Context ctx) {
		ctx.print("</div>");
	}
}
