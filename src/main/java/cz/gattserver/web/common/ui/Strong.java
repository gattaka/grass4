package cz.gattserver.web.common.ui;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Span;

@Tag(Tag.STRONG)
public class Strong extends Span {

	private static final long serialVersionUID = 7526089060607305886L;

	public Strong(String value) {
		super(value);
	}
}
