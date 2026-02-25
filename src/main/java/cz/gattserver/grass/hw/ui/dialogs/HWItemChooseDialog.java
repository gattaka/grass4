package cz.gattserver.grass.hw.ui.dialogs;

import java.io.Serial;
import java.util.function.Consumer;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass.hw.ui.HWItemsGrid;

public class HWItemChooseDialog extends EditWebDialog {

    @Serial
    private static final long serialVersionUID = -4522406277779261488L;

    public HWItemChooseDialog(Long ignoreId, Consumer<HWItemOverviewTO> onSelect) {
        super("Výběr");
        SpringContextHelper.inject(this);

        setWidth(900, Unit.PIXELS);

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