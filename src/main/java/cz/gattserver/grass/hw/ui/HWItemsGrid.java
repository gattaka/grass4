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
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import cz.gattserver.common.FieldUtils;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.grass.core.model.util.QuerydslUtil;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.util.TokenField;
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
import cz.gattserver.grass.hw.ui.pages.HWItemsPage;
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
    private TokenField hwTypesFilter;

    private Map<String, HWTypeBasicTO> tokenMap = new HashMap<>();
    private Map<Long, Integer> indexMap = new HashMap<>();
    private HWFilterTO filterTO;

    private TextField nameField;
    private ComboBox<HWItemState> stavCombo;
    private TextField soucastField;
    private TextField spravovanField;

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

        this.filterTO = filterTO == null ? new HWFilterTO() : filterTO;

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

        Column<HWItemOverviewTO> nameColumn = grid.addColumn(new ComponentRenderer<>(to -> {
            Anchor anchor = new Anchor();
            anchor.setText(to.getName());
            anchor.getElement().addEventListener("mousedown", event -> {
                int button = event.getEventData().get("event.button").asInt();
                if (button == 1) {
                    UI.getCurrent().getPage()
                            .open(RouteConfiguration.forSessionScope().getUrl(HWItemPage.class, to.getId()), "_blank");
                } else {
                    onSelect.accept(to);
                }
            }).addEventData("event.button").preventDefault();

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
        nameField = UIUtils.addHeaderTextField(filteringHeader.getCell(nameColumn), e -> {
            this.filterTO.setName(e.getValue());
            populate();
        });

        // Stav
        stavCombo =
                UIUtils.addHeaderComboBox(filteringHeader.getCell(stateColumn), HWItemState.class, HWItemState::getName,
                        e -> {
                            this.filterTO.setState(e.getValue());
                            populate();
                        });

        // Je součástí
        soucastField = UIUtils.addHeaderTextField(filteringHeader.getCell(usedInColumn), e -> {
            this.filterTO.setUsedInName(e.getValue());
            populate();
        });

        // Spravován pro
        spravovanField = UIUtils.addHeaderTextField(filteringHeader.getCell(supervizedColumn), e -> {
            this.filterTO.setSupervizedFor(e.getValue());
            populate();
        });

        populate();

        // Filtr na typy HW není veřejný, aby nenapovídal, co vše host nevidí
        if (securityFacade.getCurrentUser().isAdmin()) {
            for (HWTypeBasicTO type : hwService.getAllHWTypes())
                tokenMap.put(type.getName(), type);

            hwTypesFilter = new TokenField(tokenMap.keySet());
            hwTypesFilter.getInputField().setWidth("200px");
            hwTypesFilter.addTokenAddListener(token -> populate());
            hwTypesFilter.addTokenRemoveListener(e -> populate());
            hwTypesFilter.setAllowNewItems(false);
            hwTypesFilter.getInputField().setPlaceholder("Filtrovat dle typu hw");

            if (filterTO.getTypes() != null && !filterTO.getTypes().isEmpty()) {
                for (String type : filterTO.getTypes())
                    hwTypesFilter.addToken(type);
            }

            add(hwTypesFilter);
        }

        add(grid);
    }

    public static HWFilterTO processQueryToFilter(Map<String, List<String>> parametersMap) {
        HWFilterTO filterTO = new HWFilterTO();

        List<String> types = new ArrayList<>();
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
        if (!securityFacade.getCurrentUser().isAdmin()) filterTO.setPublicItem(true);

        if (hwTypesFilter != null) filterTO.setTypes(hwTypesFilter.getValues());

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
                List<Long> ids = hwService.getHWItemIds(filterTO, order);
                int index = 0;
                for (Long id : ids)
                    indexMap.put(id, index++);
                return hwService.getHWItems(filterTO, q.getOffset(), q.getLimit(), order).stream();
            };
            CountCallback<HWItemOverviewTO, HWItemOverviewTO> countCallback = q -> hwService.countHWItems(filterTO);
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
        return filterTO;
    }

    public void setFilterTO(HWFilterTO filterTO) {
        if (filterTO.getName() != null) nameField.setValue(filterTO.getName());
        if (filterTO.getState() != null) stavCombo.setValue(filterTO.getState());
        if (filterTO.getUsedInName() != null) soucastField.setValue(filterTO.getUsedInName());
        if (filterTO.getSupervizedFor() != null) spravovanField.setValue(filterTO.getSupervizedFor());

        if (hwTypesFilter != null && filterTO.getTypes() != null) hwTypesFilter.setValues(filterTO.getTypes());
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        iconDiv.getElement().removeFromParent();
        super.onDetach(detachEvent);
    }
}