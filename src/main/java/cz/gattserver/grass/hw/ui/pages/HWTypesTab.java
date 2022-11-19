package cz.gattserver.grass.hw.ui.pages;

import java.util.Arrays;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.LinkButton;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.core.model.util.QuerydslUtil;
import cz.gattserver.grass.core.ui.components.button.CreateButton;
import cz.gattserver.grass.core.ui.components.button.DeleteGridButton;
import cz.gattserver.grass.core.ui.components.button.ModifyGridButton;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.dialogs.HWItemTypeEditDialog;

public class HWTypesTab extends Div {

	private static final long serialVersionUID = -5013459007975657195L;

	private static final String NAME_BIND = "nameBind";

	private transient HWService hwService;

	private Grid<HWItemTypeTO> grid;

	private HWItemTypeTO filterTO;

	private HWService getHWService() {
		if (hwService == null)
			hwService = SpringContextHelper.getBean(HWService.class);
		return hwService;
	}

	private void populate() {
		FetchCallback<HWItemTypeTO, HWItemTypeTO> fetchCallback = q -> getHWService().getHWItemTypes(filterTO,
				q.getOffset(), q.getLimit(), QuerydslUtil.transformOrdering(q.getSortOrders(), column -> {
					switch (column) {
					case NAME_BIND:
						return "name";
					default:
						return column;
					}
				})).stream();
		CountCallback<HWItemTypeTO, HWItemTypeTO> countCallback = q -> getHWService().countHWItemTypes(filterTO);
		grid.setDataProvider(DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
	}

	public HWTypesTab() {
		filterTO = new HWItemTypeTO();

		grid = new Grid<>();
		grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		UIUtils.applyGrassDefaultStyle(grid);

		Column<HWItemTypeTO> nameColumn = grid
				.addColumn(new ComponentRenderer<Button, HWItemTypeTO>(
						to -> new LinkButton(to.getName(), e -> openNewTypeWindow(to))))
				.setHeader("Název").setSortable(true).setKey(NAME_BIND).setFlexGrow(1);
		grid.setWidthFull();
		grid.setHeight("500px");
		grid.setSelectionMode(SelectionMode.SINGLE);

		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Název
		UIUtils.addHeaderTextField(filteringHeader.getCell(nameColumn), e -> {
			filterTO.setName(e.getValue());
			populate();
		});

		populate();
		grid.sort(Arrays.asList(new GridSortOrder<>(nameColumn, SortDirection.ASCENDING)));

		add(grid);

		ButtonLayout buttonLayout = new ButtonLayout();
		add(buttonLayout);

		/**
		 * Založení nového typu
		 */
		Button newTypeBtn = new CreateButton("Založit nový typ", e -> openNewTypeWindow(null));
		buttonLayout.add(newTypeBtn);

		/**
		 * Úprava typu
		 */
		Button fixBtn = new ModifyGridButton<HWItemTypeTO>(
				set -> openNewTypeWindow(grid.getSelectedItems().iterator().next()), grid);
		buttonLayout.add(fixBtn);

		/**
		 * Smazání typu
		 */
		Button deleteBtn = new DeleteGridButton<HWItemTypeTO>(set -> {
			HWItemTypeTO item = set.iterator().next();
			try {
				getHWService().deleteHWItemType(item.getId());
				populate();
			} catch (Exception ex) {
				new ErrorDialog("Nezdařilo se smazat vybranou položku").open();
			}
		}, grid);
		buttonLayout.add(deleteBtn);
	}

	// BUG ? Při disable na tabu a opětovném enabled zůstane table disabled
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		grid.setEnabled(enabled);
	}

	private void openNewTypeWindow(HWItemTypeTO hwItemTypeDTO) {
		new HWItemTypeEditDialog(hwItemTypeDTO == null ? null : hwItemTypeDTO) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess(HWItemTypeTO hwItemTypeDTO) {
				if (hwItemTypeDTO != null) {
					grid.getDataProvider().refreshItem(hwItemTypeDTO);
				} else {
					populate();
				}
			}
		}.open();
	}

}
