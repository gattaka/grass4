package cz.gattserver.grass.medic.web.tabs;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.core.ui.components.GridOperationsTab;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.interfaces.MedicalRecordTO;
import cz.gattserver.grass.medic.service.MedicService;
import cz.gattserver.grass.medic.web.MedicalRecordDialog;

public class MedicalRecordsTab extends Div {

    private final MedicService medicService;
    private final MedicalRecordTO filterTO;

    private final ComponentFactory componentFactory;
    private GridOperationsTab gridOperationsTab;

    public MedicalRecordsTab() {
        medicService = SpringContextHelper.getBean(MedicService.class);
        filterTO = new MedicalRecordTO();
        componentFactory = new ComponentFactory();

        Consumer<MedicalRecordTO> onSave = to -> {
            medicService.saveMedicalRecord(to);
            populateGrid(gridOperationsTab.getGrid());
        };
        Consumer<MedicalRecordTO> onDelete = to -> {
            medicService.deleteMedicalRecord(to.getId());
            populateGrid(gridOperationsTab.getGrid());
        };
        gridOperationsTab = new GridOperationsTab<>(MedicalRecordTO.class,
                to -> MedicalRecordDialog.detail(medicService.getMedicalRecordById(to.getId())).open(),
                () -> MedicalRecordDialog.create(null, onSave).open(),
                to -> MedicalRecordDialog.edit(medicService.getMedicalRecordById(to.getId()), onSave).open(), onDelete,
                this::populateGrid, this::customizeGrid);
        add(gridOperationsTab);
    }

    private void populateGrid(Grid<MedicalRecordTO> grid) {
        grid.setItems(medicService.getMedicalRecords(filterTO));
    }

    private void customizeGrid(Grid<MedicalRecordTO> grid) {
        String fdateID = "fdate";
        grid.removeAllColumns();
        Grid.Column<MedicalRecordTO> dateCol = grid.addColumn(new LocalDateTimeRenderer<>(MedicalRecordTO::getDateTime,
                        () -> DateTimeFormatter.ofPattern("d. MMMM yyyy", componentFactory.createLocale()))).setHeader("Datum")
                .setKey(fdateID).setTextAlign(ColumnTextAlign.END).setWidth("130px").setFlexGrow(0);
        Grid.Column<MedicalRecordTO> instCol =
                grid.addColumn(MedicalRecordTO::getInstitutionName).setHeader("Instituce");
        Grid.Column<MedicalRecordTO> recordCol = grid.addColumn(MedicalRecordTO::getRecord).setHeader("Záznam");
        grid.setWidthFull();
        grid.setSelectionMode(SelectionMode.SINGLE);

        HeaderRow filteringHeader = grid.appendHeaderRow();

        // Datum
        UIUtils.addHeaderDatePicker(filteringHeader.getCell(dateCol), e -> {
            filterTO.setDateTime(e.getValue() == null ? null : e.getValue().atStartOfDay());
            populateGrid(grid);
        });

        // Instituce
        UIUtils.addHeaderTextField(filteringHeader.getCell(instCol), e -> {
            filterTO.setInstitutionName(e.getValue());
            populateGrid(grid);
        });

        // Záznam
        UIUtils.addHeaderTextField(filteringHeader.getCell(recordCol), e -> {
            filterTO.setRecord(e.getValue());
            populateGrid(grid);
        });
    }
}