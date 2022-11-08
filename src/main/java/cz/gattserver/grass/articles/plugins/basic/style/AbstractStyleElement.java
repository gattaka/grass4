package cz.gattserver.grass.articles.plugins.basic.style;

import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;

import java.util.List;

public abstract class AbstractStyleElement implements Element {

	private List<Element> elements;

	protected abstract void generateStartTag(Context ctx);

	protected abstract void generateEndTag(Context ctx);

	public AbstractStyleElement(List<Element> elements) {
		this.elements = elements;
	}

	@Override
	public void apply(Context ctx) {
		generateStartTag(ctx);
		generateBlock(ctx);
		generateEndTag(ctx);
	}

	protected void generateBlock(Context ctx) {
		if (elements != null)
			for (Element et : elements)
				et.apply(ctx);
	}

	@Override
	public List<Element> getSubElements() {
		return elements;
	}
}
