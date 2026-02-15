package cz.gattserver.grass.hw.ui;

import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;

import com.querydsl.core.types.OrderSpecifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.internal.AllowInert;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import cz.gattserver.common.FieldUtils;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.util.ReferenceHolder;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.grass.core.model.util.QuerydslUtil;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.util.TokenField;
import cz.gattserver.grass.core.ui.util.TokenField2;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.hw.interfaces.*;
import cz.gattserver.grass.hw.ui.pages.HWItemPage;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;

import cz.gattserver.grass.hw.service.HWService;
import org.apache.commons.lang3.StringUtils;

public class HWItemsGrid extends Div {

    private static final String ID_QUERY_TOKEN = "id";
    private static final String NAME_QUERY_TOKEN = "n";
    private static final String SUPERVIZED_FOR_QUERY_TOKEN = "sf";
    private static final String STATE_QUERY_TOKEN = "s";
    private static final String USED_IN_QUERY_TOKEN = "ui";
    private static final String TYPE_QUERY_TOKEN = "t";

    private static final String NAME_BIND = "nameBind";
    private static final String USED_IN_BIND = "usedInBind";
    private static final String SUPERVIZED_FOR_BIND = "supervizedForBind";
    private static final String PRICE_BIND = "priceBind";
    private static final String STATE_BIND = "stateBind";
    private static final String PURCHASE_DATE_BIND = "purchaseDateBind";

    private final HWService hwService;
    private final SecurityService securityFacade;

    private Grid<HWItemOverviewTO> grid;
    private TokenField2 hwTypesFilter;

    private Map<String, HWTypeBasicTO> tokenMap = new HashMap<>();
    private Map<Long, Integer> indexMap = new HashMap<>();
    private Binder<HWFilterTO> binder;

    private Div iconDiv;

