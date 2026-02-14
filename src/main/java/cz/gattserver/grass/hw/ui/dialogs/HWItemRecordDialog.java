package cz.gattserver.grass.hw.ui.dialogs;

import java.time.LocalDate;
import java.util.Locale;
import java.util.function.Consumer;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;

import com.vaadin.flow.data.binder.ValidationException;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.grass.hw.interfaces.HWItemRecordTO;

public class HWItemRecordDialog extends EditWebDialog {

    public static HWItemRecordDialog create(Long hwItemId, Consumer<HWItemRecordTO> onSave) {
        return new HWItemRecordDialog(null, onSave, false);
    }

    public static HWItemRecordDialog detail(HWItemRecordTO originalTO) {
        return new HWItemRecordDialog(originalTO, null, true);
    }

    public static HWItemRecordDialog edit(HWItemRecordTO originalTO, Consumer<HWItemRecordTO> onSave) {
        return new HWItemRecordDialog(originalTO, onSave, false);
    }

    private HWItemRecordDialog(HWItemRecordTO originalTO, Consumer<HWItemRecordTO> onSave, boolean readOnly) {
        super("Záznam", readOnly);

        setResizable(true);
        setWidth(500, Unit.PIXELS);
        setHeight(500, Unit.PIXELS);
        layout.setSizeFull();

        HWItemRecordTO formTO = originalTO == null ? new HWItemRecordTO() : originalTO.copy();
        if (originalTO == null) {
            formTO.setDate(LocalDate.now());
            formTO.setDescription("");
        }

        Binder<HWItemRecordTO> binder = new Binder<>();
        binder.setBean(formTO);

        DatePicker eventDateField = new DatePicker("Datum");
        eventDateField.setWidth(200, Unit.PIXELS);
        eventDateField.setLocale(Locale.forLanguageTag("CS"));
        binder.forField(eventDateField).asRequired("Datum musí být vyplněno")
                .bind(HWItemRecordTO::getDate, HWItemRecordTO::setDate);
        eventDateField.setReadOnly(readOnly);
        layout.add(eventDateField);

        TextArea descriptionField = new TextArea("Popis");
        descriptionField.setWidthFull();
        descriptionField.setHeightFull();
        descriptionField.setReadOnly(readOnly);
        binder.forField(descriptionField).bind(HWItemRecordTO::getDescription, HWItemRecordTO::setDescription);
        layout.add(descriptionField);

        getFooter().add(componentFactory.createDialogSubmitOrStornoLayout(e -> {
            try {
                HWItemRecordTO writeTO = binder.getBean();
                binder.writeBean(writeTO);
                onSave.accept(writeTO);
                close();
            } catch (ValidationException ex) {
                // Validační chyby se zobrazí přímo v UI elementech
            }
        }, e -> close(), !readOnly));

        // Poté, co je form probindován se nastaví hodnoty dle originálu
        if (originalTO != null) binder.readBean(originalTO);
    }

}