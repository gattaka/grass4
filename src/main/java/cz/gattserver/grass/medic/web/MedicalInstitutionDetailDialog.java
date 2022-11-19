package cz.gattserver.grass.medic.web;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.Strong;
import cz.gattserver.grass.core.ui.util.UIUtils;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import cz.gattserver.grass.medic.facade.MedicFacade;
import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;

public class MedicalInstitutionDetailDialog extends Dialog {

	private static final long serialVersionUID = -1240133390770972624L;

	private transient MedicFacade medicFacade;

	public MedicalInstitutionDetailDialog(Long id) {
		final MedicalInstitutionTO medicalInstitutionDTO = getMedicFacade().getMedicalInstitutionById(id);

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setPadding(false);
		add(layout);

		layout.add(new Strong("Název"));
		layout.add(medicalInstitutionDTO.getName());

		layout.add(new Strong("Web"));
		if (StringUtils.isBlank(medicalInstitutionDTO.getWeb())) {
			layout.add("-");
		} else {
			Anchor link = new Anchor(medicalInstitutionDTO.getWeb(), medicalInstitutionDTO.getWeb());
			link.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
			link.setTarget("_blank");
			layout.add(link);
		}

		layout.add(new Strong("Adresa"));
		layout.add(medicalInstitutionDTO.getAddress());

		layout.add(new Strong("Hodiny"));
		Div div = new Div();
		div.setText(StringUtils.isBlank(medicalInstitutionDTO.getHours()) ? "-" : medicalInstitutionDTO.getHours());
		div.getStyle().set("white-space", "pre-wrap");
		div.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
		layout.add(div);
	}

	protected MedicFacade getMedicFacade() {
		if (medicFacade == null)
			medicFacade = SpringContextHelper.getBean(MedicFacade.class);
		return medicFacade;
	}

}