    public HWItemsGrid(HWFilterTO filterTO, Consumer<HWItemOverviewTO> onSelect) {
        this.hwService = SpringContextHelper.getBean(HWService.class);
        this.securityFacade = SpringContextHelper.getBean(SecurityService.class);

        iconDiv = new Div();
        iconDiv.addClassName("hw-hover-icon");
        iconDiv.setVisible(false);
        iconDiv.getElement().setAttribute("popover", "manual");
        iconDiv.getElement().executeJs("this.showPopover()");
        UI.getCurrent().getElement().appendChild(iconDiv.getElement());

        binder = new Binder<>(HWFilterTO.class);
        binder.setBean(filterTO == null ? new HWFilterTO() : filterTO);

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
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.setWidthFull();
        grid.setHeight("480px");

        grid.addColumn(new IconRenderer<>(c -> {
            ImageIcon ii = HWUIUtils.chooseImageIcon(c);
            if (ii != null) {
                Image img = ii.createImage(c.getState().getName());
                img.addClassName(UIUtils.GRID_ICON_CSS_CLASS);
                img.setTitle(c.getState().getName());
                return img;
            } else {
                return new Span();
            }
        }, c -> "")).setFlexGrow(0).setWidth("31px").setHeader("").setTextAlign(ColumnTextAlign.CENTER);

        ComponentFactory componentFactory = new ComponentFactory();

        Column<HWItemOverviewTO> nameColumn = grid.addColumn(new ComponentRenderer<>(to -> {
            Anchor anchor = componentFactory.createAnchor(to.getName(), e -> onSelect.accept(to),
                    e -> UI.getCurrent().getPage()
                            .open(RouteConfiguration.forSessionScope().getUrl(HWItemPage.class, to.getId()), "_blank"));

            // addEventListener na mouseover má z nějakého důvodu prázdný event -- Vaadin bug?
            UI.getCurrent().getPage().executeJs("$0.onmouseover = function(event) {"
                    /*      */ + "let bound = document.body.getBoundingClientRect(); "
                    /*      */ + "$1.$server.imgShowCallback(\"" + to.getId()
                    /*      */ + "\", event.clientX - bound.x, event.clientY - bound.y); "
                    /*  */ + "}", anchor.getElement(), HWItemsGrid.this.getElement());
            anchor.getElement().addEventListener("mouseout", e -> iconDiv.setVisible(false));
            return anchor;
        })).setHeader("Název").setSortable(true).setKey(NAME_BIND).setResizable(true);

        // kontrola na null je tady jenom proto, aby při selectu (kdy se udělá
        // nový objekt a dá se mu akorát ID, které se porovnává) aplikace
        // nespadla na NPE -- což je trochu zvláštní, protože ve skutečnosti
        // žádný majetek nemá stav null.
        Column<HWItemOverviewTO> stateColumn =
                grid.addColumn(hw -> hw.getState() == null ? "" : hw.getState().getName()).setHeader("Stav")
                        .setKey(STATE_BIND).setWidth("120px").setFlexGrow(0).setSortable(true);
        Column<HWItemOverviewTO> usedInColumn =
                grid.addColumn(HWItemOverviewTO::getUsedInName).setKey(USED_IN_BIND).setHeader("Je součástí")
                        .setWidth("120px").setFlexGrow(0).setSortable(true);
        Column<HWItemOverviewTO> supervizedColumn =
                grid.addColumn(HWItemOverviewTO::getSupervizedFor).setKey(SUPERVIZED_FOR_BIND)
                        .setHeader("Spravováno pro").setWidth("120px").setFlexGrow(0).setSortable(true);
        if (securityFacade.getCurrentUser().isAdmin()) {
            grid.addColumn(hw -> FieldUtils.formatMoney(hw.getPrice())).setHeader("Cena").setKey(PRICE_BIND)
                    .setTextAlign(ColumnTextAlign.END).setWidth("100px").setFlexGrow(0).setSortable(true);
        }
        grid.addColumn(new LocalDateRenderer<>(HWItemOverviewTO::getPurchaseDate, "d. M. yyyy")).setHeader("Získáno")
                .setKey(PURCHASE_DATE_BIND).setTextAlign(ColumnTextAlign.END).setWidth("100px").setFlexGrow(0)
                .setSortable(true);

        HeaderRow filteringHeader = grid.appendHeaderRow();

        // Název
        TextField nameField = UIUtils.addHeaderTextField(filteringHeader.getCell(nameColumn), e -> populate());
        binder.bind(nameField, HWFilterTO::getName, HWFilterTO::setName);

        // Stav
        ComboBox<HWItemState> stavCombo =
                UIUtils.addHeaderComboBox(filteringHeader.getCell(stateColumn), HWItemState.class, HWItemState::getName,
                        e -> populate());
        binder.bind(stavCombo, HWFilterTO::getState, HWFilterTO::setState);
        stavCombo.setOverlayWidth("150px");

        // Je součástí
        TextField soucastField = UIUtils.addHeaderTextField(filteringHeader.getCell(usedInColumn), e -> populate());
        binder.bind(soucastField, HWFilterTO::getUsedInName, HWFilterTO::setUsedInName);

        // Spravován pro
        TextField spravovanField =
                UIUtils.addHeaderTextField(filteringHeader.getCell(supervizedColumn), e -> populate());
        binder.bind(spravovanField, HWFilterTO::getSupervizedFor, HWFilterTO::setSupervizedFor);

        populate();

        // Filtr na typy HW není veřejný, aby nenapovídal, co vše host nevidí
        if (securityFacade.getCurrentUser().isAdmin()) {
            for (HWTypeBasicTO type : hwService.getAllHWTypes())
                tokenMap.put(type.getName(), type);

            hwTypesFilter = new TokenField2(null, tokenMap.keySet());
            hwTypesFilter.getInputField().setWidth("200px");
            hwTypesFilter.setAllowNewItems(false);
            hwTypesFilter.getInputField().setPlaceholder("Filtrovat dle typu hw");
            binder.bind(hwTypesFilter, HWFilterTO::getTypes, HWFilterTO::setTypes);

            hwTypesFilter.addTokenAddListener(token -> populate());
            hwTypesFilter.addTokenRemoveListener(e -> populate());

            add(hwTypesFilter);
        }

        add(grid);
    }

    public static HWFilterTO processQueryToFilter(Map<String, List<String>> parametersMap) {
        HWFilterTO filterTO = new HWFilterTO();

        Set<String> types = new LinkedHashSet<>();
        for (String key : parametersMap.keySet()) {
            List<String> values = parametersMap.get(key);
            if (ID_QUERY_TOKEN.equals(key)) {
                filterTO.setId(Long.valueOf(values.get(0)));
            } else if (NAME_QUERY_TOKEN.equals(key)) {
                filterTO.setName(values.get(0));
            } else if (SUPERVIZED_FOR_QUERY_TOKEN.equals(key)) {
                filterTO.setSupervizedFor(values.get(0));
            } else if (STATE_QUERY_TOKEN.equals(key)) {
                try {
                    filterTO.setState(HWItemState.valueOf(values.get(0)));
                } catch (IllegalArgumentException e) {
                    // chybná neexistující konstanta
                }
            } else if (USED_IN_QUERY_TOKEN.equals(key)) {
                filterTO.setUsedInName(values.get(0));
            } else if (key.startsWith(TYPE_QUERY_TOKEN)) {
                types.add(values.get(0));
            }
        }
        if (!types.isEmpty()) filterTO.setTypes(types);

        return filterTO;
    }

