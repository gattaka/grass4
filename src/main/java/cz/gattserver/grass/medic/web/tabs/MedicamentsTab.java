package cz.gattserver.grass.medic.web.tabs;

import java.util.ArrayList;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;

import cz.gattserver.grass.medic.interfaces.MedicamentTO;
import cz.gattserver.grass.medic.web.MedicamentCreateDialog;
import cz.gattserver.grass.medic.web.MedicamentDetailDialog;

public class MedicamentsTab extends MedicPageTab<MedicamentTO, ArrayList<MedicamentTO>> {

	private static final long serialVersionUID = -5013459007975657195L;

	public MedicamentsTab() {
		super(MedicamentTO.class);
	}

	@Override
	protected ArrayList<MedicamentTO> getItems() {
		return new ArrayList<>(getMedicFacade().getAllMedicaments());
	}

	@Override
	protected Dialog createCreateDialog() {
		return new MedicamentCreateDialog() {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				data = getItems();
				grid.setItems(data);
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
		getMedicFacade().deleteMedicament(dto);
	}

	@Override
	protected void customizeGrid(Grid<MedicamentTO> grid) {
		grid.getColumnByKey("name").setHeader("Název");
		grid.getColumnByKey("tolerance").setHeader("Reakce, nežádoucí účinky");
		grid.setWidthFull();
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setColumns("name", "tolerance");
	}

}
