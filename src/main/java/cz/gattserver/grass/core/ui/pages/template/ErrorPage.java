package cz.gattserver.grass.core.ui.pages.template;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@Tag(Tag.DIV)
public abstract class ErrorPage extends OneColumnPage {

	private static final long serialVersionUID = 4576353466500365046L;

	public ErrorPage() {
		init();
	}

	@Override
	protected void createColumnContent(Div layout) {
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSpacing(true);
		horizontalLayout.setPadding(true);
		horizontalLayout.setWidthFull();

		Div div = new Div();
		div.setText(getErrorText());
		div.addClassName("error-label");
		Image img = new Image(getErrorImage(), "Chyba");

		horizontalLayout.add(img);
		horizontalLayout.add(div);
		horizontalLayout.setFlexGrow(1, div);
		horizontalLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);

		layout.add(horizontalLayout);
	}

	protected abstract String getErrorText();

	protected abstract String getErrorImage();

}