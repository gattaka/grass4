package cz.gattserver.grass.articles.editor.parser.elements;

import cz.gattserver.grass.articles.editor.parser.Context;

import java.util.List;

public class TextElement implements cz.gattserver.grass.articles.editor.parser.elements.Element {

	private String text;

	public TextElement(String text) {
		this.text = text;
	}

	@Override
	public void apply(Context ctx) {
		ctx.println(text);
	}

	public String getText() {
		return text;
	}

	@Override
	public List<Element> getSubElements() {
		return null;
	}
}
