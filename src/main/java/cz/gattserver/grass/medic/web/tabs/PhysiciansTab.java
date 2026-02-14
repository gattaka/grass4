package cz.gattserver.grass.medic.web.tabs;

import java.util.function.Consumer;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;

import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.grass.core.ui.components.GridOperationsTab;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.interfaces.PhysicianTO;
import cz.gattserver.grass.medic.service.MedicService;
import cz.gattserver.grass.medic.web.PhysicianDialog;

public class PhysiciansTab extends Div {

    private final MedicService medicService;
    private final PhysicianTO filterTO;

    private GridOperationsTab gridOperationsTab;

    public PhysiciansTab() {
        medicService = SpringContextHelper.getBean(MedicService.class);
        filterTO = new PhysicianTO();

        Consumer<PhysicianTO> onSave = to -> {
            medicService.savePhysician(to);
            populateGrid(gridOperationsTab.getGrid());
        };
        Consumer<PhysicianTO> onDelete = to -> {
            medicService.deletePhysician(to.getId());
            populateGrid(gridOperationsTab.getGrid());
        };
        gridOperationsTab = new GridOperationsTab<>(PhysicianTO.class,
                to -> PhysicianDialog.detail(medicService.getPhysicianById(to.getId())).open(),
                () -> PhysicianDialog.create(onSave).open(),
                to -> PhysicianDialog.edit(medicService.getPhysicianById(to.getId()), onSave).open(), onDelete,
                this::populateGrid, this::customizeGrid);
        add(gridOperationsTab);
    }

    private void populateGrid(Grid<PhysicianTO> grid) {
        grid.setItems(medicService.getPhysicians(filterTO));
    }

    private void customizeGrid(Grid<PhysicianTO> grid) {
        grid.removeAllColumns();
        grid.setWidthFull();
        grid.setSelectionMode(SelectionMode.SINGLE);

        Grid.Column<PhysicianTO> nameCol = grid.addColumn(PhysicianTO::getName).setHeader("Jméno");
        Grid.Column<PhysicianTO> emailCol = grid.addColumn(PhysicianTO::getEmail).setHeader("Email");
        Grid.Column<PhysicianTO> phoneCol = grid.addColumn(PhysicianTO::getPhone).setHeader("Telefon");

        HeaderRow filteringHeader = grid.appendHeaderRow();

        UIUtils.addHeaderTextField(filteringHeader.getCell(nameCol), e -> {
            filterTO.setName(e.getValue());
            populateGrid(grid);
        });
        UIUtils.addHeaderTextField(filteringHeader.getCell(emailCol), e -> {
            filterTO.setEmail(e.getValue());
            populateGrid(grid);
        });
        UIUtils.addHeaderTextField(filteringHeader.getCell(phoneCol), e -> {
            filterTO.setPhone(e.getValue());
            populateGrid(grid);
        });
    }
}