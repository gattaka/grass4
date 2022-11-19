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
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.core.ui.components.SaveCloseLayout;
import cz.gattserver.grass.core.ui.util.TokenField;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.facade.MedicFacade;
import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;
import cz.gattserver.grass.medic.interfaces.MedicalRecordTO;
import cz.gattserver.grass.medic.interfaces.MedicamentTO;
import cz.gattserver.grass.medic.interfaces.PhysicianTO;
import cz.gattserver.grass.medic.interfaces.ScheduledVisitTO;

public abstract class MedicalRecordCreateDialog extends EditWebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient MedicFacade medicFacade;

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

		Binder<MedicalRecordTO> binder = new Binder<>(MedicalRecordTO.class);
		binder.setBean(new MedicalRecordTO());

		final ComboBox<MedicalInstitutionTO> institutionComboBox = new ComboBox<>("Instituce",
				getMedicFacade().getAllMedicalInstitutions());
		institutionComboBox.setWidthFull();
		binder.forField(institutionComboBox).asRequired().bind("institution");

		Set<PhysicianTO> physicians = getMedicFacade().getAllPhysicians();
		final ComboBox<PhysicianTO> physicianComboBox = new ComboBox<>("Ošetřující lékař", physicians);
		physicianComboBox.setWidthFull();
		binder.forField(physicianComboBox).asRequired().bind("physician");

		HorizontalLayout line1 = new HorizontalLayout(institutionComboBox, physicianComboBox);
		line1.addClassName(UIUtils.TOP_PULL_CSS_CLASS);
		line1.setPadding(false);
		add(line1);

		final DatePicker dateField = new DatePicker("Datum návštěvy");
		dateField.setLocale(Locale.forLanguageTag("CS"));
		dateField.setWidthFull();
		binder.forField(dateField).asRequired().bind("date");

		final TimePicker timeField = new TimePicker("Čas návštěvy");
		timeField.setLocale(Locale.forLanguageTag("CS"));
		timeField.setWidthFull();
		binder.forField(timeField).bind("time");

		HorizontalLayout line2 = new HorizontalLayout(dateField, timeField);
		line2.setPadding(false);
		add(line2);

		final TextArea recordField = new TextArea("Záznam");
		add(recordField);
		recordField.setWidthFull();
		recordField.setHeight("200px");
		binder.forField(recordField).asRequired().bind("record");

		Map<String, MedicamentTO> medicaments = new HashMap<String, MedicamentTO>();
		for (MedicamentTO mto : getMedicFacade().getAllMedicaments())
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
					getMedicFacade().saveMedicalRecord(writeDTO);
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

	protected MedicFacade getMedicFacade() {
		if (medicFacade == null)
			medicFacade = SpringContextHelper.getBean(MedicFacade.class);
		return medicFacade;
	}

	protected abstract void onSuccess();

}
