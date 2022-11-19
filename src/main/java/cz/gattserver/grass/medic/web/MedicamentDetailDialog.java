package cz.gattserver.grass.medic.web;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.Strong;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.facade.MedicFacade;
import cz.gattserver.grass.medic.interfaces.MedicamentTO;

public class MedicamentDetailDialog extends Dialog {

	private static final long serialVersionUID = -1240133390770972624L;

	public MedicamentDetailDialog(Long id) {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setPadding(false);
		add(layout);

		setWidth("400px");

		final MedicamentTO medicamentDTO = SpringContextHelper.getBean(MedicFacade.class).getMedicamentById(id);

		layout.add(new Strong("Název"));
		layout.add(medicamentDTO.getName());

		layout.add(new Strong("Reakce"));
		Div div = new Div();
		div.setText(medicamentDTO.getTolerance());
		div.getStyle().set("white-space", "pre-wrap");
		div.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
		layout.add(div);
	}
}
