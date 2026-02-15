package cz.gattserver.grass.medic.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;

import com.vaadin.flow.data.binder.ValidationException;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.grass.core.ui.util.TokenField;
import cz.gattserver.grass.medic.service.MedicService;
import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;
import cz.gattserver.grass.medic.interfaces.MedicalRecordTO;
import cz.gattserver.grass.medic.interfaces.MedicamentTO;
import cz.gattserver.grass.medic.interfaces.PhysicianTO;
import cz.gattserver.grass.medic.interfaces.ScheduledVisitTO;

public class MedicalRecordDialog extends EditWebDialog {

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

        MedicService medicService = SpringContextHelper.getBean(MedicService.class);

        Binder<MedicalRecordTO> binder = new Binder<>(MedicalRecordTO.class);
        MedicalRecordTO beanTO = new MedicalRecordTO();
        if (scheduledVisitTO != null) {
            beanTO.setDateTime(scheduledVisitTO.getDateTime());
            beanTO.setInstitutionId(scheduledVisitTO.getInstitutionId());
        }
        binder.setBean(beanTO);

        List<MedicalInstitutionTO> medicalInstitutionTOList = medicService.getMedicalInstitutions();
        ComboBox<MedicalInstitutionTO> institutionComboBox = new ComboBox<>("Instituce", medicalInstitutionTOList);
        institutionComboBox.setWidthFull();
        institutionComboBox.setReadOnly(readOnly);
        componentFactory.attachLink(institutionComboBox, f -> MedicalInstitutionDialog.detail(
                medicService.getMedicalInstitutionById(originalTO.getInstitutionId())).open());
        componentFactory.bind(binder.forField(institutionComboBox).asRequired(componentFactory.createRequiredLabel()),
                medicalInstitutionTOList, MedicalRecordTO::getInstitutionId, MedicalRecordTO::setInstitutionId);

        List<PhysicianTO> physicians = medicService.getPhysicians();
        ComboBox<PhysicianTO> physicianComboBox = new ComboBox<>("Ošetřující lékař", physicians);
        physicianComboBox.setWidthFull();
        physicianComboBox.setReadOnly(readOnly);
        componentFactory.attachLink(physicianComboBox,
                f -> PhysicianDialog.detail(medicService.getPhysicianById(originalTO.getPhysicianId())).open());
        componentFactory.bind(binder.forField(physicianComboBox), physicians, MedicalRecordTO::getPhysicianId,
                MedicalRecordTO::setPhysicianId);

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

        DateTimePicker dateTimePicker = componentFactory.createDateTimePicker("Datum návštěvy");
        dateTimePicker.setReadOnly(readOnly);
        binder.forField(dateTimePicker).asRequired(componentFactory.createRequiredLabel())
                .bind(MedicalRecordTO::getDateTime, MedicalRecordTO::setDateTime);

        HorizontalLayout line2 = new HorizontalLayout(dateTimePicker);
        line2.setWidthFull();
        line2.setPadding(false);
        layout.add(line2);

        TextArea recordField = new TextArea("Záznam");
        layout.add(recordField);
        recordField.setWidthFull();
        recordField.setHeight("200px");
        recordField.setReadOnly(readOnly);
        binder.forField(recordField).asRequired(componentFactory.createRequiredLabel())
                .bind(MedicalRecordTO::getRecord, MedicalRecordTO::setRecord);

        Map<String, Long> medicamentsNameToIdMap = new HashMap<>();
        Map<Long, String> medicamentIdToNameMap = new HashMap<>();
        for (MedicamentTO mto : medicService.getMedicaments()) {
            medicamentsNameToIdMap.put(mto.getName(), mto.getId());
            medicamentIdToNameMap.put(mto.getId(), mto.getName());
        }

        TokenField tokenField = new TokenField("Medikamenty", medicamentsNameToIdMap.keySet());
        tokenField.setAllowNewItems(false);
        tokenField.setReadOnly(readOnly);
        binder.forField(tokenField).bind(to -> to.getMedicaments().stream().map(m -> medicamentIdToNameMap.get(m))
                .collect(Collectors.toSet()), (to, val) -> {
            to.setMedicaments(val.stream().map(m -> medicamentsNameToIdMap.get(m)).collect(Collectors.toSet()));
        });
        layout.add(tokenField);

        getFooter().add(componentFactory.createDialogSubmitOrStornoLayout(e -> {
            try {
                MedicalRecordTO to = originalTO == null ? new MedicalRecordTO() : originalTO;
                binder.writeBean(to);
                onSave.accept(to);
                close();
            } catch (ValidationException ex) {
                // ValidationException je zpracována přes UI a zbytek chci, aby vyskočil do error dialogu
            }
        }, e -> close(), !readOnly));

        if (originalTO != null) binder.readBean(originalTO);
    }

}