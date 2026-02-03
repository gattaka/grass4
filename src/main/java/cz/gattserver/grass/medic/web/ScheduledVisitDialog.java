package cz.gattserver.grass.medic.web;

import java.io.Serial;
import java.time.YearMonth;
import java.util.List;
import java.util.function.Consumer;

import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.ValidationException;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;

import cz.gattserver.grass.medic.service.MedicService;
import org.vaadin.addons.componentfactory.monthpicker.MonthPicker;

public class ScheduledVisitDialog extends WebDialog {

    @Serial
    private static final long serialVersionUID = -6773027334692911384L;

    private static final Logger logger = LoggerFactory.getLogger(ScheduledVisitDialog.class);

    public static ScheduledVisitDialog detail(ScheduledVisitTO originalTO) {
        return new ScheduledVisitDialog(originalTO, null, null, true);
    }

    public static ScheduledVisitDialog edit(ScheduledVisitTO originalTO, Consumer<ScheduledVisitTO> onSave) {
        return new ScheduledVisitDialog(originalTO, null, onSave, false);
    }

    public static ScheduledVisitDialog create(Consumer<ScheduledVisitTO> onSave) {
        return new ScheduledVisitDialog(new ScheduledVisitTO(true), null, onSave, false);
    }

    public static ScheduledVisitDialog create(ScheduledVisitTO fromToBePlannedTO, Consumer<ScheduledVisitTO> onSave) {
        return new ScheduledVisitDialog(new ScheduledVisitTO(true), fromToBePlannedTO, onSave, false);
    }

    public static ScheduledVisitDialog createToBePlanned(Consumer<ScheduledVisitTO> onSave) {
        return new ScheduledVisitDialog(new ScheduledVisitTO(false), null, onSave, false);
    }

    private ScheduledVisitDialog(ScheduledVisitTO originalOrNewTO, ScheduledVisitTO fromToBePlannedTO,
                                 Consumer<ScheduledVisitTO> onSave, boolean readOnly) {
        super("Návštěva");

        MedicService medicService = SpringContextHelper.getBean(MedicService.class);

        ComponentFactory componentFactory = new ComponentFactory();

        setWidth("400px");
        setResizable(true);

        ScheduledVisitTO formTO = originalOrNewTO.copy();
        if (fromToBePlannedTO != null) {
            formTO.setPurpose(fromToBePlannedTO.getPurpose());
            formTO.setRecordId(fromToBePlannedTO.getRecordId());
            formTO.setInstitutionId(fromToBePlannedTO.getInstitutionId());
        }

        Binder<ScheduledVisitTO> binder = new Binder<>(ScheduledVisitTO.class);
        binder.setBean(formTO);

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
        layout.add(formLayout);

        TextField purposeField = new TextField("Účel návštěvy");
        formLayout.add(purposeField);
        formLayout.setColspan(purposeField, 2);
        purposeField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
        purposeField.setWidthFull();
        purposeField.setReadOnly(readOnly);
        binder.forField(purposeField).asRequired(componentFactory.createRequiredLabel())
                .bind(ScheduledVisitTO::getPurpose, ScheduledVisitTO::setPurpose);

        if (Boolean.TRUE == formTO.getPlanned()) {
            DateTimePicker dateTimePicker = componentFactory.createDateTimePicker("Datum návštěvy");
            formLayout.add(dateTimePicker);
            dateTimePicker.setReadOnly(readOnly);
            binder.forField(dateTimePicker).asRequired(componentFactory.createRequiredLabel())
                    .bind(ScheduledVisitTO::getDateTime, ScheduledVisitTO::setDateTime);
            formLayout.setColspan(dateTimePicker, 2);
        } else {
            MonthPicker monthPicker = componentFactory.createMonthPicker("Datum");
            formLayout.add(monthPicker);
            monthPicker.setReadOnly(readOnly);
            binder.forField(monthPicker).asRequired(componentFactory.createRequiredLabel())
                    .bind(to -> to.getDateTime() == null ? null : YearMonth.from(to.getDateTime()),
                            (to, val) -> to.setDateTime(val == null ? null : val.atEndOfMonth().atStartOfDay()));

            TextField periodField = new TextField("Pravidelnost (měsíce)");
            formLayout.add(periodField);
            periodField.setWidthFull();
            periodField.setReadOnly(readOnly);
            binder.forField(periodField)
                    .withConverter(new StringToIntegerConverter(0, "Počet měsíců musí být celé číslo")).bind("period");
        }

        List<MedicalInstitutionTO> institutions = medicService.getMedicalInstitutions();
        ComboBox<MedicalInstitutionTO> institutionComboBox = new ComboBox<>("Instituce", institutions);
        formLayout.setColspan(institutionComboBox, 2);
        formLayout.add(institutionComboBox);
        institutionComboBox.setWidthFull();
        institutionComboBox.setReadOnly(readOnly);
        componentFactory.bind(binder.forField(institutionComboBox).asRequired(componentFactory.createRequiredLabel()),
                institutions, ScheduledVisitTO::getInstitutionId, ScheduledVisitTO::setInstitutionId);

        List<MedicalRecordTO> records = medicService.getMedicalRecords();
        ComboBox<MedicalRecordTO> recordsComboBox = new ComboBox<>("Navazuje na kontrolu", records);
        formLayout.setColspan(recordsComboBox, 2);
        formLayout.add(recordsComboBox);
        recordsComboBox.setWidthFull();
        recordsComboBox.setReadOnly(readOnly);
        componentFactory.bind(binder.forField(recordsComboBox), records, ScheduledVisitTO::getRecordId,
                ScheduledVisitTO::setRecordId);

        getFooter().add(componentFactory.createDialogSubmitOrStornoLayout(e -> {
            try {
                binder.writeBean(formTO);
                onSave.accept(formTO);
                close();
            } catch (ValidationException ex) {
                // ValidationException je zpracována přes UI a zbytek chci, aby vyskočil do error dialogu
            }
        }, e -> close()));
    }
}