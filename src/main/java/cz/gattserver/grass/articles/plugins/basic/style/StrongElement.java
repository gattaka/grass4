package cz.gattserver.grass.articles.plugins.basic.style;

import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;

import java.util.List;

public class StrongElement extends AbstractStyleElement {

	public StrongElement(List<Element> elist) {
		super(elist);
	}

	@Override
	public void generateStartTag(Context ctx) {
		ctx.print("<strong>");
	}

	@Override
	public void generateEndTag(Context ctx) {
		ctx.print("</strong>");
	}

}
