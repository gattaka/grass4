package cz.gattserver.grass.medic.web.tabs;

import java.util.function.Consumer;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;

import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.grass.core.ui.components.GridOperationsTab;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;
import cz.gattserver.grass.medic.service.MedicService;
import cz.gattserver.grass.medic.web.MedicalInstitutionDialog;

public class MedicalInstitutionsTab extends Div {

    private final MedicService medicService;
    private final Consumer<MedicalInstitutionTO> onSave;
    private final MedicalInstitutionTO filterTO;

    public MedicalInstitutionsTab() {
        medicService = SpringContextHelper.getBean(MedicService.class);
        filterTO = new MedicalInstitutionTO();

        onSave = to -> medicService.saveMedicalInstitution(to);
        add(new GridOperationsTab<>(MedicalInstitutionTO.class,
                to -> MedicalInstitutionDialog.detail(medicService.getMedicalInstitutionById(to.getId())).open(),
                () -> MedicalInstitutionDialog.create(onSave).open(),
                to -> MedicalInstitutionDialog.edit(medicService.getMedicalInstitutionById(to.getId()), onSave).open(),
                to -> medicService.deleteMedicalInstitution(to), this::populateGrid, this::customizeGrid));
    }

    private void populateGrid(Grid<MedicalInstitutionTO> grid) {
        grid.setItems(medicService.getMedicalInstitutions(filterTO));
    }

    private void customizeGrid(Grid<MedicalInstitutionTO> grid) {
        grid.removeAllColumns();
        Grid.Column<MedicalInstitutionTO> nameCol = grid.addColumn("name").setHeader("Název");
        Grid.Column<MedicalInstitutionTO> addressCol = grid.addColumn("address").setHeader("Adresa");
        Grid.Column<MedicalInstitutionTO> webCol = grid.addColumn("web").setHeader("Stránky");
        grid.setWidthFull();
        grid.setSelectionMode(SelectionMode.SINGLE);

        HeaderRow filteringHeader = grid.appendHeaderRow();

        // Název
        UIUtils.addHeaderTextField(filteringHeader.getCell(nameCol), e -> {
            filterTO.setName(e.getValue());
            populateGrid(grid);
        });

        // Adresa
        UIUtils.addHeaderTextField(filteringHeader.getCell(addressCol), e -> {
            filterTO.setAddress(e.getValue());
            populateGrid(grid);
        });

        // Web
        UIUtils.addHeaderTextField(filteringHeader.getCell(webCol), e -> {
            filterTO.setWeb(e.getValue());
            populateGrid(grid);
        });
    }
}
