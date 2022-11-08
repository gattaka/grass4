package cz.gattserver.grass.articles.editor.parser.elements;

import cz.gattserver.grass.articles.editor.parser.Context;

import java.util.List;

/**
 * @author gatt
 */
public class ArticleElement implements Element {

	private List<Element> children;

	public ArticleElement(List<Element> elements) {
		this.children = elements;
	}

	@Override
	public void apply(Context ctx) {
		if (children != null) {
			for (Element et : children)
				et.apply(ctx);
		} else {
			ctx.print("~ empty ~");
		}
	}

	@Override
	public List<Element> getSubElements() {
		return children;
	}
}
