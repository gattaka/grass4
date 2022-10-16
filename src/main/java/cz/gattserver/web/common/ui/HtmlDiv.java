package cz.gattserver.web.common.ui;

import com.vaadin.flow.component.html.Div;

public class HtmlDiv extends Div {

	private static final long serialVersionUID = 7526089060607305886L;

	public HtmlDiv() {
	}

	public HtmlDiv(String value) {
		setValue(value);
	}

	public HtmlDiv setValue(String value) {
		// https://github.com/vaadin/flow/issues/4644
		getElement().executeJs("this.innerHTML = $0", value);
		return this;
	}

	public String getValue() {
		return getElement().getProperty("innerHTML");
	}
}
