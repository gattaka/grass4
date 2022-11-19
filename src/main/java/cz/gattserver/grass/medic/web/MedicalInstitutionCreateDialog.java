package cz.gattserver.grass.medic.web;

import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.core.ui.components.SaveCloseLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.facade.MedicFacade;
import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;

public abstract class MedicalInstitutionCreateDialog extends EditWebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient MedicFacade medicFacade;

	public MedicalInstitutionCreateDialog() {
		this(null);
	}

	public MedicalInstitutionCreateDialog(MedicalInstitutionTO modifiedMedicalInstitutionDTO) {
		setWidth("500px");

		MedicalInstitutionTO formDTO = new MedicalInstitutionTO();
		Binder<MedicalInstitutionTO> binder = new Binder<>(MedicalInstitutionTO.class);
		binder.setBean(formDTO);

		final TextField nameField = new TextField("Název");
		add(nameField);
		nameField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
		nameField.setWidthFull();
		binder.forField(nameField).bind("name");

		final TextField addressField = new TextField("Adresa");
		add(addressField);
		addressField.setWidthFull();
		binder.forField(addressField).bind("address");

		final TextField webField = new TextField("Webové stránky");
		add(webField);
		webField.setWidthFull();
		binder.forField(webField).bind("web");

		final TextArea hoursField = new TextArea("Otevírací hodiny");
		add(hoursField);
		hoursField.setWidthFull();
		hoursField.setHeight("200px");
		binder.forField(hoursField).bind("hours");

		add(new SaveCloseLayout(e -> {
			MedicalInstitutionTO writeDTO = modifiedMedicalInstitutionDTO == null ? new MedicalInstitutionTO()
					: modifiedMedicalInstitutionDTO;
			if (binder.writeBeanIfValid(writeDTO)) {
				try {
					getMedicFacade().saveMedicalInstitution(writeDTO);
					onSuccess();
					close();
				} catch (Exception ex) {
					new ErrorDialog("Nezdařilo se vytvořit nový záznam").open();
				}
			}
		}, e -> close()));

		if (modifiedMedicalInstitutionDTO != null)
			binder.readBean(modifiedMedicalInstitutionDTO);
	}

	protected MedicFacade getMedicFacade() {
		if (medicFacade == null)
			medicFacade = SpringContextHelper.getBean(MedicFacade.class);
		return medicFacade;
	}

	protected abstract void onSuccess();

}
