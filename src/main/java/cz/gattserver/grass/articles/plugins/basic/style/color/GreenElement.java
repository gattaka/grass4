package cz.gattserver.grass.articles.plugins.basic.style.color;

import cz.gattserver.grass.articles.plugins.basic.style.AbstractStyleElement;
import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;

import java.util.List;

public class GreenElement extends AbstractStyleElement {

	public GreenElement(List<Element> elist) {
		super(elist);
	}

	@Override
	public void generateStartTag(Context ctx) {
		ctx.print("<span style='color: green'>");
	}

	@Override
	public void generateEndTag(Context ctx) {
		ctx.print("</span>");
	}

}
