package cz.gattserver.grass.articles.plugins.basic.style;

import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;

import java.util.List;

public class SubElement extends AbstractStyleElement {

	public SubElement(List<Element> elist) {
		super(elist);
	}

	@Override
	public void generateStartTag(Context ctx) {
		ctx.print("<sub>");
	}

	@Override
	public void generateEndTag(Context ctx) {
		ctx.print("</sub>");
	}

}
