package cz.gattserver.common.vaadin;

import com.vaadin.flow.component.html.Span;

public class HtmlSpan extends Span {

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
