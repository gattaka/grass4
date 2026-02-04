package cz.gattserver.grass.medic.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;

import com.vaadin.flow.data.binder.ValidationException;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.grass.core.ui.util.TokenField;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.service.MedicService;
import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;
import cz.gattserver.grass.medic.interfaces.MedicalRecordTO;
import cz.gattserver.grass.medic.interfaces.MedicamentTO;
import cz.gattserver.grass.medic.interfaces.PhysicianTO;
import cz.gattserver.grass.medic.interfaces.ScheduledVisitTO;

public class MedicalRecordDialog extends EditWebDialog {

    private final MedicService medicService;

    public static MedicalRecordDialog detail(MedicalRecordTO originalTO) {
        return new MedicalRecordDialog(null, originalTO, null, true);
    }

    public static MedicalRecordDialog edit(MedicalRecordTO originalTO, Consumer<MedicalRecordTO> onSave) {
        return new MedicalRecordDialog(null, originalTO, onSave, false);
    }

    public static MedicalRecordDialog create(ScheduledVisitTO scheduledVisitDTO, Consumer<MedicalRecordTO> onSave) {
        return new MedicalRecordDialog(scheduledVisitDTO, null, onSave, false);
    }

    private MedicalRecordDialog(ScheduledVisitTO scheduledVisitTO, MedicalRecordTO originalTO,
                                Consumer<MedicalRecordTO> onSave, boolean readOnly) {
        super("Záznam", readOnly);
        setWidth("800px");

        this.medicService = SpringContextHelper.getBean(MedicService.class);

        Binder<MedicalRecordTO> binder = new Binder<>(MedicalRecordTO.class);
        binder.setBean(new MedicalRecordTO());

        final ComboBox<MedicalInstitutionTO> institutionComboBox =
                new ComboBox<>("Instituce", medicService.getMedicalInstitutions());
        institutionComboBox.setWidthFull();
        institutionComboBox.setReadOnly(readOnly);
        componentFactory.attachLink(institutionComboBox, f -> MedicalInstitutionDialog.detail(
                medicService.getMedicalInstitutionById(originalTO.getInstitution().getId())).open());
        binder.forField(institutionComboBox).asRequired(componentFactory.createRequiredLabel())
                .bind(MedicalRecordTO::getInstitution, MedicalRecordTO::setInstitution);

        Set<PhysicianTO> physicians = medicService.getPhysicians();
        final ComboBox<PhysicianTO> physicianComboBox = new ComboBox<>("Ošetřující lékař", physicians);
        physicianComboBox.setWidthFull();
        physicianComboBox.setReadOnly(readOnly);
        componentFactory.attachLink(physicianComboBox, f -> PhysicianDialog.detail(
                medicService.getPhysicianById(originalTO.getPhysician().getId())).open());
        binder.forField(physicianComboBox).bind(MedicalRecordTO::getPhysician, MedicalRecordTO::setPhysician);

        institutionComboBox.addValueChangeListener(e -> {
            if (institutionComboBox.getValue() == null || physicianComboBox.getValue() != null) return;
            MedicalInstitutionTO to = institutionComboBox.getValue();
            PhysicianTO pTO = medicService.getPhysicianByLastVisit(to.getId());
            if (pTO != null) physicianComboBox.setValue(pTO);
        });

        HorizontalLayout line1 = new HorizontalLayout(institutionComboBox, physicianComboBox);
        line1.setWidthFull();
        line1.setPadding(false);
        layout.add(line1);

        ComponentFactory componentFactory = new ComponentFactory();

        final DateTimePicker dateTimePicker = componentFactory.createDateTimePicker("Datum návštěvy");
        dateTimePicker.setReadOnly(readOnly);
        binder.forField(dateTimePicker).asRequired(componentFactory.createRequiredLabel())
                .bind(MedicalRecordTO::getDateTime, MedicalRecordTO::setDateTime);

        HorizontalLayout line2 = new HorizontalLayout(dateTimePicker);
        line2.setWidthFull();
        line2.setPadding(false);
        layout.add(line2);

        final TextArea recordField = new TextArea("Záznam");
        layout.add(recordField);
        recordField.setWidthFull();
        recordField.setHeight("200px");
        recordField.setReadOnly(readOnly);
        binder.forField(recordField).asRequired(componentFactory.createRequiredLabel())
                .bind(MedicalRecordTO::getRecord, MedicalRecordTO::setRecord);

        Map<String, MedicamentTO> medicaments = new HashMap<>();
        for (MedicamentTO mto : medicService.getMedicaments())
            medicaments.put(mto.getName(), mto);

        TokenField tokenField = new TokenField(medicaments.keySet());
        tokenField.setAllowNewItems(false);
        tokenField.setPlaceholder("Medikamenty");
        if (originalTO != null) for (MedicamentTO m : originalTO.getMedicaments())
            tokenField.addToken(m.getName());

        if (readOnly) {
            Div tags = componentFactory.createButtonLayout();
            tags.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
            originalTO.getMedicaments().forEach(med -> tags.add(new Button(med.getName())));
            layout.add(tags);
        } else {
            layout.add(tokenField);
        }

        getFooter().add(componentFactory.createDialogSubmitOrStornoLayout(e -> {
            try {
                MedicalRecordTO writeDTO = originalTO == null ? new MedicalRecordTO() : originalTO;
                binder.writeBean(writeDTO);
                writeDTO.setMedicaments(
                        tokenField.getValues().stream().map(medicaments::get).collect(Collectors.toSet()));
                medicService.saveMedicalRecord(writeDTO);
                onSave.accept(writeDTO);
                close();
            } catch (ValidationException ex) {
                // ValidationException je zpracována přes UI a zbytek chci, aby vyskočil do error dialogu
            }
        }, e -> close(), !readOnly));

        if (originalTO != null) binder.readBean(originalTO);

        if (scheduledVisitTO != null) {
            dateTimePicker.setValue(scheduledVisitTO.getDateTime());
            institutionComboBox.setValue(new MedicalInstitutionTO(scheduledVisitTO.getInstitutionId()));
        }
    }

}