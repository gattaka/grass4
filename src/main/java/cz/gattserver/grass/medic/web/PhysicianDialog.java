package cz.gattserver.grass.medic.web;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import com.vaadin.flow.data.binder.ValidationException;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.interfaces.PhysicianTO;

import java.io.Serial;
import java.util.function.Consumer;

public class PhysicianDialog extends EditWebDialog {

    @Serial
    private static final long serialVersionUID = -6773027334692911384L;

    public static PhysicianDialog detail(PhysicianTO originalTO) {
        return new PhysicianDialog(originalTO, null, true);
    }

    public static PhysicianDialog edit(PhysicianTO originalTO, Consumer<PhysicianTO> onSave) {
        return new PhysicianDialog(originalTO, onSave, false);
    }

    public static PhysicianDialog create(Consumer<PhysicianTO> onSave) {
        return new PhysicianDialog(null, onSave, false);
    }

    private PhysicianDialog(PhysicianTO originalTO, Consumer<PhysicianTO> onSave, boolean readOnly) {
        super("Lékař", readOnly);
        setWidth("300px");

        FormLayout formLayout = new FormLayout();
        layout.add(formLayout);

        Binder<PhysicianTO> binder = new Binder<>(PhysicianTO.class);
        binder.setBean(new PhysicianTO());

        final TextField nameField = new TextField("Jméno");
        formLayout.add(nameField);
        nameField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
        nameField.setWidthFull();
        nameField.setReadOnly(readOnly);
        binder.forField(nameField).asRequired(componentFactory.createRequiredLabel())
                .bind(PhysicianTO::getName, PhysicianTO::setName);

        TextField emailField = new TextField("Email");
        formLayout.add(emailField);
        emailField.setWidthFull();
        emailField.setReadOnly(readOnly);
        binder.forField(emailField).bind(PhysicianTO::getEmail, PhysicianTO::setEmail);

        TextField phoneField = new TextField("Telefon");
        formLayout.add(phoneField);
        phoneField.setWidthFull();
        phoneField.setReadOnly(readOnly);
        binder.forField(phoneField).bind(PhysicianTO::getPhone, PhysicianTO::setPhone);

        if (originalTO != null) binder.readBean(originalTO);

        getFooter().add(componentFactory.createDialogSubmitOrStornoLayout(e -> {
            try {
                PhysicianTO writeTO = originalTO == null ? new PhysicianTO() : originalTO;
                binder.writeBean(writeTO);
                onSave.accept(writeTO);
                close();
            } catch (ValidationException ex) {
                // ValidationException je zpracována přes UI a zbytek chci, aby vyskočil do error dialogu
            }
        }, e -> close(), !readOnly));

        if (originalTO != null) binder.readBean(originalTO);
    }

}
