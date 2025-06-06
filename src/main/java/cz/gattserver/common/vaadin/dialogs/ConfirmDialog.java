package cz.gattserver.common.vaadin.dialogs;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.io.Serializable;

public class ConfirmDialog extends WebDialog {

	private static final long serialVersionUID = 4123506060675738841L;

	private ConfirmAction confirmAction;

	public interface ConfirmAction extends Serializable {
		public void onConfirm(ClickEvent<?> event);
	}

	/**
	 * Nejobecnější dotaz na potvrzení operace
	 */
	public ConfirmDialog(ConfirmAction confirmAction) {
		this("Opravdu si přejete provést tuto operaci ?", confirmAction);
	}

	/**
	 * Okno bude vytvořeno s popiskem, ve kterém bude předaný text
	 * 
	 * @param labelCaption
	 *            text popisku okna
	 */
	public ConfirmDialog(String labelCaption, ConfirmAction confirmAction) {
		this(new Label(labelCaption), confirmAction);
	}

	/**
	 * Okno bude vytvořeno přímo s připraveným popiskem
	 * 
	 * @param label
	 *            popisek okna
	 */
	public ConfirmDialog(Label label, ConfirmAction confirmAction) {
		this.confirmAction = confirmAction;

		label.setWidth("350px");

		addComponent(label, Alignment.STRETCH);
		HorizontalLayout btnLayout = new HorizontalLayout();
		addComponent(btnLayout, Alignment.CENTER);

		Button confirm = new Button("Ano", (ComponentEventListener<ClickEvent<Button>>) event -> {
			ConfirmDialog.this.confirmAction.onConfirm(event);
			close();
		});
		btnLayout.add(confirm);

		Button close = new Button("Ne", (ComponentEventListener<ClickEvent<Button>>) event -> close());
		btnLayout.add(close);

	}

	public ConfirmAction getConfirmAction() {
		return confirmAction;
	}

	public void setConfirmAction(ConfirmAction confirmAction) {
		this.confirmAction = confirmAction;
	}

}
