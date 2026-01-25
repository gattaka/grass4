package cz.gattserver.grass.hw.ui.dialogs;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass.hw.interfaces.HWItemState;
import cz.gattserver.grass.hw.interfaces.HWServiceNoteTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.UsedInChooser;

public abstract class HWServiceNoteEditDialog extends EditWebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient HWService hwService;

	public HWServiceNoteEditDialog(final HWItemTO hwItem) {
		this(hwItem, null);
	}

	public HWServiceNoteEditDialog(final HWItemTO hwItem, HWServiceNoteTO originalTO) {
        super("Záznam");
		HWServiceNoteTO formTO = new HWServiceNoteTO();
		formTO.setDate(LocalDate.now());
		formTO.setDescription("");
		formTO.setState(hwItem.getState());

		Binder<HWServiceNoteTO> binder = new Binder<>();
		binder.setBean(formTO);

		FormLayout winLayout = new FormLayout();
		winLayout.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
		add(winLayout);

		DatePicker eventDateField = new DatePicker("Datum");
		eventDateField.setLocale(Locale.forLanguageTag("CS"));
		binder.forField(eventDateField).asRequired("Datum musí být vyplněno").bind(HWServiceNoteTO::getDate,
				HWServiceNoteTO::setDate);
		winLayout.add(eventDateField);

		ComboBox<HWItemState> stateComboBox = new ComboBox<>("Stav", Arrays.asList(HWItemState.values()));
		// namísto propertyId a captionId jsou funkcionální settery a gettery
		stateComboBox.setItemLabelGenerator(HWItemState::getName);
		binder.forField(stateComboBox).bind(HWServiceNoteTO::getState, HWServiceNoteTO::setState);
		winLayout.add(stateComboBox);

		add(new UsedInChooser(hwItem, to -> {
			formTO.setUsedInId(to.getId());
			formTO.setUsedInName(to.getName());
		}));

		ComboBox<HWItemOverviewTO> usedInCombo = new ComboBox<>("Je součástí",
				getHWService().getHWItemsAvailableForPart(hwItem.getId()));
		usedInCombo.setSizeFull();
		usedInCombo.setItemLabelGenerator(HWItemOverviewTO::getName);
		usedInCombo.setValue(hwItem.getUsedIn());
		// ekvivalent Convertoru z v7
		binder.bind(usedInCombo, note -> {
			if (note.getUsedInName() == null)
				return null;
			HWItemOverviewTO to = new HWItemOverviewTO();
			to.setId(note.getUsedInId());
			to.setName(note.getUsedInName());
			return to;
		}, (note, item) -> {
			note.setUsedInId(item == null ? null : item.getId());
			note.setUsedInName(item == null ? null : item.getName());
		});
		winLayout.add(usedInCombo, 2);

		if (hwItem.getUsedIn() != null)
			usedInCombo.setValue(hwItem.getUsedIn());

		TextArea descriptionField = new TextArea("Popis");
		descriptionField.setWidthFull();
		descriptionField.setHeight("200px");
		binder.forField(descriptionField).bind(HWServiceNoteTO::getDescription, HWServiceNoteTO::setDescription);
		winLayout.add(descriptionField, 2);

		HorizontalLayout buttons = componentFactory.createDialogSubmitOrStornoLayout(e -> {
			try {
				HWServiceNoteTO writeDTO = originalTO == null ? new HWServiceNoteTO() : originalTO;
				binder.writeBean(writeDTO);
				if (originalTO == null) {
					getHWService().addServiceNote(writeDTO, hwItem.getId());
					onSuccess(writeDTO);
				} else {
					getHWService().modifyServiceNote(writeDTO);
					onSuccess(writeDTO);
				}
				close();
			} catch (Exception ex) {
				new ErrorDialog("Nezdařilo se zapsat nový servisní záznam").open();
			}
		}, e -> close());

		add(buttons);

		// Poté, co je form probindován se nastaví hodnoty dle originálu
		if (originalTO != null)
			binder.readBean(originalTO);
	}

	private HWService getHWService() {
		if (hwService == null)
			hwService = SpringContextHelper.getBean(HWService.class);
		return hwService;
	}

	protected abstract void onSuccess(HWServiceNoteTO noteDTO);

}
