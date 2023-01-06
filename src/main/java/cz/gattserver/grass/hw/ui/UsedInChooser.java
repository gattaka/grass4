package cz.gattserver.grass.hw.ui;

import java.util.function.Consumer;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.grass.core.ui.components.button.DeleteButton;
import cz.gattserver.grass.core.ui.components.button.ImageButton;
import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.ui.dialogs.HWItemChooseDialog;

public class UsedInChooser extends Div {

	private static final long serialVersionUID = -5660237108485881386L;

	public UsedInChooser(HWItemTO originalTO, Consumer<HWItemOverviewTO> onSelect) {
		Div usedInLabel = new Div(new Text("Je součástí"));
		usedInLabel.getStyle().set("color", "var(--lumo-secondary-text-color)").set("font-weight", "500")
				.set("font-size", "var(--lumo-font-size-s)").set("padding-top", "var(--lumo-space-m)");
		add(usedInLabel);

		HorizontalLayout usedIdLayout = new HorizontalLayout();
		add(usedIdLayout);

		Span usedInText = new Span();
		usedInText.getStyle().set("margin-left", "10px");
		if (originalTO != null)
			usedInText.setText(originalTO.getUsedInName());
		Button clearBtn = new DeleteButton(e -> {
			onSelect.accept(null);
			usedInText.removeAll();
		});
		usedIdLayout.add(clearBtn);
		Button chooseUsedInBtn = new ImageButton("Vybrat", ImageIcon.SEARCH_16_ICON, e -> {
			new HWItemChooseDialog(originalTO == null ? null : originalTO.getId(), to -> {
				usedInText.setText(to.getName());
				onSelect.accept(to);
			}).open();
		});
		usedIdLayout.add(chooseUsedInBtn);
		usedIdLayout.setAlignItems(Alignment.BASELINE);
		usedIdLayout.add(usedInText);
	}
}
