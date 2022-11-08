package cz.gattserver.grass.articles.plugins.basic.style;

import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;

import java.util.List;

public class CrossoutElement extends AbstractStyleElement {

	public CrossoutElement(List<Element> elist) {
		super(elist);
	}

	@Override
	public void generateStartTag(Context ctx) {
		ctx.print("<span style='text-decoration: line-through'>");
	}

	@Override
	public void generateEndTag(Context ctx) {
		ctx.print("</span>");
	}
	
}
