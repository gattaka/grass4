package cz.gattserver.grass.medic.web;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.Strong;
import cz.gattserver.grass.medic.service.MedicService;
import cz.gattserver.grass.medic.interfaces.PhysicianTO;

public class PhysicianDetailDialog extends Dialog {

	private static final long serialVersionUID = -1240133390770972624L;

	public PhysicianDetailDialog(Long id) {
		final PhysicianTO physicianDTO = SpringContextHelper.getBean(MedicService.class).getPhysicianById(id);
		
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setPadding(false);
		add(layout);
		
		layout.add(new Strong("Jméno"));
		layout.add(physicianDTO.getName());
	}

}
