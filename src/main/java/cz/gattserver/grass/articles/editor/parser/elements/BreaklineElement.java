package cz.gattserver.grass.articles.editor.parser.elements;

import cz.gattserver.grass.articles.editor.parser.Context;

import java.util.List;

public class BreaklineElement implements Element {

	private String text = "<br/>";

	@Override
	public void apply(Context ctx) {
		ctx.println(text);
	}

	@Override
	public List<Element> getSubElements() {
		return null;
	}
}
