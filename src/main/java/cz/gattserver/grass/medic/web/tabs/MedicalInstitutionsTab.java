package cz.gattserver.grass.medic.web.tabs;

import java.util.ArrayList;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;

import com.vaadin.flow.component.grid.HeaderRow;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;
import cz.gattserver.grass.medic.web.MedicalInstitutionCreateDialog;
import cz.gattserver.grass.medic.web.MedicalInstitutionDetailDialog;

public class MedicalInstitutionsTab
		extends MedicPageTab<MedicalInstitutionTO, MedicalInstitutionTO, ArrayList<MedicalInstitutionTO>> {

	private static final long serialVersionUID = -5013459007975657195L;

	@Override
	protected ArrayList<MedicalInstitutionTO> getItems(MedicalInstitutionTO filterTO) {
		return new ArrayList<>(medicService.getMedicalInstitutions(filterTO));
	}

	@Override
	protected Dialog createCreateDialog() {
		return new MedicalInstitutionCreateDialog() {
			private static final long serialVersionUID = 5711665262096833291L;

			@Override
			protected void onSuccess() {
				populateGrid();
			}
		};
	}

	@Override
	protected Dialog createDetailDialog(Long id) {
		return new MedicalInstitutionDetailDialog(id);
	}

	@Override
	protected Dialog createModifyDialog(MedicalInstitutionTO to) {
		return new MedicalInstitutionCreateDialog(to) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				grid.getDataProvider().refreshItem(to);
			}
		};
	}

	@Override
	protected void deleteEntity(MedicalInstitutionTO dto) {
		medicService.deleteMedicalInstitution(dto);
	}

	public MedicalInstitutionsTab() {
		super(MedicalInstitutionTO.class);
		filterTO = new MedicalInstitutionTO();
	}

	@Override
	protected void customizeGrid(Grid<MedicalInstitutionTO> grid) {
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
			populateGrid();
		});

		// Adresa
		UIUtils.addHeaderTextField(filteringHeader.getCell(addressCol), e -> {
			filterTO.setAddress(e.getValue());
			populateGrid();
		});

		// Web
		UIUtils.addHeaderTextField(filteringHeader.getCell(webCol), e -> {
			filterTO.setWeb(e.getValue());
			populateGrid();
		});
	}
}
