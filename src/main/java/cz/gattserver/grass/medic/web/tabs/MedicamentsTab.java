package cz.gattserver.grass.medic.web.tabs;

import java.util.function.Consumer;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;

import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.grass.core.ui.components.GridOperationsTab;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.interfaces.MedicamentTO;
import cz.gattserver.grass.medic.service.MedicService;
import cz.gattserver.grass.medic.web.MedicamentDialog;

public class MedicamentsTab extends Div {

    private static final long serialVersionUID = -5013459007975657195L;

    private final MedicService medicService;
    private final MedicamentTO filterTO;

    private GridOperationsTab gridOperationsTab;

    public MedicamentsTab() {
        medicService = SpringContextHelper.getBean(MedicService.class);
        filterTO = new MedicamentTO();

        Consumer<MedicamentTO> onSave = to -> {
            medicService.saveMedicament(to);
            populateGrid(gridOperationsTab.getGrid());
        };
        Consumer<MedicamentTO> onDelete = to -> {
            if (medicService.isMedicamentUsed(to.getId())) {
                UIUtils.showError("Medikament nelze smazat -- je evidován u záznamu");
                return;
            }
            medicService.deleteMedicament(to.getId());
            populateGrid(gridOperationsTab.getGrid());
        };
        gridOperationsTab = new GridOperationsTab<>(MedicamentTO.class,
                to -> MedicamentDialog.detail(medicService.getMedicamentById(to.getId())).open(),
                () -> MedicamentDialog.create(onSave).open(),
                to -> MedicamentDialog.edit(medicService.getMedicamentById(to.getId()), onSave).open(), onDelete,
                this::populateGrid, this::customizeGrid);
        add(gridOperationsTab);
    }

    private void populateGrid(Grid<MedicamentTO> grid) {
        grid.setItems(medicService.getMedicaments(filterTO));
    }

    private void customizeGrid(Grid<MedicamentTO> grid) {
        grid.removeAllColumns();
        Grid.Column<MedicamentTO> nameCol = grid.addColumn("name").setHeader("Název");
        Grid.Column<MedicamentTO> toleranceCol = grid.addColumn("tolerance").setHeader("Reakce, nežádoucí účinky");
        grid.setWidthFull();
        grid.setSelectionMode(SelectionMode.SINGLE);

        HeaderRow filteringHeader = grid.appendHeaderRow();

        // Název
        UIUtils.addHeaderTextField(filteringHeader.getCell(nameCol), e -> {
            filterTO.setName(e.getValue());
            populateGrid(grid);
        });

        // Název
        UIUtils.addHeaderTextField(filteringHeader.getCell(toleranceCol), e -> {
            filterTO.setTolerance(e.getValue());
            populateGrid(grid);
        });
    }
}