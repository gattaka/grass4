package cz.gattserver.grass.articles.editor.parser.elements;

import cz.gattserver.grass.articles.editor.parser.Context;

import java.util.List;

public class BreaklineElement implements Element {

    @Override
	public void apply(Context ctx) {
        String text = "<br/>";
        ctx.println(text);
	}

	@Override
	public List<Element> getSubElements() {
		return null;
	}
}
