package cz.gattserver.grass.ui.components;

import java.util.List;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;

public class Breadcrumb extends Div {

	private static final long serialVersionUID = 3874999284861747099L;

	public static class BreadcrumbElement {
		private String caption;
		private String url;

		public BreadcrumbElement(String caption, String url) {
			this.url = url;
			this.caption = caption;
		}

		public String getCaption() {
			return caption;
		}

		public void setCaption(String caption) {
			this.caption = caption;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}

	public Breadcrumb() {
		setId("breadcrumb");
	}

	public Breadcrumb(List<BreadcrumbElement> breadcrumbElements) {
		this();
		initBreadcrumb(breadcrumbElements);
	}

	private Anchor createBreadcrumbElementLink(BreadcrumbElement element) {
		return new Anchor(element.getUrl(), element.getCaption());
	}

	private void initBreadcrumb(List<BreadcrumbElement> breadcrumbElements) {
		// konstrukce breadcrumb v opačném pořadí (správném)
		BreadcrumbElement element = null;
		for (int i = breadcrumbElements.size() - 1; i >= 0; i--) {
			element = breadcrumbElements.get(i);
			if (i != breadcrumbElements.size() - 1) {
				Image separator = new Image("img/bullet.png", "bullet");
				add(separator);
			}
			Anchor link = createBreadcrumbElementLink(element);
			add(link);
		}
	}

	public void resetBreadcrumb(List<BreadcrumbElement> breadcrumbElements) {
		removeAll();
		initBreadcrumb(breadcrumbElements);
	}

}
