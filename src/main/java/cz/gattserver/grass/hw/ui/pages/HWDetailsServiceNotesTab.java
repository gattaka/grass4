package cz.gattserver.grass.hw.ui.pages;

import java.util.Arrays;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.components.OperationsLayout;
import cz.gattserver.grass.core.ui.components.button.CreateButton;
import cz.gattserver.grass.core.ui.components.button.DeleteGridButton;
import cz.gattserver.grass.core.ui.components.button.ModifyGridButton;
import cz.gattserver.grass.core.ui.util.ContainerDiv;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.interfaces.HWServiceNoteTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.dialogs.HWItemDetailsDialog;
import cz.gattserver.grass.hw.ui.dialogs.HWServiceNoteEditDialog;

public class HWDetailsServiceNotesTab extends Div {

	private static final long serialVersionUID = -3236939739462367881L;

	private static final String DEFAULT_NOTE_LABEL_VALUE = "- Zvolte servisní záznam -";

	private transient HWService hwService;
	private transient SecurityService securityFacade;

	private Column<HWServiceNoteTO> serviceDateColumn;
	private Grid<HWServiceNoteTO> serviceNotesGrid;
	private HWItemTO hwItem;
	private HWItemDetailsDialog hwItemDetailDialog;

	public HWDetailsServiceNotesTab(HWItemTO hwItem, HWItemDetailsDialog hwItemDetailDialog) {
		SpringContextHelper.inject(this);
		this.hwItem = hwItem;
		this.hwItemDetailDialog = hwItemDetailDialog;
		init();
	}

	private HWService getHWService() {
		if (hwService == null)
			hwService = SpringContextHelper.getBean(HWService.class);
		return hwService;
	}

	private UserInfoTO getUser() {
		if (securityFacade == null)
			securityFacade = SpringContextHelper.getBean(SecurityService.class);
		return securityFacade.getCurrentUser();
	}

	private void init() {
		serviceNotesGrid = new Grid<>();
		add(serviceNotesGrid);

		UIUtils.applyGrassDefaultStyle(serviceNotesGrid);
		serviceNotesGrid.setSelectionMode(SelectionMode.SINGLE);
		Column<HWServiceNoteTO> idColumn = serviceNotesGrid
				.addColumn(new TextRenderer<HWServiceNoteTO>(to -> String.valueOf(to.getId())));
		serviceDateColumn = serviceNotesGrid
				.addColumn(new LocalDateRenderer<HWServiceNoteTO>(HWServiceNoteTO::getDate, "d.M.yyyy")).setHeader("Datum")
				.setTextAlign(ColumnTextAlign.END).setWidth("80px").setFlexGrow(0);
		serviceNotesGrid.addColumn(hw -> hw.getState().getName()).setHeader("Stav").setWidth("110px").setFlexGrow(0);
		serviceNotesGrid
				.addColumn(
						new TextRenderer<>(to -> to.getUsedInName() == null ? "" : String.valueOf(to.getUsedInName())))
				.setHeader("Je součástí").setWidth("180px").setFlexGrow(0);
		serviceNotesGrid.addColumn(new TextRenderer<>(to -> String.valueOf(to.getDescription()))).setHeader("Obsah");
		idColumn.setVisible(false);
		serviceNotesGrid.setHeight("300px");

		serviceNotesGrid
				.sort(Arrays.asList(new GridSortOrder<HWServiceNoteTO>(serviceDateColumn, SortDirection.ASCENDING),
						new GridSortOrder<HWServiceNoteTO>(idColumn, SortDirection.ASCENDING)));

		populateServiceNotesGrid();

		final Div serviceNoteDescription = new ContainerDiv();
		serviceNoteDescription.add(DEFAULT_NOTE_LABEL_VALUE);
		serviceNoteDescription.setHeight("300px");
		serviceNoteDescription.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		add(serviceNoteDescription);

		serviceNotesGrid.addSelectionListener(selection -> {
			if (selection.getFirstSelectedItem().isPresent()) {
				HWServiceNoteTO serviceNoteDTO = selection.getFirstSelectedItem().get();
				serviceNoteDescription.setText((String) serviceNoteDTO.getDescription());
			} else {
				serviceNoteDescription.setText(DEFAULT_NOTE_LABEL_VALUE);
			}
		});

		if (getUser().isAdmin()) {
			OperationsLayout operationsLayout = new OperationsLayout(e -> hwItemDetailDialog.close());
			add(operationsLayout);

			Button newNoteBtn = new CreateButton(e -> new HWServiceNoteEditDialog(hwItem) {
				private static final long serialVersionUID = -5582822648042555576L;

				@Override
				protected void onSuccess(HWServiceNoteTO noteDTO) {
					hwItem.getServiceNotes().add(noteDTO);
					populateServiceNotesGrid();
					hwItemDetailDialog.refreshItem();
					hwItemDetailDialog.switchServiceNotesTab();
					serviceNotesGrid.select(noteDTO);
				}
			}.open());

			Button fixNoteBtn = new ModifyGridButton<>("Opravit záznam", event -> {
				if (serviceNotesGrid.getSelectedItems().isEmpty())
					return;
				new HWServiceNoteEditDialog(hwItem, serviceNotesGrid.getSelectedItems().iterator().next()) {
					private static final long serialVersionUID = -5582822648042555576L;

					@Override
					protected void onSuccess(HWServiceNoteTO noteDTO) {
						populateServiceNotesGrid();
					}
				}.open();
			}, serviceNotesGrid);

			Button deleteNoteBtn = new DeleteGridButton<>("Smazat záznam", items -> {
				HWServiceNoteTO item = items.iterator().next();
				getHWService().deleteServiceNote(item, hwItem.getId());
				hwItem.getServiceNotes().remove(item);
				populateServiceNotesGrid();
				hwItemDetailDialog.refreshTabLabels();
			}, serviceNotesGrid);

			operationsLayout.add(newNoteBtn);
			operationsLayout.add(fixNoteBtn);
			operationsLayout.add(deleteNoteBtn);
		}
	}

	private void populateServiceNotesGrid() {
		serviceNotesGrid.setItems(hwItem.getServiceNotes());
		serviceNotesGrid.sort(Arrays.asList(new GridSortOrder<>(serviceDateColumn, SortDirection.DESCENDING)));
	}
}
