package cz.gattserver.grass.medic.web;

import java.time.format.DateTimeFormatter;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.InlineButton;
import cz.gattserver.common.vaadin.Strong;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.service.MedicService;
import cz.gattserver.grass.medic.interfaces.MedicalRecordTO;

public class MedicalRecordDetailDialog extends Dialog {

	private static final long serialVersionUID = -1240133390770972624L;

	private transient MedicService medicService;

	public MedicalRecordDetailDialog(Long id) {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setPadding(false);
		add(layout);

		setWidth("600px");

		MedicalRecordTO medicalRecordDTO = getMedicFacade().getMedicalRecordById(id);

		VerticalLayout vl1 = new VerticalLayout();
		vl1.setPadding(false);
		vl1.add(new Strong("Instituce"));
        InlineButton button = new InlineButton(medicalRecordDTO.getInstitution().getName(),
				e -> new MedicalInstitutionDetailDialog(medicalRecordDTO.getInstitution().getId()).open());
		button.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
		vl1.add(button);

		VerticalLayout vl2 = new VerticalLayout();
		vl2.setPadding(false);
		vl2.add(new Strong("Ošetřující lékař"));
		vl2.add(medicalRecordDTO.getPhysician() == null ? "" : medicalRecordDTO.getPhysician().getName());

		HorizontalLayout line1 = new HorizontalLayout(vl1, vl2);
		line1.setWidthFull();
		line1.setPadding(false);
		layout.add(line1);

		layout.add(new Strong("Datum"));
		layout.add(medicalRecordDTO.getDateTime().format(DateTimeFormatter.ofPattern("d. MMMM yyyy, H:mm")));

		layout.add(new Strong("Záznam"));
		Div div = new Div();
		div.setText(medicalRecordDTO.getRecord());
		div.setWidthFull();
		div.getStyle().set("white-space", "pre-wrap");
		div.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
		layout.add(div);

		layout.add(new Strong("Medikamenty"));

		ButtonLayout tags = new ButtonLayout();
		tags.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
		medicalRecordDTO.getMedicaments().forEach(med -> {
			Button token = new Button(med.getName());
			tags.add(token);
		});
		layout.add(tags);
	}

	protected MedicService getMedicFacade() {
		if (medicService == null)
			medicService = SpringContextHelper.getBean(MedicService.class);
		return medicService;
	}
}
