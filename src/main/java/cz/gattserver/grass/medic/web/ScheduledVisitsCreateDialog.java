package cz.gattserver.grass.medic.web;

import java.util.List;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
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

import cz.gattserver.grass.medic.service.MedicService;
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

	public ScheduledVisitsCreateDialog(Operation operation, ScheduledVisitTO originalTO) {
		boolean planned =
				operation.equals(Operation.PLANNED) || operation.equals(Operation.PLANNED_FROM_TO_BE_PLANNED);

		MedicService medicalFacade = SpringContextHelper.getBean(MedicService.class);

		setWidth("400px");

		ScheduledVisitTO formTO = new ScheduledVisitTO();
		formTO.setPurpose("");
		formTO.setPlanned(planned);
		formTO.setState(planned ? ScheduledVisitState.PLANNED : ScheduledVisitState.TO_BE_PLANNED);

		Binder<ScheduledVisitTO> binder = new Binder<>(ScheduledVisitTO.class);
		binder.setBean(formTO);

		final TextField purposeField = new TextField("Účel návštěvy");
		add(purposeField);
		purposeField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
		purposeField.setWidthFull();
		binder.forField(purposeField).asRequired().bind(ScheduledVisitTO::getPurpose, ScheduledVisitTO::setPurpose);

		if (!planned) {
			final TextField periodField = new TextField("Pravidelnost (měsíce)");
			add(periodField);
			periodField.setWidthFull();
			binder.forField(periodField)
					.withConverter(new StringToIntegerConverter(0, "Počet měsíců musí být celé číslo")).bind("period");
		}

		ComponentFactory componentFactory = new ComponentFactory();

		final DatePicker dateField = componentFactory.createDatePicker("Datum návštěvy");
		add(dateField);
		binder.forField(dateField).asRequired().bind(ScheduledVisitTO::getDate, ScheduledVisitTO::setDate);

		if (planned) {
			final TimePicker timeField = componentFactory.createTimePicker("Čas návštěvy");
			add(timeField);
			binder.forField(timeField).asRequired().bind(ScheduledVisitTO::getTime, ScheduledVisitTO::setTime);
		}

		List<MedicalInstitutionTO> institutions = medicalFacade.getMedicalInstitutions();
		final ComboBox<MedicalInstitutionTO> institutionComboBox = new ComboBox<>("Instituce", institutions);
		add(institutionComboBox);
		institutionComboBox.setWidthFull();
		binder.forField(institutionComboBox).asRequired().bind(ScheduledVisitTO::getInstitution,
				ScheduledVisitTO::setInstitution);

		List<MedicalRecordTO> records = medicalFacade.getMedicalRecords();
		final ComboBox<MedicalRecordTO> recordsComboBox = new ComboBox<>("Navazuje na kontrolu", records);
		add(recordsComboBox);
		recordsComboBox.setWidthFull();
		binder.forField(recordsComboBox).bind(ScheduledVisitTO::getRecord, ScheduledVisitTO::setRecord);

		add(new SaveCloseLayout(e -> {
			ScheduledVisitTO writeTO = originalTO == null ? formTO : originalTO;
			if (binder.writeBeanIfValid(writeTO)) {
				try {
					medicalFacade.saveScheduledVisit(writeTO);
					onSuccess();
					close();
				} catch (Exception ex) {
					String msg = "Nezdařilo se vytvořit nový záznam";
					new ErrorDialog(msg).open();
					logger.error(msg, ex);
				}
			}
		}, e -> close()));

		if (originalTO != null)
			binder.readBean(originalTO);

		// vyplňuji objednání na základě plánovaného objednání
		if (originalTO != null && planned) {
			purposeField.setValue(originalTO.getPurpose());
			recordsComboBox.setValue(originalTO.getRecord());
			institutionComboBox.setValue(originalTO.getInstitution());
		}
	}

	protected abstract void onSuccess();

}
