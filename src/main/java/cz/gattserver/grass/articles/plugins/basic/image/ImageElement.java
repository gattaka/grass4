package cz.gattserver.grass.articles.plugins.basic.image;

import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;

import java.util.List;

public class ImageElement implements Element {

	private String link;

	public ImageElement(String link) {
		this.link = link;
	}

	@Override
	public void apply(Context ctx) {
		ctx.addCSSResource("articles/basic/style.css");
		ctx.print("<a target=\"_blank\" href=\"" + link + "\"><img class=\"articles-basic-img\" src=\"" + link
				+ "\" alt=\"" + link + "\" /></a>");
	}

	@Override
	public List<Element> getSubElements() {
		return null;
	}
}
