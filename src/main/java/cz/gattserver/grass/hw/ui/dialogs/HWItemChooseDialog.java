package cz.gattserver.grass.hw.ui.dialogs;

import java.util.function.Consumer;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.grass.core.ui.components.SaveCloseLayout;
import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass.hw.ui.HWItemsGrid;

public class HWItemChooseDialog extends EditWebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	public HWItemChooseDialog(Long ignoreId, Consumer<HWItemOverviewTO> onSelect) {
		SpringContextHelper.inject(this);

		setWidth("900px");

		HWItemsGrid itemsGrid = new HWItemsGrid(to -> {
			onSelect.accept(to);
			close();
		});
		itemsGrid.getFilterTO().setIgnoreId(ignoreId);
		add(itemsGrid);

		SaveCloseLayout buttons = new SaveCloseLayout(e -> {
			onSelect.accept(itemsGrid.getGrid().getSelectedItems().iterator().next());
			close();
		}, e -> close());
		add(buttons);
		itemsGrid.getGrid()
				.addSelectionListener(e -> buttons.getSaveButton().setEnabled(!e.getAllSelectedItems().isEmpty()));

	}

}
