package cz.gattserver.grass.medic.web;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import com.vaadin.flow.data.binder.ValidationException;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
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

        Binder<PhysicianTO> binder = new Binder<>(PhysicianTO.class);
        binder.setBean(new PhysicianTO());

        final TextField nameField = new TextField("Jméno");
        add(nameField);
        nameField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
        nameField.setWidthFull();
        binder.forField(nameField).asRequired(componentFactory.createRequiredLabel()).bind("name");

        add(componentFactory.createDialogSubmitOrCloseLayout(e -> {
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
