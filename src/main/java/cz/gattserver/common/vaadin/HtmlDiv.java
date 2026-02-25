package cz.gattserver.common.vaadin;

import com.vaadin.flow.component.html.Div;
import lombok.NoArgsConstructor;

import java.io.Serial;

@NoArgsConstructor
public class HtmlDiv extends Div {

    @Serial
    private static final long serialVersionUID = -4907503228381589255L;

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
