package cz.gattserver.grass.medic.web.tabs;

import java.util.ArrayList;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;

import com.vaadin.flow.component.grid.HeaderRow;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.interfaces.PhysicianTO;
import cz.gattserver.grass.medic.service.MedicService;
import cz.gattserver.grass.medic.web.PhysicianCreateDialog;
import cz.gattserver.grass.medic.web.PhysicianDetailDialog;

public class PhysiciansTab extends MedicPageTab<PhysicianTO, PhysicianTO, ArrayList<PhysicianTO>> {

    private static final long serialVersionUID = -5013459007975657195L;

    public PhysiciansTab() {
        super(PhysicianTO.class);
        filterTO = new PhysicianTO();
    }

    @Override
    protected ArrayList<PhysicianTO> getItems(PhysicianTO filterTO) {
        return new ArrayList<>(medicService.getPhysicians(filterTO));
    }

    @Override
    protected Dialog createCreateDialog() {
        return new PhysicianCreateDialog() {
            private static final long serialVersionUID = -7566950396535469316L;

            @Override
            protected void onSuccess(PhysicianTO to) {
                data = getItems(filterTO);
                grid.setItems(data);
            }
        };
    }

    @Override
    protected Dialog createDetailDialog(Long id) {
        return new PhysicianDetailDialog(SpringContextHelper.getBean(MedicService.class).getPhysicianById(id));
    }

    @Override
    protected Dialog createModifyDialog(PhysicianTO originalTO) {
        return new PhysicianCreateDialog(originalTO) {
            private static final long serialVersionUID = -7566950396535469316L;

            @Override
            protected void onSuccess(PhysicianTO to) {
                grid.getDataProvider().refreshItem(to);
            }
        };
    }

    @Override
    protected void deleteEntity(PhysicianTO dto) {
        medicService.deletePhysician(dto);
    }

    @Override
    protected void customizeGrid(Grid<PhysicianTO> grid) {
        grid.removeAllColumns();
        Grid.Column<PhysicianTO> nameCol = grid.addColumn("name").setHeader("Jméno");
        grid.setWidthFull();
        grid.setSelectionMode(SelectionMode.SINGLE);

        HeaderRow filteringHeader = grid.appendHeaderRow();

        // Název
        UIUtils.addHeaderTextField(filteringHeader.getCell(nameCol), e -> {
            filterTO.setName(e.getValue());
            populateGrid();
        });
    }
}