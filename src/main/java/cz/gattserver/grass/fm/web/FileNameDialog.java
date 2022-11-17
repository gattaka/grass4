package cz.gattserver.grass.fm.web;

import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.core.ui.components.button.CloseButton;
import cz.gattserver.grass.core.ui.components.button.SaveButton;
import cz.gattserver.grass.fm.interfaces.FMItemTO;

public class FileNameDialog extends WebDialog {

	private static final long serialVersionUID = 9163906666470561249L;

	public interface SaveAction {
		void onSave(FMItemTO quoteDTO, FileNameDialog dialog);
	}

	public FileNameDialog(SaveAction saveAction) {
		init(null, saveAction);
	}

	public FileNameDialog(FMItemTO to, SaveAction saveAction) {
		init(to, saveAction);
	}

	private void init(FMItemTO existingTO, SaveAction saveAction) {
		final Binder<FMItemTO> binder = new Binder<>();
		binder.setBean(new FMItemTO());

		final TextField textField = new TextField();
		textField.setPlaceholder("Název souboru");
		textField.setWidth("400px");
		binder.forField(textField).asRequired().bind(FMItemTO::getName, FMItemTO::setName);
		addComponent(textField);

		if (existingTO != null)
			binder.readBean(existingTO);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(false);
		btnLayout.setPadding(false);
		btnLayout.setWidthFull();
		btnLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
		addComponent(btnLayout);

		btnLayout.add(new SaveButton(event -> {
			if (!binder.validate().isOk())
				return;
			FMItemTO to = binder.getBean();
			to.setName(textField.getValue());
			saveAction.onSave(to, this);
		}));

		btnLayout.add(new CloseButton(e -> close()));
		textField.focus();
	}
}
