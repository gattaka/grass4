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
import cz.gattserver.common.vaadin.InlineButton;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.core.model.util.QuerydslUtil;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.hw.interfaces.HWFilterTO;
import cz.gattserver.grass.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.HWUIUtils;
import cz.gattserver.grass.hw.ui.dialogs.HWItemTypeEditDialog;
import cz.gattserver.grass.hw.ui.pages.HWPage;

public class HWTypesTab extends Div {

    private static final long serialVersionUID = -5013459007975657195L;

    private static final String NAME_BIND = "nameBind";
    private static final String COUNT_BIND = "countBind";

    private transient HWService hwService;

    private Grid<HWItemTypeTO> grid;

    private HWItemTypeTO filterTO;

    private HWService getHWService() {
        if (hwService == null) hwService = SpringContextHelper.getBean(HWService.class);
        return hwService;
    }

    private void populate() {
        FetchCallback<HWItemTypeTO, HWItemTypeTO> fetchCallback =
                q -> getHWService().getHWItemTypes(filterTO, q.getOffset(), q.getLimit(),
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
        CountCallback<HWItemTypeTO, HWItemTypeTO> countCallback = q -> getHWService().countHWItemTypes(filterTO);
        grid.setDataProvider(DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
    }

    public HWTypesTab() {
        filterTO = new HWItemTypeTO();

        grid = new Grid<>();
        grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        UIUtils.applyGrassDefaultStyle(grid);

        grid.addColumn(HWItemTypeTO::getCount).setHeader("Počet").setSortable(true).setKey(COUNT_BIND).setWidth("100px")
                .setFlexGrow(0);
        Column<HWItemTypeTO> nameColumn =
                grid.addColumn(new ComponentRenderer<>(to -> new InlineButton(to.getName(), e -> {
                    HWFilterTO filter = new HWFilterTO();
                    List<String> types = new ArrayList<>();
                    types.add(to.getName());
                    filter.setTypes(types);
                    Map<String, String> filterQuery = HWUIUtils.processFilterToQuery(filter);
                    UI.getCurrent().navigate(HWPage.class, QueryParameters.simple(filterQuery));
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

        ButtonLayout buttonLayout = new ButtonLayout();
        add(buttonLayout);

        /**
         * Založení nového typu
         */
        ComponentFactory componentFactory = new ComponentFactory();
        Button newTypeBtn = componentFactory.createCreateButton(e -> openNewTypeWindow(null));
        buttonLayout.add(newTypeBtn);

        /**
         * Úprava typu
         */
        buttonLayout.add(componentFactory.createEditGridButton(item -> openNewTypeWindow(item), grid));

        /**
         * Smazání typu
         */
        buttonLayout.add(componentFactory.createDeleteGridButton(item -> {
            try {
                getHWService().deleteHWItemType(item.getId());
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
