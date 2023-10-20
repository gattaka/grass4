package cz.gattserver.grass.medic.web.tabs;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;

import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.interfaces.MedicalRecordTO;
import cz.gattserver.grass.medic.web.MedicalRecordCreateDialog;
import cz.gattserver.grass.medic.web.MedicalRecordDetailDialog;

public class MedicalRecordsTab extends MedicPageTab<MedicalRecordTO, MedicalRecordTO, ArrayList<MedicalRecordTO>> {

	private static final long serialVersionUID = -5013459007975657195L;

	public MedicalRecordsTab() {
		super(MedicalRecordTO.class);
		filterTO = new MedicalRecordTO();
	}

	@Override
	protected ArrayList<MedicalRecordTO> getItems(MedicalRecordTO filterTO) {
		return new ArrayList<>(medicService.getMedicalRecords(filterTO));
	}

	@Override
	protected Dialog createCreateDialog() {
		return new MedicalRecordCreateDialog() {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateGrid();
			}
		};
	}

	@Override
	protected Dialog createDetailDialog(Long id) {
		return new MedicalRecordDetailDialog(id);
	}

	@Override
	protected Dialog createModifyDialog(MedicalRecordTO to) {
		return new MedicalRecordCreateDialog(to) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				grid.getDataProvider().refreshItem(to);
			}
		};
	}

	@Override
	protected void deleteEntity(MedicalRecordTO to) {
		medicService.deleteMedicalRecord(to);
	}

	@Override
	protected void customizeGrid(Grid<MedicalRecordTO> grid) {
		String fdateID = "fdate";
		grid.removeAllColumns();
		Grid.Column<MedicalRecordTO> dateCol = grid.addColumn(new LocalDateTimeRenderer<>(MedicalRecordTO::getDateTime,
						() -> DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.forLanguageTag("CS")))).setHeader(
								"Datum")
				.setKey(fdateID).setTextAlign(ColumnTextAlign.END).setWidth("130px").setFlexGrow(0);
		Grid.Column<MedicalRecordTO> instCol = grid.addColumn("institution").setHeader("Instituce");
		Grid.Column<MedicalRecordTO> recordCol = grid.addColumn("record").setHeader("Záznam");
		grid.setWidthFull();
		grid.setSelectionMode(SelectionMode.SINGLE);

		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Datum
		UIUtils.addHeaderDatePicker(filteringHeader.getCell(dateCol), e -> {
			filterTO.setDate(e.getValue());
			populateGrid();
		});

		// Instituce
		UIUtils.addHeaderTextField(filteringHeader.getCell(instCol), e -> {
			filterTO.setInstitutionName(e.getValue());
			populateGrid();
		});

		// Záznam
		UIUtils.addHeaderTextField(filteringHeader.getCell(recordCol), e -> {
			filterTO.setRecord(e.getValue());
			populateGrid();
		});
	}

}