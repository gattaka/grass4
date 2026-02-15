package cz.gattserver.grass.hw.ui.dialogs;

import java.util.function.Consumer;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass.hw.ui.HWItemsGrid;

public class HWItemChooseDialog extends EditWebDialog {

    public HWItemChooseDialog(Long ignoreId, Consumer<HWItemOverviewTO> onSelect) {
        super("Výběr");
        SpringContextHelper.inject(this);

        setWidth("900px");

        HWItemsGrid itemsGrid = new HWItemsGrid(null, to -> {
            onSelect.accept(to);
            close();
        });
        itemsGrid.getFilterTO().setIgnoreId(ignoreId);
        add(itemsGrid);

        HorizontalLayout buttons = componentFactory.createDialogSubmitOrStornoLayout(e -> {
            onSelect.accept(itemsGrid.getGrid().getSelectedItems().iterator().next());
            close();
        }, e -> close(), saveButton -> itemsGrid.getGrid()
                .addSelectionListener(e -> saveButton.setEnabled(!e.getAllSelectedItems().isEmpty())));
        add(buttons);
    }
}