package cz.gattserver.grass.articles.plugins.favlink.plugin;

import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class FavlinkElement implements Element {

	private static final int MAX_LENGTH = 100;
	private static final int MIN_LINK_LENGTH = 20;

	private String link = null;
	private String description = null;
	private String imgURL = null;

	public FavlinkElement(String faviconURL, String description, String pageURL) {
		this.imgURL = faviconURL;
		this.description = description;
		this.link = pageURL;
	}

	private String createShortLink(String link, String shortDescription) {
		int maxLength = MAX_LENGTH - shortDescription.length();
		if (link.length() <= maxLength)
			return link;
		return link.substring(0, maxLength / 2 - 3) + "..." + link.substring(link.length() - maxLength / 2);
	}

	private String createShortDescription(String link, String description) {
		if (description.length() > MAX_LENGTH - MIN_LINK_LENGTH) {
			int maxLength = MAX_LENGTH - MIN_LINK_LENGTH;
			return description.substring(0, maxLength - 3) + "...";
		}
		return description;
	}

	@Override
	public void apply(Context ctx) {
		String shortDescription = "";
		if (StringUtils.isNotBlank(description)) {
			shortDescription = createShortDescription(link, description);
			ctx.print(shortDescription);
			ctx.print(" ");
		}
		ctx.print("<a style=\"word-wrap: break-word; white-space: nowrap\" href=\"" + link + "\" ");
		if (StringUtils.isNotBlank(description))
			ctx.print("title=\"" + description + "\" ");
		ctx.print(">");
		if (imgURL != null)
			ctx.print("<img style=\"margin: 4px 5px -4px 2px;\" height=\"16\" width=\"16\" src=\"" + imgURL + "\" />");
		ctx.print(createShortLink(link, shortDescription));
		ctx.print("</a>");
	}

	@Override
	public List<Element> getSubElements() {
		return null;
	}
}
