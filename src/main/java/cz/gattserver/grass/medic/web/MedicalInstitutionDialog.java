package cz.gattserver.grass.medic.web;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import com.vaadin.flow.data.binder.ValidationException;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;

import java.util.function.Consumer;

public class MedicalInstitutionDialog extends EditWebDialog {

    public static MedicalInstitutionDialog detail(MedicalInstitutionTO originalTO) {
        return new MedicalInstitutionDialog(originalTO, null, true);
    }

    public static MedicalInstitutionDialog edit(MedicalInstitutionTO originalTO,
                                                Consumer<MedicalInstitutionTO> onSave) {
        return new MedicalInstitutionDialog(originalTO, onSave, false);
    }

    public static MedicalInstitutionDialog create(Consumer<MedicalInstitutionTO> onSave) {
        return new MedicalInstitutionDialog(null, onSave, false);
    }

    private MedicalInstitutionDialog(MedicalInstitutionTO originalTO, Consumer<MedicalInstitutionTO> onSave,
                                     boolean readOnly) {
        super("Instituce", readOnly);
        setWidth("500px");

        MedicalInstitutionTO formDTO = new MedicalInstitutionTO();
        Binder<MedicalInstitutionTO> binder = new Binder<>(MedicalInstitutionTO.class);
        binder.setBean(formDTO);

        FormLayout form = new FormLayout();
        add(form);

        TextField nameField = new TextField("Název");
        form.add(nameField);
        nameField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
        nameField.setWidthFull();
        nameField.setReadOnly(readOnly);
        binder.forField(nameField).asRequired(componentFactory.createRequiredLabel()).bind("name");

        TextField addressField = new TextField("Adresa");
        form.add(addressField);
        addressField.setWidthFull();
        addressField.setReadOnly(readOnly);
        binder.forField(addressField).asRequired(componentFactory.createRequiredLabel()).bind("address");

        TextField webField = new TextField("Webové stránky");
        form.add(webField);
        webField.setWidthFull();
        webField.setReadOnly(readOnly);
        componentFactory.attachLink(webField, f -> UI.getCurrent().getPage().open(originalTO.getWeb(), "_blank"));
        binder.forField(webField).bind("web");

        TextArea hoursField = new TextArea("Otevírací hodiny");
        form.add(hoursField);
        hoursField.setWidthFull();
        hoursField.setHeight("200px");
        hoursField.setReadOnly(readOnly);
        binder.forField(hoursField).bind("hours");

        if (originalTO != null) binder.readBean(originalTO);

        getFooter().add(componentFactory.createDialogSubmitOrStornoLayout(e -> {
            try {
                MedicalInstitutionTO writeTO = originalTO == null ? new MedicalInstitutionTO() : originalTO;
                binder.writeBean(writeTO);
                onSave.accept(writeTO);
                close();
            } catch (ValidationException ex) {
                // ValidationException je zpracována přes UI a zbytek chci, aby vyskočil do error dialogu
            }
        }, e -> close(), !readOnly));
    }
}