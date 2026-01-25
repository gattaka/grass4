package cz.gattserver.grass.medic.web;

import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import com.vaadin.flow.data.binder.ValidationException;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.service.MedicService;
import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;

public abstract class MedicalInstitutionCreateDialog extends EditWebDialog {

    private static final long serialVersionUID = -6773027334692911384L;

    private transient MedicService medicService;

    public MedicalInstitutionCreateDialog() {
        this(null);
    }

    public MedicalInstitutionCreateDialog(MedicalInstitutionTO modifiedMedicalInstitutionDTO) {
        super("Instituce");
        setWidth("500px");

        MedicalInstitutionTO formDTO = new MedicalInstitutionTO();
        Binder<MedicalInstitutionTO> binder = new Binder<>(MedicalInstitutionTO.class);
        binder.setBean(formDTO);

        final TextField nameField = new TextField("Název");
        add(nameField);
        nameField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
        nameField.setWidthFull();
        binder.forField(nameField).asRequired(componentFactory.createRequiredLabel()).bind("name");

        final TextField addressField = new TextField("Adresa");
        add(addressField);
        addressField.setWidthFull();
        binder.forField(addressField).asRequired(componentFactory.createRequiredLabel()).bind("address");

        final TextField webField = new TextField("Webové stránky");
        add(webField);
        webField.setWidthFull();
        binder.forField(webField).bind("web");

        final TextArea hoursField = new TextArea("Otevírací hodiny");
        add(hoursField);
        hoursField.setWidthFull();
        hoursField.setHeight("200px");
        binder.forField(hoursField).bind("hours");

        add(componentFactory.createDialogSubmitOrStornoLayout(e -> {
            MedicalInstitutionTO writeDTO =
                    modifiedMedicalInstitutionDTO == null ? new MedicalInstitutionTO() : modifiedMedicalInstitutionDTO;
            try {
                binder.writeBean(writeDTO);
                getMedicFacade().saveMedicalInstitution(writeDTO);
                onSuccess();
                close();
            } catch (ValidationException ex) {
                // ValidationException je zpracována přes UI a zbytek chci, aby vyskočil do error dialogu
            }
        }, e -> close()));

        if (modifiedMedicalInstitutionDTO != null) binder.readBean(modifiedMedicalInstitutionDTO);
    }

    protected MedicService getMedicFacade() {
        if (medicService == null) medicService = SpringContextHelper.getBean(MedicService.class);
        return medicService;
    }

    protected abstract void onSuccess();

}
