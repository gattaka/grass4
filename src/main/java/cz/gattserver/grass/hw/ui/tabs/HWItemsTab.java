package cz.gattserver.grass.hw.ui.tabs;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.components.button.CreateButton;
import cz.gattserver.grass.core.ui.components.button.DeleteGridButton;
import cz.gattserver.grass.core.ui.components.button.GridButton;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;

import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.HWItemsGrid;
import cz.gattserver.grass.hw.ui.dialogs.HWItemDetailsDialog;
import cz.gattserver.grass.hw.ui.dialogs.HWItemEditDialog;

public class HWItemsTab extends Div {

	private static final long serialVersionUID = -5013459007975657195L;

	@Autowired
	private HWService hwService;

	@Autowired
	private SecurityService securityFacade;

	private HWItemsGrid itemsGrid;

	public HWItemsTab() {
		SpringContextHelper.inject(this);

		itemsGrid = new HWItemsGrid(to -> openDetailWindow(to.getId()));

		add(itemsGrid);

		ButtonLayout buttonLayout = new ButtonLayout();
		add(buttonLayout);

		if (securityFacade.getCurrentUser().isAdmin()) {

			// Založení nové položky HW
			Button newHWBtn = new CreateButton("Přidat", e -> openItemWindow(null));
			buttonLayout.add(newHWBtn);

			// Kopie položky HW
			Button copyHWBtn = new GridButton<HWItemOverviewTO>("Zkopírovat",
					set -> copyItemWindow(set.iterator().next().getId()), itemsGrid.getGrid());
			copyHWBtn.setIcon(new Image(ImageIcon.PLUS_16_ICON.createResource(), "image"));
			buttonLayout.add(copyHWBtn);
		}

		// Zobrazení detailů položky HW
		Button detailsBtn = new GridButton<>("Detail",
				set -> openDetailWindow(set.iterator().next().getId()), itemsGrid.getGrid());
		detailsBtn.setIcon(new Image(ImageIcon.CLIPBOARD_16_ICON.createResource(), "image"));
		buttonLayout.add(detailsBtn);

		if (securityFacade.getCurrentUser().isAdmin()) {
			// Oprava údajů existující položky HW
			Button fixBtn = new GridButton<>("Upravit", set -> openItemWindow(set.iterator().next()),
					itemsGrid.getGrid());
			fixBtn.setIcon(new Image(ImageIcon.QUICKEDIT_16_ICON.createResource(), "image"));
			buttonLayout.add(fixBtn);

			// Smazání položky HW
			Button deleteBtn = new DeleteGridButton<>("Smazat", set -> {
				HWItemOverviewTO item = set.iterator().next();
				deleteItem(item);
			}, itemsGrid.getGrid());
			buttonLayout.add(deleteBtn);
		}
	}

	private void deleteItem(HWItemOverviewTO item) {
		try {
			hwService.deleteHWItem(item.getId());
			populate();
		} catch (Exception ex) {
			new ErrorDialog("Nezdařilo se smazat vybranou položku").open();
		}
	}

	private void openItemWindow(HWItemOverviewTO hwItemOverviewTO) {
		HWItemTO hwItem = null;
		if (hwItemOverviewTO != null)
			hwItem = hwService.getHWItem(hwItemOverviewTO.getId());
		new HWItemEditDialog(hwItem == null ? null : hwItem.getId()) {
			private static final long serialVersionUID = -1397391593801030584L;

			@Override
			protected void onSuccess(HWItemTO dto) {
				populate();
				HWItemOverviewTO filterTO = new HWItemOverviewTO();
				filterTO.setId(dto.getId());
				itemsGrid.getGrid().select(filterTO);
				if (hwItemOverviewTO == null)
					openDetailWindow(dto.getId());
			}
		}.open();
	}

	private void copyItemWindow(Long id) {
		Long newId = hwService.copyHWItem(id);
		populate();
		HWItemOverviewTO newTO = new HWItemOverviewTO();
		newTO.setId(newId);
		itemsGrid.getGrid().select(newTO);
		openDetailWindow(newId);
	}

	private void openDetailWindow(Long id) {
		new HWItemDetailsDialog(HWItemsTab.this, id) {
			private static final long serialVersionUID = 1621156205987235037L;

			public HWItemTO refreshItem() {
				HWItemTO to = super.refreshItem();
				itemsGrid.getGrid().getSelectedItems().forEach(item -> {
					if (item.getId().equals(id)) {
						item.setName(to.getName());
						item.setState(to.getState());
						item.setUsedInName(to.getUsedInName());
						item.setSupervizedFor(to.getSupervizedFor());
						item.setPrice(to.getPrice());
						item.setPurchaseDate(to.getPurchaseDate());
					}
				});
				return to;
			};
		}.open();
	}

	public void populate() {
		itemsGrid.populate();
	}

}