    public static Map<String, String> processFilterToQuery(HWFilterTO filterTO) {
        Map<String, String> filterQuery = new HashMap<>();
        if (StringUtils.isNotBlank(filterTO.getName())) filterQuery.put(NAME_QUERY_TOKEN, filterTO.getName());
        if (StringUtils.isNotBlank(filterTO.getSupervizedFor()))
            filterQuery.put(SUPERVIZED_FOR_QUERY_TOKEN, filterTO.getSupervizedFor());
        if (filterTO.getState() != null) filterQuery.put(STATE_QUERY_TOKEN, filterTO.getState().name());
        if (StringUtils.isNotBlank(filterTO.getUsedInName()))
            filterQuery.put(USED_IN_QUERY_TOKEN, filterTO.getUsedInName());

        if (filterTO.getTypes() != null) {
            int i = 0;
            for (String type : filterTO.getTypes()) {
                i++;
                filterQuery.put(TYPE_QUERY_TOKEN + i, type);
            }
        }
        return filterQuery;
    }

    @ClientCallable
    private void imgShowCallback(Long id, double x, double y) {
        InputStream iconIs = hwService.getHWItemIconMiniFileInputStream(id);
        if (iconIs == null) return;
        iconDiv.removeAll();
        iconDiv.setVisible(true);
        String name = "hw-item-" + id;
        Image img = new Image(DownloadHandler.fromInputStream(e -> new DownloadResponse(iconIs, name, null, -1)), name);
        iconDiv.add(img);
        iconDiv.getStyle().set("left", (15 + x) + "px").set("top", y + "px");
        img.setMaxWidth("200px");
        img.setMaxHeight("200px");
    }

    public void populate() {
        if (!securityFacade.getCurrentUser().isAdmin()) binder.getBean().setPublicItem(true);

        if (grid.getDataProvider() == null || !(grid.getDataProvider() instanceof CallbackDataProvider)) {
            FetchCallback<HWItemOverviewTO, HWItemOverviewTO> fetchCallback = q -> {
                OrderSpecifier<?>[] order =
                        QuerydslUtil.transformOrdering(q.getSortOrders(), column -> switch (column) {
                            case PRICE_BIND -> "price";
                            case STATE_BIND -> "state";
                            case PURCHASE_DATE_BIND -> "purchaseDate";
                            case NAME_BIND -> "name";
                            case USED_IN_BIND -> "usedIn";
                            case SUPERVIZED_FOR_BIND -> "supervizedFor";
                            default -> column;
                        });
                if (order.length == 0) {
                    order = new OrderSpecifier[1];
                    order[0] = QuerydslUtil.transformOrder(true, "name");
                }

                // potřebuju všechny Id, aby šlo poslepu volat scroll i tam, kde jsem ještě nebyl,
                // jinak bude scroll házet na indexMap NPE, protože jeho id ještě nemusí být naindexované
                List<Long> ids = hwService.getHWItemIds(binder.getBean(), order);
                int index = 0;
                for (Long id : ids)
                    indexMap.put(id, index++);
                return hwService.getHWItems(binder.getBean(), q.getOffset(), q.getLimit(), order).stream();
            };
            CountCallback<HWItemOverviewTO, HWItemOverviewTO> countCallback =
                    q -> hwService.countHWItems(binder.getBean());
            grid.setDataProvider(DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
        } else {
            grid.getDataProvider().refreshAll();
        }
    }

    public void selectAndScroll(Long id) {
        HWItemOverviewTO to = new HWItemOverviewTO();
        to.setId(id);
        grid.select(to);
        grid.getElement().callJsFunction("$server.scrollToId", to.getId().toString());
    }

    private void onGridScrollToId(Long id) {
        Integer index = indexMap.get(id);
        if (index != null) grid.scrollToIndex(index);
    }

    public Grid<HWItemOverviewTO> getGrid() {
        return grid;
    }

    public HWFilterTO getFilterTO() {
        return binder.getBean();
    }

    public void setFilterTO(HWFilterTO filterTO) {
        binder.readBean(filterTO);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        iconDiv.getElement().removeFromParent();
        super.onDetach(detachEvent);
    }
}