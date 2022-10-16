package cz.gattserver.web.common.ui;

import com.vaadin.flow.component.html.Span;

public class HtmlSpan extends Span {

	private static final long serialVersionUID = 7526089060607305886L;

	public HtmlSpan(String value) {
		super();
		setValue(value);
	}

	public HtmlSpan setValue(String value) {
		getElement().setProperty("innerHTML", value);
		return this;
	}

	public String getValue() {
		return getElement().getProperty("innerHTML");
	}
}
