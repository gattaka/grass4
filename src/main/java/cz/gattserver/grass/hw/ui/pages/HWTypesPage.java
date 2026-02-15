package cz.gattserver.grass.hw.ui.pages;

import com.querydsl.core.types.OrderSpecifier;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.internal.AllowInert;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.core.model.util.QuerydslUtil;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.pages.MainView;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.hw.interfaces.HWFilterTO;
import cz.gattserver.grass.hw.interfaces.HWTypeTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.HWItemsGrid;
import cz.gattserver.grass.hw.ui.HWUIUtils;
import cz.gattserver.grass.hw.ui.dialogs.HWTypeEditDialog;

import java.util.*;

@PageTitle("Evidence HW")
@Route(value = "hw-types", layout = MainView.class)
public class HWTypesPage extends Div implements HasUrlParameter<String>, BeforeLeaveObserver {

    private static final String ID_QUERY_TOKEN = "id";
    private static final String NAME_QUERY_TOKEN = "name";

    private static final String NAME_BIND = "nameBind";
    private static final String COUNT_BIND = "countBind";

    private final HWService hwService;
    private final SecurityService securityService;

    private Grid<HWTypeTO> grid;
    private HWTypeTO filterTO;

    private TextField nameField;
    private Map<Long, Integer> indexMap = new HashMap<>();

    private Long lastId;

    public HWTypesPage(HWService hwService, SecurityService securityService) {
        this.hwService = hwService;
        this.securityService = securityService;
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        removeAll();
        ComponentFactory componentFactory = new ComponentFactory();

        Div layout = componentFactory.createOneColumnLayout();
        add(layout);

        layout.add(HWUIUtils.createNavigationLayout());

        filterTO = new HWTypeTO();

        // Tabulka HW
        grid = new Grid<>() {
            @AllowInert
            @ClientCallable
            private void scrollToId(Long id) {
                onGridScrollToId(id);
            }
        };
        grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        UIUtils.applyGrassDefaultStyle(grid);

        grid.addColumn(HWTypeTO::getCount).setHeader("Počet").setSortable(true).setKey(COUNT_BIND).setWidth("100px")
                .setFlexGrow(0);
        Grid.Column<HWTypeTO> nameColumn = grid.addColumn(new ComponentRenderer<>(to -> {
            HWFilterTO filter = new HWFilterTO();
            Set<String> types = new LinkedHashSet<>();
            types.add(to.getName());
            filter.setTypes(types);
            Map<String, String> params = HWItemsGrid.processFilterToQuery(filter);
            RouterLink routerLink = new RouterLink(to.getName(), HWItemsPage.class);
            routerLink.setQueryParameters(QueryParameters.simple(params));
            routerLink.getElement().addEventListener("click", e -> lastId = to.getId());
            return routerLink;
        })).setHeader("Název").setSortable(true).setKey(NAME_BIND).setFlexGrow(1);
        grid.setWidthFull();
        grid.setHeight("500px");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        HeaderRow filteringHeader = grid.appendHeaderRow();

        // Název
        nameField = UIUtils.addHeaderTextField(filteringHeader.getCell(nameColumn), e -> {
            filterTO.setName(e.getValue());
            populate();
        });

        layout.add(grid);

        Div buttonLayout = componentFactory.createButtonLayout();
        layout.add(buttonLayout);

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
                hwService.deleteHWType(item.getId());
                populate();
            } catch (Exception ex) {
                new ErrorDialog("Nezdařilo se smazat vybranou položku").open();
            }
        }, grid));

        QueryParameters params = event.getLocation().getQueryParameters();
        Map<String, List<String>> parametersMap = params.getParameters();
        HWFilterTO filterTO = new HWFilterTO();
        if (parametersMap.containsKey(ID_QUERY_TOKEN))
            filterTO.setId(Long.parseLong(parametersMap.get(ID_QUERY_TOKEN).get(0)));
        if (parametersMap.containsKey(NAME_QUERY_TOKEN)) filterTO.setName(parametersMap.get(NAME_QUERY_TOKEN).get(0));
        setFilterTO(filterTO);

        populate();

        if (filterTO.getId() != null) selectAndScroll(filterTO.getId());
    }

    public void setFilterTO(HWFilterTO filterTO) {
        if (filterTO.getName() != null) nameField.setValue(filterTO.getName());
    }

    private void onGridScrollToId(Long id) {
        Integer index = indexMap.get(id);
        if (index != null) grid.scrollToIndex(index);
    }

    public void selectAndScroll(Long id) {
        grid.select(new HWTypeTO(id));
        grid.getElement().callJsFunction("$server.scrollToId", id.toString());
    }

    private void populate() {
        CallbackDataProvider.FetchCallback<HWTypeTO, HWTypeTO> fetchCallback = q -> {
            OrderSpecifier<?>[] order = QuerydslUtil.transformOrdering(q.getSortOrders(), column -> {
                switch (column) {
                    case NAME_BIND:
                        return "name";
                    case COUNT_BIND:
                        return "count";
                    default:
                        return column;
                }
            });
            if (order.length == 0) {
                order = new OrderSpecifier[1];
                order[0] = QuerydslUtil.transformOrder(true, "name");
            }

            // potřebuju všechny Id, aby šlo poslepu volat scroll i tam, kde jsem ještě nebyl,
            // jinak bude scroll házet na indexMap NPE, protože jeho id ještě nemusí být naindexované
            List<Long> ids = hwService.getHWTypeIds(filterTO, order);
            int index = 0;
            for (Long id : ids)
                indexMap.put(id, index++);
            return hwService.getHWTypes(filterTO, q.getOffset(), q.getLimit(), order).stream();
        };
        CallbackDataProvider.CountCallback<HWTypeTO, HWTypeTO> countCallback = q -> hwService.countHWTypes(filterTO);
        grid.setDataProvider(DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
    }

    private void openCreateDialog() {
        HWTypeEditDialog.create(to -> {
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

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        Map<String, List<String>> params = new HashMap<>();
        if (lastId != null) params.put(ID_QUERY_TOKEN, List.of(lastId.toString()));
        if (filterTO.getName() != null) params.put(NAME_QUERY_TOKEN, List.of(filterTO.getName()));
        QueryParameters queryParams = new QueryParameters(params);
        lastId = null;

        String listURL = RouteConfiguration.forSessionScope().getUrl(HWTypesPage.class);
        UI.getCurrent().getPage().getHistory().replaceState(null, listURL + "?" + queryParams.getQueryString());

        if (NavigationTrigger.ROUTER_LINK == event.getTrigger())
            UI.getCurrent().getPage().getHistory().pushState(null, event.getLocation().getPathWithQueryParameters());
    }
}