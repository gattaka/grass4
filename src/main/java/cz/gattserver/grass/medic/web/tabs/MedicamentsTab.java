package cz.gattserver.grass.medic.web.tabs;

import java.util.ArrayList;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;

import com.vaadin.flow.component.grid.HeaderRow;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.interfaces.MedicamentTO;
import cz.gattserver.grass.medic.web.MedicamentCreateDialog;
import cz.gattserver.grass.medic.web.MedicamentDetailDialog;

public class MedicamentsTab extends MedicPageTab<MedicamentTO, MedicamentTO, ArrayList<MedicamentTO>> {

	private static final long serialVersionUID = -5013459007975657195L;

	public MedicamentsTab() {
		super(MedicamentTO.class);
		filterTO = new MedicamentTO();
	}

	@Override
	protected ArrayList<MedicamentTO> getItems(MedicamentTO filterTO) {
		return new ArrayList<>(medicService.getMedicaments(filterTO));
	}

	@Override
	protected Dialog createCreateDialog() {
		return new MedicamentCreateDialog() {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateGrid();
			}
		};
	}

	@Override
	protected Dialog createDetailDialog(Long id) {
		return new MedicamentDetailDialog(id);
	}

	@Override
	protected Dialog createModifyDialog(MedicamentTO dto) {
		return new MedicamentCreateDialog(dto) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				grid.getDataProvider().refreshItem(dto);
			}
		};
	}

	@Override
	protected void deleteEntity(MedicamentTO dto) {
		medicService.deleteMedicament(dto);
	}

	@Override
	protected void customizeGrid(Grid<MedicamentTO> grid) {
		grid.removeAllColumns();
		Grid.Column<MedicamentTO> nameCol = grid.addColumn("name").setHeader("Název");
		Grid.Column<MedicamentTO> toleranceCol = grid.addColumn("tolerance").setHeader("Reakce, nežádoucí účinky");
		grid.setWidthFull();
		grid.setSelectionMode(SelectionMode.SINGLE);

		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Název
		UIUtils.addHeaderTextField(filteringHeader.getCell(nameCol), e -> {
			filterTO.setName(e.getValue());
			populateGrid();
		});

		// Název
		UIUtils.addHeaderTextField(filteringHeader.getCell(toleranceCol), e -> {
			filterTO.setTolerance(e.getValue());
			populateGrid();
		});
	}
}