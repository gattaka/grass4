package cz.gattserver.grass.medic.web.tabs;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;

import cz.gattserver.grass.medic.interfaces.MedicalRecordTO;
import cz.gattserver.grass.medic.web.MedicalRecordCreateDialog;
import cz.gattserver.grass.medic.web.MedicalRecordDetailDialog;

public class MedicalRecordsTab extends MedicPageTab<MedicalRecordTO, ArrayList<MedicalRecordTO>> {

	private static final long serialVersionUID = -5013459007975657195L;

	public MedicalRecordsTab() {
		super(MedicalRecordTO.class);
	}

	@Override
	protected ArrayList<MedicalRecordTO> getItems() {
		return new ArrayList<>(getMedicFacade().getAllMedicalRecords());
	}

	@Override
	protected Dialog createCreateDialog() {
		return new MedicalRecordCreateDialog() {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				refreshGrid();
			}
		};
	}

	public void refreshGrid() {
		data = getItems();
		grid.setItems(data);
	}

	@Override
	protected Dialog createDetailDialog(Long id) {
		return new MedicalRecordDetailDialog(id);
	}

	@Override
	protected Dialog createModifyDialog(MedicalRecordTO dto) {
		return new MedicalRecordCreateDialog(dto) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				grid.getDataProvider().refreshItem(dto);
			}
		};
	}

	@Override
	protected void deleteEntity(MedicalRecordTO dto) {
		getMedicFacade().deleteMedicalRecord(dto);
	}

	@Override
	protected void customizeGrid(Grid<MedicalRecordTO> grid) {
		String fdateID = "fdate";
		grid.removeAllColumns();
		grid.addColumn(new LocalDateTimeRenderer<>(MedicalRecordTO::getDateTime,
				DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.forLanguageTag("CS")))).setHeader("Datum")
				.setKey(fdateID).setTextAlign(ColumnTextAlign.END).setWidth("130px").setFlexGrow(0);
		grid.addColumn("institution").setHeader("Instituce");
		grid.addColumn("record").setHeader("Záznam");
		grid.setWidthFull();
		grid.setSelectionMode(SelectionMode.SINGLE);
	}

}
