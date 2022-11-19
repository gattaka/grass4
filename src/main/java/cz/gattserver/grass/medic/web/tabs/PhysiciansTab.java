package cz.gattserver.grass.medic.web.tabs;

import java.util.ArrayList;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;

import cz.gattserver.grass.medic.interfaces.PhysicianTO;
import cz.gattserver.grass.medic.web.PhysicianCreateDialog;
import cz.gattserver.grass.medic.web.PhysicianDetailDialog;

public class PhysiciansTab extends MedicPageTab<PhysicianTO, ArrayList<PhysicianTO>> {

	private static final long serialVersionUID = -5013459007975657195L;

	public PhysiciansTab() {
		super(PhysicianTO.class);
	}

	@Override
	protected ArrayList<PhysicianTO> getItems() {
		return new ArrayList<>(getMedicFacade().getAllPhysicians());
	}

	@Override
	protected Dialog createCreateDialog() {
		return new PhysicianCreateDialog() {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess(PhysicianTO to) {
				data = getItems();
				grid.setItems(data);
			}
		};
	}

	@Override
	protected Dialog createDetailDialog(Long id) {
		return new PhysicianDetailDialog(id);
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
		getMedicFacade().deletePhysician(dto);
	}

	@Override
	protected void customizeGrid(Grid<PhysicianTO> grid) {
		grid.getColumnByKey("name").setHeader("Jméno");
		grid.setWidthFull();
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setColumns("name");
	}

}
