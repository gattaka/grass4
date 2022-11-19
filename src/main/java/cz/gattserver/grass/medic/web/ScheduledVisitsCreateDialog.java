package cz.gattserver.grass.medic.web;

import java.util.List;
import java.util.Locale;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.core.ui.components.SaveCloseLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;

import cz.gattserver.grass.medic.facade.MedicFacade;
import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;
import cz.gattserver.grass.medic.interfaces.MedicalRecordTO;
import cz.gattserver.grass.medic.interfaces.ScheduledVisitState;
import cz.gattserver.grass.medic.interfaces.ScheduledVisitTO;

public abstract class ScheduledVisitsCreateDialog extends WebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private static final Logger logger = LoggerFactory.getLogger(ScheduledVisitsCreateDialog.class);

	public ScheduledVisitsCreateDialog(Operation operation) {
		this(operation, null);
	}

	public ScheduledVisitsCreateDialog(Operation operation, ScheduledVisitTO originalDTO) {
		boolean planned = operation.equals(Operation.PLANNED) || operation.equals(Operation.PLANNED_FROM_TO_BE_PLANNED);

		MedicFacade medicalFacade = SpringContextHelper.getBean(MedicFacade.class);

		setWidth("400px");

		ScheduledVisitTO formDTO = new ScheduledVisitTO();
		formDTO.setPurpose("");
		formDTO.setPlanned(planned);
		formDTO.setState(planned ? ScheduledVisitState.PLANNED : ScheduledVisitState.TO_BE_PLANNED);

		Binder<ScheduledVisitTO> binder = new Binder<>(ScheduledVisitTO.class);
		binder.setBean(formDTO);

		final TextField purposeField = new TextField("Účel návštěvy");
		add(purposeField);
		purposeField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
		purposeField.setWidthFull();
		binder.forField(purposeField).asRequired().bind("purpose");

		if (!planned) {
			final TextField periodField = new TextField("Pravidelnost (měsíce)");
			add(periodField);
			periodField.setWidthFull();
			binder.forField(periodField)
					.withConverter(new StringToIntegerConverter(0, "Počet měsíců musí být celé číslo")).bind("period");
		}

		final DatePicker dateField = new DatePicker("Datum návštěvy");
		dateField.setLocale(Locale.forLanguageTag("CS"));
		add(dateField);
		dateField.setWidthFull();
		binder.forField(dateField).bind("date");

		if (planned) {
			final TimePicker timeField = new TimePicker("Čas návštěvy");
			timeField.setLocale(Locale.forLanguageTag("CS"));
			add(timeField);
			timeField.setWidthFull();
			binder.forField(timeField).bind("time");
		}

		dateField.setWidthFull();
		binder.forField(dateField).asRequired().bind("date");

		List<MedicalRecordTO> records = medicalFacade.getAllMedicalRecords();
		final ComboBox<MedicalRecordTO> recordsComboBox = new ComboBox<>("Navazuje na kontrolu", records);
		add(recordsComboBox);
		recordsComboBox.setWidthFull();
		binder.forField(recordsComboBox).bind("record");

		List<MedicalInstitutionTO> institutions = medicalFacade.getAllMedicalInstitutions();
		final ComboBox<MedicalInstitutionTO> institutionComboBox = new ComboBox<>("Instituce", institutions);
		add(institutionComboBox);
		institutionComboBox.setWidthFull();
		binder.forField(institutionComboBox).asRequired().bind("institution");

		add(new SaveCloseLayout(e -> {
			ScheduledVisitTO writeDTO = originalDTO == null ? formDTO : originalDTO;
			if (binder.writeBeanIfValid(writeDTO)) {
				try {
					medicalFacade.saveScheduledVisit(writeDTO);
					onSuccess();
					close();
				} catch (Exception ex) {
					String msg = "Nezdařilo se vytvořit nový záznam";
					new ErrorDialog(msg).open();
					logger.error(msg, ex);
				}
			}
		}, e -> close()));

		if (originalDTO != null)
			binder.readBean(originalDTO);

		// vyplňuji objednání na základě plánovaného objednání
		if (originalDTO != null && planned) {
			purposeField.setValue(originalDTO.getPurpose());
			recordsComboBox.setValue(originalDTO.getRecord());
			institutionComboBox.setValue(originalDTO.getInstitution());
		}
	}

	protected abstract void onSuccess();

}
