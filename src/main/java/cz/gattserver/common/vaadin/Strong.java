package cz.gattserver.common.vaadin;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Span;

@Tag(Tag.STRONG)
public class Strong extends Span {

	public Strong(String value) {
		super(value);
	}
}