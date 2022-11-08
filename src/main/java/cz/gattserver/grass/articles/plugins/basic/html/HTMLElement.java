package cz.gattserver.grass.articles.plugins.basic.html;

import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;

import java.util.List;

public class HTMLElement implements Element {

	private String content;

	public HTMLElement(String content) {
		this.content = content;
	}

	@Override
	public void apply(Context ctx) {
		ctx.print("<div id=\"htmldiv\">" + content + "</div>");
	}

	@Override
	public List<Element> getSubElements() {
		return null;
	}
}
