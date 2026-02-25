package cz.gattserver.common.vaadin;

import com.vaadin.flow.component.html.Span;

import java.io.Serial;

public class HtmlSpan extends Span {

    @Serial
    private static final long serialVersionUID = 1340755668510217229L;

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
