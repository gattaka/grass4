package cz.gattserver.common.vaadin.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.server.StreamResource;

public class MessageDialog extends WebDialog {

	private static final long serialVersionUID = 4123506060675738841L;

	protected TextArea detailsArea;

	/**
	 * @param labelCaption
	 *            obsah zprávy v okně
	 * @param imageResource
	 *            resource ikony okna
	 */
	public MessageDialog(String labelCaption, StreamResource imageResource) {
		this(labelCaption, null, imageResource);
	}

	/**
	 * @param labelCaption
	 *            obsah zprávy v okně
	 * @param imageResource
	 *            resource ikony okna
	 */
	public MessageDialog(String labelCaption, String details, StreamResource imageResource) {
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSpacing(true);
		addComponent(horizontalLayout);

		Image embedded = new Image(imageResource, "img");
		horizontalLayout.add(embedded);
		horizontalLayout.setVerticalComponentAlignment(Alignment.CENTER, embedded);

		Div msgLabel = new Div(labelCaption);
		msgLabel.setSizeUndefined();
		horizontalLayout.add(msgLabel);

		createDetails(details);

		Button proceedButton = new Button("OK", event -> close());

		addComponent(proceedButton);
		setComponentAlignment(proceedButton, Alignment.END);
	}

	protected void createDetails(String details) {
		if (details != null) {
			detailsArea = new TextArea();
			detailsArea.setValue(details);
			detailsArea.setEnabled(true);
			detailsArea.setReadOnly(true);
			detailsArea.setWidthFull();
			detailsArea.setHeight("200px");
			addComponent(detailsArea);
		}
	}

}
