package cz.gattserver.grass.hw.ui.dialogs;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.interfaces.HWItemState;
import cz.gattserver.grass.hw.interfaces.HWItemRecordTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.UsedInChooser;

public abstract class HWItemRecordEditDialog extends EditWebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient HWService hwService;

	public HWItemRecordEditDialog(final HWItemTO hwItem) {
		this(hwItem, null);
	}

	public HWItemRecordEditDialog(final HWItemTO hwItem, HWItemRecordTO originalTO) {
        super("Záznam");
		HWItemRecordTO formTO = new HWItemRecordTO();
		formTO.setDate(LocalDate.now());
		formTO.setDescription("");
		formTO.setState(hwItem.getState());

		Binder<HWItemRecordTO> binder = new Binder<>();
		binder.setBean(formTO);

		FormLayout winLayout = new FormLayout();
		winLayout.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
		add(winLayout);

		DatePicker eventDateField = new DatePicker("Datum");
		eventDateField.setLocale(Locale.forLanguageTag("CS"));
		binder.forField(eventDateField).asRequired("Datum musí být vyplněno").bind(HWItemRecordTO::getDate,
				HWItemRecordTO::setDate);
		winLayout.add(eventDateField);

		ComboBox<HWItemState> stateComboBox = new ComboBox<>("Stav", Arrays.asList(HWItemState.values()));
		// namísto propertyId a captionId jsou funkcionální settery a gettery
		stateComboBox.setItemLabelGenerator(HWItemState::getName);
		binder.forField(stateComboBox).bind(HWItemRecordTO::getState, HWItemRecordTO::setState);
		winLayout.add(stateComboBox);

		add(new UsedInChooser(hwItem, to -> {
			formTO.setUsedInId(to.getId());
			formTO.setUsedInName(to.getName());
		}));

        // TODO Je součástí udělat výběrem přes dialog

		TextArea descriptionField = new TextArea("Popis");
		descriptionField.setWidthFull();
		descriptionField.setHeight("200px");
		binder.forField(descriptionField).bind(HWItemRecordTO::getDescription, HWItemRecordTO::setDescription);
		winLayout.add(descriptionField, 2);

        getFooter().add(componentFactory.createDialogSubmitOrStornoLayout(e -> {
			try {
				HWItemRecordTO writeTO = originalTO == null ? new HWItemRecordTO() : originalTO;
				binder.writeBean(writeTO);
				if (originalTO == null) {
					getHWService().addItemRecord(writeTO, hwItem.getId(),hwItem.getUsedInId(), hwItem.getName());
					onSuccess(writeTO);
				} else {
					getHWService().modifyServiceNote(writeTO);
					onSuccess(writeTO);
				}
				close();
			} catch (Exception ex) {
				new ErrorDialog("Nezdařilo se zapsat nový servisní záznam").open();
			}
		}, e -> close()));

		// Poté, co je form probindován se nastaví hodnoty dle originálu
		if (originalTO != null)
			binder.readBean(originalTO);
	}

	private HWService getHWService() {
		if (hwService == null)
			hwService = SpringContextHelper.getBean(HWService.class);
		return hwService;
	}

	protected abstract void onSuccess(HWItemRecordTO noteDTO);

}