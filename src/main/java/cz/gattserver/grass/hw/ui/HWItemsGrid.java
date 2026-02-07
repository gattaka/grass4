package cz.gattserver.grass.hw.ui;

import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;

import com.querydsl.core.types.OrderSpecifier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.internal.AllowInert;
import com.vaadin.flow.component.textfield.TextField;
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
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;

import cz.gattserver.grass.hw.service.HWService;

public class HWItemsGrid extends Div {

    private static final long serialVersionUID = -6094970451516214174L;

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

    public HWItemsGrid(Consumer<HWItemOverviewTO> onSelect) {
        this.hwService = SpringContextHelper.getBean(HWService.class);
        this.securityFacade = SpringContextHelper.getBean(SecurityService.class);

        iconDiv = new Div();
        iconDiv.setVisible(false);
        iconDiv.getStyle().set("position", "absolute").set("background", "white").set("padding", "5px")
                .set("border-radius", "3px").set("border", "1px solid #d5d5d5").set("z-index", "999");
        add(iconDiv);

        filterTO = new HWFilterTO();

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
            add(hwTypesFilter);
        }

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
            Long id = to.getId();
            Anchor a = new Anchor();
            a.getElement().addEventListener("click", event -> onSelect.accept(hwService.getHWOverviewItem(id)));
            a.setText(to.getName());
            // addEventListener na mouseover má z nějakého důvodu prázdný event -- Vaadin bug?
            UI.getCurrent().getPage().executeJs("$0.onmouseover = function(event) {"
                    /*      */ + "let bound = document.body.getBoundingClientRect(); "
                    /*      */ + "$1.$server.imgShowCallback(\"" + id
                    /*      */ + "\", event.clientX - bound.x, event.clientY - bound.y); "
                    /*  */ + "}", a.getElement(), HWItemsGrid.this.getElement());
            a.getElement().addEventListener("mouseout", e -> iconDiv.setVisible(false));
            return a;
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
            filterTO.setName(e.getValue());
            populate();
        });

        // Stav
        stavCombo =
                UIUtils.addHeaderComboBox(filteringHeader.getCell(stateColumn), HWItemState.class, HWItemState::getName,
                        e -> {
                            filterTO.setState(e.getValue());
                            populate();
                        });

        // Je součástí
        soucastField = UIUtils.addHeaderTextField(filteringHeader.getCell(usedInColumn), e -> {
            filterTO.setUsedInName(e.getValue());
            populate();
        });

        // Spravován pro
        spravovanField = UIUtils.addHeaderTextField(filteringHeader.getCell(supervizedColumn), e -> {
            filterTO.setSupervizedFor(e.getValue());
            populate();
        });

        populate();
        grid.sort(Arrays.asList(new GridSortOrder<>(nameColumn, SortDirection.ASCENDING)));

        add(grid);
    }

    @ClientCallable
    private void imgShowCallback(Long id, double x, double y) {
        InputStream iconIs = hwService.getHWItemIconMiniFileInputStream(id);
        if (iconIs == null) return;
        iconDiv.setVisible(true);
        iconDiv.removeAll();
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
}