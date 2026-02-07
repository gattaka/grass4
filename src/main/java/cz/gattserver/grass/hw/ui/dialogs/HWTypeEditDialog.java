package cz.gattserver.grass.hw.ui.dialogs;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.hw.interfaces.HWTypeTO;
import cz.gattserver.grass.hw.service.HWService;

public abstract class HWTypeEditDialog extends EditWebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient HWService hwService;

	public HWTypeEditDialog(HWTypeTO originalDTO) {
        super("Typ");
		init(originalDTO);
	}

	private HWService getHWService() {
		if (hwService == null)
			hwService = SpringContextHelper.getBean(HWService.class);
		return hwService;
	}

	public void init(HWTypeTO originalDTO) {
		HWTypeTO formDTO = new HWTypeTO();
		formDTO.setName("");
		Binder<HWTypeTO> binder = new Binder<>(HWTypeTO.class);
		binder.setBean(formDTO);

		final TextField nameField = new TextField();
		nameField.setPlaceholder("Typ HW");
		nameField.setWidthFull();
		binder.forField(nameField).asRequired().bind(HWTypeTO::getName, HWTypeTO::setName);

		add(nameField);

		HorizontalLayout buttons = componentFactory.createDialogSubmitOrStornoLayout(e -> {
			try {
				HWTypeTO writeDTO = originalDTO == null ? new HWTypeTO() : originalDTO;
				binder.writeBean(writeDTO);
				getHWService().saveHWType(writeDTO);
				onSuccess(writeDTO);
				close();
			} catch (Exception ex) {
				new ErrorDialog("Uložení se nezdařilo").open();
			}
		}, e -> close(), saveButton -> saveButton.addClickShortcut(Key.ENTER));
		buttons.setMinWidth("200px");
		add(buttons);

		if (originalDTO != null)
			binder.readBean(originalDTO);

		nameField.focus();
	}

	protected abstract void onSuccess(HWTypeTO hwItemTypeDTO);

}
