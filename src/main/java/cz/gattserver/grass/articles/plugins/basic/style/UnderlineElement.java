package cz.gattserver.grass.articles.plugins.basic.style;

import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;

import java.util.List;

public class UnderlineElement extends AbstractStyleElement {

	public UnderlineElement(List<Element> elist) {
		super(elist);
	}

	@Override
	public void generateStartTag(Context ctx) {
		ctx.print("<span style='text-decoration: underline'>");
	}

	@Override
	public void generateEndTag(Context ctx) {
		ctx.print("</span>");
	}
}
