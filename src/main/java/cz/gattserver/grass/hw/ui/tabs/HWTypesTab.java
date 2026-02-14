package cz.gattserver.grass.hw.ui.tabs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.vaadin.flow.component.UI;
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

import com.vaadin.flow.router.QueryParameters;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.core.model.util.QuerydslUtil;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.hw.interfaces.HWFilterTO;
import cz.gattserver.grass.hw.interfaces.HWTypeTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.HWUIUtils;
import cz.gattserver.grass.hw.ui.dialogs.HWTypeEditDialog;
import cz.gattserver.grass.hw.ui.pages.HWItemPage;

public class HWTypesTab extends Div {

    private static final String NAME_BIND = "nameBind";
    private static final String COUNT_BIND = "countBind";

    private transient HWService hwService;

    private Grid<HWTypeTO> grid;

    private HWTypeTO filterTO;

    private HWService getHWService() {
        if (hwService == null) hwService = SpringContextHelper.getBean(HWService.class);
        return hwService;
    }

    private void populate() {
        FetchCallback<HWTypeTO, HWTypeTO> fetchCallback =
                q -> getHWService().getHWTypes(filterTO, q.getOffset(), q.getLimit(),
                        QuerydslUtil.transformOrdering(q.getSortOrders(), column -> {
                            switch (column) {
                                case NAME_BIND:
                                    return "name";
                                case COUNT_BIND:
                                    return "count";
                                default:
                                    return column;
                            }
                        })).stream();
        CountCallback<HWTypeTO, HWTypeTO> countCallback = q -> getHWService().countHWTypes(filterTO);
        grid.setDataProvider(DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
    }

    public HWTypesTab() {
        filterTO = new HWTypeTO();

        grid = new Grid<>();
        grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        UIUtils.applyGrassDefaultStyle(grid);

        ComponentFactory componentFactory = new ComponentFactory();

        grid.addColumn(HWTypeTO::getCount).setHeader("Počet").setSortable(true).setKey(COUNT_BIND).setWidth("100px")
                .setFlexGrow(0);
        Column<HWTypeTO> nameColumn =
                grid.addColumn(new ComponentRenderer<>(to -> componentFactory.createInlineButton(to.getName(), e -> {
                    HWFilterTO filter = new HWFilterTO();
                    List<String> types = new ArrayList<>();
                    types.add(to.getName());
                    filter.setTypes(types);
                    Map<String, String> filterQuery = HWUIUtils.processFilterToQuery(filter);
                    UI.getCurrent().navigate(HWItemPage.class, QueryParameters.simple(filterQuery));
                }))).setHeader("Název").setSortable(true).setKey(NAME_BIND).setFlexGrow(1);
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

        Div buttonLayout = componentFactory.createButtonLayout();
        add(buttonLayout);

        /**
         * Založení nového typu
         */
        Button newTypeBtn = componentFactory.createCreateButton(e -> openCreateDialog());
        buttonLayout.add(newTypeBtn);

        /**
         * Úprava typu
         */
        buttonLayout.add(componentFactory.createEditGridButton(item -> openEditDialog(item), grid));

        /**
         * Smazání typu
         */
        buttonLayout.add(componentFactory.createDeleteGridButton(item -> {
            try {
                getHWService().deleteHWType(item.getId());
                populate();
            } catch (Exception ex) {
                new ErrorDialog("Nezdařilo se smazat vybranou položku").open();
            }
        }, grid));
    }

    // BUG ? Při disable na tabu a opětovném enabled zůstane table disabled
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        grid.setEnabled(enabled);
    }

    private void openCreateDialog() {
        HWTypeEditDialog.create( to -> {
            hwService.saveHWType(to);
            grid.getDataProvider().refreshItem(to);
            populate();
        }).open();
    }

    private void openEditDialog(HWTypeTO hwItemTypeTO) {
        HWTypeEditDialog.edit(hwItemTypeTO, to -> {
            hwService.saveHWType(to);
            grid.getDataProvider().refreshItem(to);
            populate();
        }).open();
    }
}