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

    private static final long serialVersionUID = -5013459007975657195L;

    private final MedicService medicService;
    private final Consumer<PhysicianTO> onSave;
    private final PhysicianTO filterTO;

    public PhysiciansTab() {
        medicService = SpringContextHelper.getBean(MedicService.class);
        filterTO = new PhysicianTO();

        onSave = to -> medicService.savePhysician(to);
        add(new GridOperationsTab<>(PhysicianTO.class,
                to -> PhysicianDialog.detail(medicService.getPhysicianById(to.getId())).open(),
                () -> PhysicianDialog.create(onSave).open(),
                to -> PhysicianDialog.edit(medicService.getPhysicianById(to.getId()), onSave).open(),
                to -> medicService.deletePhysician(to), this::populateGrid, this::customizeGrid));
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