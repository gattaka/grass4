package cz.gattserver.grass.medic.web;

import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import com.vaadin.flow.data.binder.ValidationException;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;

import java.util.function.Consumer;

public class MedicalInstitutionDialog extends EditWebDialog {

    private static final long serialVersionUID = -6773027334692911384L;

    public static MedicalInstitutionDialog detail(MedicalInstitutionTO originalTO) {
        return new MedicalInstitutionDialog(originalTO, null, true);
    }

    public static MedicalInstitutionDialog edit(MedicalInstitutionTO originalTO, Consumer<MedicalInstitutionTO> onSave) {
        return new MedicalInstitutionDialog(originalTO, onSave, false);
    }

    public static MedicalInstitutionDialog create(Consumer<MedicalInstitutionTO> onSave) {
        return new MedicalInstitutionDialog(null, onSave, false);
    }

    private MedicalInstitutionDialog(MedicalInstitutionTO originalTO, Consumer<MedicalInstitutionTO> onSave, boolean readOnly) {
        super("Instituce");
        setWidth("500px");

        MedicalInstitutionTO formDTO = new MedicalInstitutionTO();
        Binder<MedicalInstitutionTO> binder = new Binder<>(MedicalInstitutionTO.class);
        binder.setBean(formDTO);

        final TextField nameField = new TextField("Název");
        add(nameField);
        nameField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
        nameField.setWidthFull();
        nameField.setReadOnly(readOnly);
        binder.forField(nameField).asRequired(componentFactory.createRequiredLabel()).bind("name");

        final TextField addressField = new TextField("Adresa");
        add(addressField);
        addressField.setWidthFull();
        addressField.setReadOnly(readOnly);
        binder.forField(addressField).asRequired(componentFactory.createRequiredLabel()).bind("address");

        final TextField webField = new TextField("Webové stránky");
        add(webField);
        webField.setWidthFull();
        webField.setReadOnly(readOnly);
        binder.forField(webField).bind("web");

        final TextArea hoursField = new TextArea("Otevírací hodiny");
        add(hoursField);
        hoursField.setWidthFull();
        hoursField.setHeight("200px");
        hoursField.setReadOnly(readOnly);
        binder.forField(hoursField).bind("hours");

        if (originalTO!= null)
            binder.readBean(originalTO);

        add(componentFactory.createDialogSubmitOrStornoLayout(e -> {
            try {
                MedicalInstitutionTO writeTO =
                        originalTO == null ? new MedicalInstitutionTO() : originalTO;
                binder.writeBean(writeTO);
                onSave.accept(writeTO);
                close();
            } catch (ValidationException ex) {
                // ValidationException je zpracována přes UI a zbytek chci, aby vyskočil do error dialogu
            }
        }, e -> close()));
    }
}