package cz.gattserver.grass.medic.web;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.core.ui.components.SaveCloseLayout;
import cz.gattserver.grass.core.ui.util.TokenField;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.service.MedicService;
import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;
import cz.gattserver.grass.medic.interfaces.MedicalRecordTO;
import cz.gattserver.grass.medic.interfaces.MedicamentTO;
import cz.gattserver.grass.medic.interfaces.PhysicianTO;
import cz.gattserver.grass.medic.interfaces.ScheduledVisitTO;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class MedicalRecordCreateDialog extends EditWebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	public MedicalRecordCreateDialog() {
		this(null, null);
	}

	public MedicalRecordCreateDialog(ScheduledVisitTO scheduledVisitDTO) {
		this(scheduledVisitDTO, null);
	}

	public MedicalRecordCreateDialog(MedicalRecordTO recordDTO) {
		this(null, recordDTO);
	}

	private MedicalRecordCreateDialog(ScheduledVisitTO scheduledVisitDTO, MedicalRecordTO originalDTO) {
		setWidth("800px");

		MedicService medicService = SpringContextHelper.getBean(MedicService.class);

		Binder<MedicalRecordTO> binder = new Binder<>(MedicalRecordTO.class);
		binder.setBean(new MedicalRecordTO());

		final ComboBox<MedicalInstitutionTO> institutionComboBox = new ComboBox<>("Instituce",
				medicService.getMedicalInstitutions());
		institutionComboBox.setWidthFull();
		binder.forField(institutionComboBox).asRequired().bind(MedicalRecordTO::getInstitution,
				MedicalRecordTO::setInstitution);

		Set<PhysicianTO> physicians = medicService.getPhysicians();
		final ComboBox<PhysicianTO> physicianComboBox = new ComboBox<>("Ošetřující lékař", physicians);
		physicianComboBox.setWidthFull();
		binder.forField(physicianComboBox).bind(MedicalRecordTO::getPhysician,
				MedicalRecordTO::setPhysician);

		institutionComboBox.addValueChangeListener(e -> {
			if (institutionComboBox.getValue() == null || physicianComboBox.getValue() != null)
				return;
			MedicalInstitutionTO to = institutionComboBox.getValue();
			PhysicianTO pTO = medicService.getPhysicianByLastVisit(to.getId());
			if (pTO != null)
				physicianComboBox.setValue(pTO);
		});

		HorizontalLayout line1 = new HorizontalLayout(institutionComboBox, physicianComboBox);
		line1.addClassName(UIUtils.TOP_PULL_CSS_CLASS);
		line1.setPadding(false);
		add(line1);

		ComponentFactory componentFactory = new ComponentFactory();

		final DatePicker dateField = componentFactory.createDatePicker("Datum návštěvy");
		binder.forField(dateField).asRequired().bind(MedicalRecordTO::getDate, MedicalRecordTO::setDate);

		final TimePicker timeField = componentFactory.createTimePicker("Čas návštěvy");
		binder.forField(timeField).bind(MedicalRecordTO::getTime, MedicalRecordTO::setTime);

		HorizontalLayout line2 = new HorizontalLayout(dateField, timeField);
		line2.setPadding(false);
		add(line2);

		final TextArea recordField = new TextArea("Záznam");
		add(recordField);
		recordField.setWidthFull();
		recordField.setHeight("200px");
		binder.forField(recordField).asRequired().bind(MedicalRecordTO::getRecord, MedicalRecordTO::setRecord);

		Map<String, MedicamentTO> medicaments = new HashMap<>();
		for (MedicamentTO mto : medicService.getMedicaments())
			medicaments.put(mto.getName(), mto);

		TokenField tokenField = new TokenField(medicaments.keySet());
		tokenField.setAllowNewItems(false);
		tokenField.setPlaceholder("Medikamenty");
		if (originalDTO != null)
			for (MedicamentTO m : originalDTO.getMedicaments())
				tokenField.addToken(m.getName());
		add(tokenField);

		add(new SaveCloseLayout(e -> {
			MedicalRecordTO writeDTO = originalDTO == null ? new MedicalRecordTO() : originalDTO;
			if (binder.writeBeanIfValid(writeDTO)) {
				try {
					writeDTO.setMedicaments(
							tokenField.getValues().stream().map(medicaments::get).collect(Collectors.toSet()));
					medicService.saveMedicalRecord(writeDTO);
					onSuccess();
					close();
				} catch (Exception ex) {
					new ErrorDialog("Nezdařilo se uložit nový záznam").open();
				}
			}
		}, e -> close()));

		if (originalDTO != null)
			binder.readBean(originalDTO);

		if (scheduledVisitDTO != null) {
			dateField.setValue(scheduledVisitDTO.getDate());
			timeField.setValue(scheduledVisitDTO.getTime());
			institutionComboBox.setValue(scheduledVisitDTO.getInstitution());
		}
	}

	protected abstract void onSuccess();

}