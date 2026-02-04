package cz.gattserver.grass.medic.web;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import com.vaadin.flow.data.binder.ValidationException;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.interfaces.MedicamentTO;

import java.util.function.Consumer;

public class MedicamentDialog extends EditWebDialog {

    private static final long serialVersionUID = -6773027334692911384L;

    public static MedicamentDialog detail(MedicamentTO originalTO) {
        return new MedicamentDialog(originalTO, null, true);
    }

    public static MedicamentDialog edit(MedicamentTO originalTO, Consumer<MedicamentTO> onSave) {
        return new MedicamentDialog(originalTO, onSave, false);
    }

    public static MedicamentDialog create(Consumer<MedicamentTO> onSave) {
        return new MedicamentDialog(null, onSave, false);
    }

    private MedicamentDialog(MedicamentTO originalTO, Consumer<MedicamentTO> onSave, boolean readOnly) {
        super("Medikament", readOnly);
        setWidth(400, Unit.PIXELS);

        MedicamentTO formDTO = new MedicamentTO();
        Binder<MedicamentTO> binder = new Binder<>(MedicamentTO.class);
        binder.setBean(formDTO);

        FormLayout formLayout = new FormLayout();
        add(formLayout);

        TextField nameField = new TextField("Název");
        formLayout.add(nameField);
        nameField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
        nameField.setWidthFull();
        nameField.setReadOnly(readOnly);
        binder.forField(nameField).asRequired(componentFactory.createRequiredLabel()).bind("name");

        TextArea toleranceField = new TextArea("Reakce, nežádoucí účinky");
        formLayout.add(toleranceField);
        toleranceField.setHeight("200px");
        toleranceField.setWidthFull();
        toleranceField.setReadOnly(readOnly);
        binder.forField(toleranceField).bind("tolerance");

        getFooter().add(componentFactory.createDialogSubmitOrStornoLayout(e -> {
            try {
                MedicamentTO writeDTO = originalTO == null ? new MedicamentTO() : originalTO;
                binder.writeBean(writeDTO);
                onSave.accept(writeDTO);
                close();
            } catch (ValidationException ex) {
                // ValidationException je zpracována přes UI a zbytek chci, aby vyskočil do error dialogu
            }
        }, e -> close()));

        if (originalTO != null) binder.readBean(originalTO);
    }
}