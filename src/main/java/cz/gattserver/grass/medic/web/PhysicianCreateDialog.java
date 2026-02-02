package cz.gattserver.grass.medic.web;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import com.vaadin.flow.data.binder.ValidationException;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.service.MedicService;
import cz.gattserver.grass.medic.interfaces.PhysicianTO;

public abstract class PhysicianCreateDialog extends EditWebDialog {

    private static final long serialVersionUID = -6773027334692911384L;

    public PhysicianCreateDialog() {
        this(null);
    }

    public PhysicianCreateDialog(PhysicianTO originalTO) {
        super("Lékař");
        setWidth("300px");

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(false);
        add(layout);

        Binder<PhysicianTO> binder = new Binder<>(PhysicianTO.class);
        binder.setBean(new PhysicianTO());

        final TextField nameField = new TextField("Jméno");
        layout.add(nameField);
        nameField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
        nameField.setWidthFull();
        binder.forField(nameField).asRequired(componentFactory.createRequiredLabel())
                .bind(PhysicianTO::getName, PhysicianTO::setName);

        final TextField emailField = new TextField("Email");
        layout.add(emailField);
        emailField.setWidthFull();
        binder.forField(emailField).asRequired(componentFactory.createRequiredLabel())
                .bind(PhysicianTO::getEmail, PhysicianTO::setEmail);

        final TextField phoneField = new TextField("Telefon");
        layout.add(phoneField);
        phoneField.setWidthFull();
        binder.forField(phoneField).asRequired(componentFactory.createRequiredLabel())
                .bind(PhysicianTO::getPhone, PhysicianTO::setPhone);

        getFooter().add(componentFactory.createDialogSubmitOrStornoLayout(e -> {
            PhysicianTO writeTO = originalTO == null ? new PhysicianTO() : originalTO;
            try {
                binder.writeBean(writeTO);
                SpringContextHelper.getBean(MedicService.class).savePhysician(writeTO);
                onSuccess(writeTO);
                close();
            } catch (ValidationException ex) {
                // ValidationException je zpracována přes UI a zbytek chci, aby vyskočil do error dialogu
            }
        }, e -> close()));

        if (originalTO != null) binder.readBean(originalTO);
    }

    protected abstract void onSuccess(PhysicianTO to);

}
