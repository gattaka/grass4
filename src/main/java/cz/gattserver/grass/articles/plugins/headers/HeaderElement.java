package cz.gattserver.grass.articles.plugins.headers;

import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;

import java.util.List;

public class HeaderElement implements Element {

	private List<Element> headerContent;
	private int level;

	public HeaderElement(List<Element> headerContent, int level) {
		this.headerContent = headerContent;
		this.level = level;
	}

	@Override
	public void apply(Context ctx) {
		ctx.resetHeaderLevel();
		ctx.print("<div class=\"articles-h" + level + "\">");
		for (Element headerText : headerContent)
			headerText.apply(ctx);
		ctx.print(" <a class=\"articles-h-id\" href=\"" + ctx.getNextHeaderIdentifier() + "\"></a>");
		ctx.print("</div>");
		ctx.setHeaderLevel(level);
		ctx.addCSSResource("articles/style.css");
	}
	
	@Override
	public List<Element> getSubElements() {
		return headerContent;
	}

}
