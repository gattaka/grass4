package cz.gattserver.grass.medic.web;

import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import com.vaadin.flow.data.binder.ValidationException;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.service.MedicService;
import cz.gattserver.grass.medic.interfaces.MedicamentTO;

public abstract class MedicamentCreateDialog extends EditWebDialog {

    private static final long serialVersionUID = -6773027334692911384L;

    private transient MedicService medicService;

    public MedicamentCreateDialog() {
        this(null);
    }

    public MedicamentCreateDialog(MedicamentTO originalDTO) {
        super("Medikament");
        setWidth("300px");

        MedicamentTO formDTO = new MedicamentTO();
        Binder<MedicamentTO> binder = new Binder<>(MedicamentTO.class);
        binder.setBean(formDTO);

        final TextField nameField = new TextField("Název");
        add(nameField);
        nameField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
        nameField.setWidthFull();
        binder.forField(nameField).asRequired(componentFactory.createRequiredLabel()).bind("name");

        final TextArea toleranceField = new TextArea("Reakce, nežádoucí účinky");
        add(toleranceField);
        toleranceField.setHeight("200px");
        toleranceField.setWidthFull();
        binder.forField(toleranceField).bind("tolerance");

        add(componentFactory.createDialogSubmitOrStornoLayout(e -> {
            MedicamentTO writeDTO = originalDTO == null ? new MedicamentTO() : originalDTO;
            try {
                binder.writeBean(writeDTO);
                getMedicFacade().saveMedicament(writeDTO);
                onSuccess();
                close();
            } catch (ValidationException ex) {
                // ValidationException je zpracována přes UI a zbytek chci, aby vyskočil do error dialogu
            }
        }, e -> close()));

        if (originalDTO != null) binder.readBean(originalDTO);
    }

    protected MedicService getMedicFacade() {
        if (medicService == null) medicService = SpringContextHelper.getBean(MedicService.class);
        return medicService;
    }

    protected abstract void onSuccess();

}
