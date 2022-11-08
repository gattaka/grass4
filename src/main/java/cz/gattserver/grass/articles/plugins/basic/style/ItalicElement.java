package cz.gattserver.grass.articles.plugins.basic.style;

import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;

import java.util.List;

public class ItalicElement extends AbstractStyleElement {

	public ItalicElement(List<Element> elist) {
		super(elist);
	}

	@Override
	public void generateStartTag(Context ctx) {
		ctx.print("<em>");
	}

	@Override
	public void generateEndTag(Context ctx) {
		ctx.print("</em>");
	}

}
