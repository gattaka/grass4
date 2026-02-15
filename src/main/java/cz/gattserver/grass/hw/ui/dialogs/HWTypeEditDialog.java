package cz.gattserver.grass.hw.ui.dialogs;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import com.vaadin.flow.data.binder.ValidationException;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.hw.interfaces.HWTypeTO;

import java.util.function.Consumer;

public class HWTypeEditDialog extends EditWebDialog {

    public static HWTypeEditDialog detail(HWTypeTO originalTO) {
        return new HWTypeEditDialog(originalTO, null, true);
    }

    public static HWTypeEditDialog edit(HWTypeTO originalTO, Consumer<HWTypeTO> onSave) {
        return new HWTypeEditDialog(originalTO, onSave, false);
    }

    public static HWTypeEditDialog create(Consumer<HWTypeTO> onSave) {
        return new HWTypeEditDialog(null, onSave, false);
    }

    private HWTypeEditDialog(HWTypeTO originalTO, Consumer<HWTypeTO> onSave, boolean readOnly) {
        super("Typ", readOnly);

        setWidth(300, Unit.PIXELS);

        HWTypeTO formTO = originalTO == null ? new HWTypeTO() : originalTO.copy();
        Binder<HWTypeTO> binder = new Binder<>(HWTypeTO.class);
        binder.setBean(formTO);

        final TextField nameField = new TextField();
        nameField.setPlaceholder("Typ HW");
        nameField.setWidthFull();
        nameField.setReadOnly(readOnly);
        binder.forField(nameField).asRequired(componentFactory.createRequiredLabel()).bind(HWTypeTO::getName, HWTypeTO::setName);

        add(nameField);

        getFooter().add(componentFactory.createDialogSubmitOrStornoLayout(e -> {
            try {
                binder.writeBean(formTO);
                onSave.accept(formTO);
                close();
            } catch (ValidationException ex) {
                // UI
            }
        }, e -> close(), saveButton -> saveButton.addClickShortcut(Key.ENTER)));

        if (formTO != null) binder.readBean(formTO);

        nameField.focus();
    }
}