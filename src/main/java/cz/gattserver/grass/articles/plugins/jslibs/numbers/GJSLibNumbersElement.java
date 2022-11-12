package cz.gattserver.grass.articles.plugins.jslibs.numbers;

import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;

import java.util.List;

public class GJSLibNumbersElement implements Element {

	@Override
	public void apply(Context ctx) {
		ctx.addJSResource("articles/jslibs/js/numbers.js");
	}

	@Override
	public List<Element> getSubElements() {
		return null;
	}
}
