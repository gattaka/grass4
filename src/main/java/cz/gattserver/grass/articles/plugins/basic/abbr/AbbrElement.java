package cz.gattserver.grass.articles.plugins.basic.abbr;

import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;

import java.util.List;

public class AbbrElement implements Element {

	private String text;
	private String title;

	public AbbrElement(String text, String title) {
		this.text = text;
		this.title = title;
	}

	@Override
	public void apply(Context ctx) {
		ctx.print("<abbr title=\"" + title + "\">" + text + "</abbr>");
	}

	@Override
	public List<Element> getSubElements() {
		return null;
	}
}
