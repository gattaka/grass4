package cz.gattserver.grass.hw.ui.tabs;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.common.vaadin.Strong;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.util.TableLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.hw.ui.pages.HWItemPage;
import cz.gattserver.grass.hw.ui.pages.HWPage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;

import cz.gattserver.common.util.CZAmountFormatter;
import cz.gattserver.common.util.MoneyFormatter;
import cz.gattserver.grass.hw.HWConfiguration;
import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.HWUIUtils;
import cz.gattserver.grass.hw.ui.dialogs.HWItemEditDialog;

public class HWDetailsInfoTab extends Div {

    private static final long serialVersionUID = 8602793883158440889L;

    private static final Logger logger = LoggerFactory.getLogger(HWDetailsInfoTab.class);

    @Autowired
    private HWService hwService;

    @Autowired
    private SecurityService securityFacade;

    private VerticalLayout hwImageLayout;
    private HWItemTO hwItem;

    private HWItemsTab itemsTab;

    public HWDetailsInfoTab(HWItemsTab itemsTab, HWItemTO hwItem) {
        SpringContextHelper.inject(this);
        setHeightFull();
        this.itemsTab = itemsTab;
        this.hwItem = hwItem;
        init();
    }

    private String createPriceString(BigDecimal price) {
        if (price == null) return "-";
        return MoneyFormatter.format(price);
    }

    private UserInfoTO getUser() {
        if (securityFacade == null) securityFacade = SpringContextHelper.getBean(SecurityService.class);
        return securityFacade.getCurrentUser();
    }

    private String createWarrantyYearsString(Integer warrantyYears) {
        return new CZAmountFormatter("rok", "roky", "let").format(warrantyYears);
    }

    // TODO ellipsis CSS?
    private String createShortName(String name) {
        int maxLength = 100;
        if (name.length() <= maxLength) return name;
        return name.substring(0, maxLength / 2 - 3) + "..." + name.substring(name.length() - maxLength / 2);
    }

    private void init() {
        ComponentFactory componentFactory = new ComponentFactory();
        Div tagsDiv = new Div();
        tagsDiv.setId("hw-tags-div");
        hwItem.getTypes().forEach(typeName -> {
            Div token = new Div(typeName);
            tagsDiv.add(token);
        });
        add(tagsDiv);

        HorizontalLayout outerLayout = new HorizontalLayout();
        outerLayout.setSpacing(true);
        outerLayout.setPadding(false);
        outerLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        add(outerLayout);

        hwImageLayout = new VerticalLayout();
        hwImageLayout.setSpacing(true);
        hwImageLayout.setPadding(false);
        hwImageLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        hwImageLayout.setWidth(200, Unit.PIXELS);
        outerLayout.add(hwImageLayout);
        createHWImageOrUpload(hwItem);

        Div itemDetailsLayout = new Div();
        itemDetailsLayout.setWidthFull();
        outerLayout.add(itemDetailsLayout);

        TableLayout tableLayout = new TableLayout();
        itemDetailsLayout.add(tableLayout);

        tableLayout.addStrong("Stav");
        tableLayout.addStrong("Získáno");
        if (getUser().isAdmin()) tableLayout.add(new Strong("Cena"));
        tableLayout.add(new Strong("Záruka"));
        tableLayout.addStrong("Spravováno pro");

        tableLayout.newRow();

        Div stateValue = new Div(new Text(hwItem.getState().getName()));
        stateValue.setMinWidth("100px");
        tableLayout.add(stateValue);

        DateTimeFormatter format = DateTimeFormatter.ofPattern("d.M.yyyy");
        Div purchDateValue =
                new Div(new Text(hwItem.getPurchaseDate() == null ? "-" : hwItem.getPurchaseDate().format(format)));
        purchDateValue.setMinWidth("100px");
        tableLayout.add(purchDateValue);

        if (getUser().isAdmin()) {
            Div priceValue = new Div(new Text(createPriceString(hwItem.getPrice())));
            priceValue.setMinWidth("100px");
            tableLayout.add(priceValue);
        }

        Div zarukaLayout = new Div();
        zarukaLayout.setMinWidth("100px");
        if (hwItem.getWarrantyYears() != null && hwItem.getWarrantyYears() > 0 && hwItem.getPurchaseDate() != null) {
            LocalDate endDate = hwItem.getPurchaseDate().plusYears(hwItem.getWarrantyYears());
            boolean isInWarranty = endDate.isAfter(LocalDate.now());
            Image emb = isInWarranty ? ImageIcon.TICK_16_ICON.createImage() : ImageIcon.DELETE_16_ICON.createImage();
            emb.setAlt("warranty");
            zarukaLayout.add(emb);
            emb.getStyle().set("margin-right", "5px").set("margin-bottom", "-3px");
            String zarukaContent =
                    hwItem.getWarrantyYears() + " " + createWarrantyYearsString(hwItem.getWarrantyYears()) + " (do " +
                            endDate.format(format) + ")";
            zarukaLayout.add(zarukaContent);
        } else {
            zarukaLayout.add("-");
        }
        tableLayout.add(zarukaLayout);

        tableLayout.add(new Span(StringUtils.isBlank(hwItem.getSupervizedFor()) ? "-" : hwItem.getSupervizedFor()));

        tableLayout.newRow();

        tableLayout.addStrong("Je součástí").setColSpan(5);
        tableLayout.newRow();

        if (hwItem.getUsedIn() == null) {
            tableLayout.add(new Span("-"));
        } else {
            // Samotný button se stále roztahoval, bez ohledu na nastavený width
            Div usedInBtn = componentFactory.createInlineButton(hwItem.getUsedIn().getName(),
                    e -> UI.getCurrent().navigate(HWItemPage.class, hwItem.getUsedIn().getId()));
            tableLayout.add(usedInBtn);
        }
        tableLayout.setColSpan(5);

        // Tabulka HW
        Grid<HWItemOverviewTO> grid = new Grid<>();
        UIUtils.applyGrassDefaultStyle(grid);
        grid.setSelectionMode(SelectionMode.NONE);
        grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        grid.setHeight("150px");

        grid.addColumn(new IconRenderer<>(c -> {
            ImageIcon ii = HWUIUtils.chooseImageIcon(c);
            if (ii != null) {
                Image img = ii.createImage(c.getState().getName());
                img.addClassName(UIUtils.GRID_ICON_CSS_CLASS);
                return img;
            } else {
                return new Span();
            }
        }, c -> "")).setFlexGrow(0).setWidth("31px").setHeader("").setTextAlign(ColumnTextAlign.CENTER);

        grid.addColumn(new ComponentRenderer<>(c -> componentFactory.createInlineButton(createShortName(c.getName()),
                        e -> UI.getCurrent().navigate(HWItemPage.class, c.getId())))).setHeader("Název součásti")
                .setFlexGrow(100);

        // kontrola na null je tady jenom proto, aby při selectu (kdy se udělá
        // nový objekt a dá se mu akorát ID, které se porovnává) aplikace
        // nespadla na NPE -- což je trochu zvláštní, protože ve skutečnosti
        // žádný majetek nemá stav null.
        grid.addColumn(hw -> hw.getState() == null ? "" : hw.getState().getName()).setHeader("Stav").setWidth("110px")
                .setFlexGrow(0);

        grid.setItems(hwService.getAllParts(hwItem.getId()));
        itemDetailsLayout.add(grid);

        H3 descrptionHeader = new H3("Popis");
        add(descrptionHeader);

        Div descriptionDiv = new Div();
        descriptionDiv.setId("hw-description-div");
        descriptionDiv.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        descriptionDiv.setHeight("300px");
        descriptionDiv.setText(hwItem.getDescription());
        add(descriptionDiv);

        if (getUser().isAdmin()) {
            Div operationsLayout = componentFactory.createButtonLayout();
            add(operationsLayout);

            final Button fixBtn = componentFactory.createEditButton(
                    e -> new HWItemEditDialog(hwService.getHWItem(hwItem.getId()),
                            to -> UI.getCurrent().getPage().reload()).open());
            operationsLayout.add(fixBtn);

            final Button deleteBtn = componentFactory.createDeleteButton(ev -> {
                try {
                    hwService.deleteHWItem(hwItem.getId());
                    UI.getCurrent().navigate(HWPage.class);
                    itemsTab.populate();
                } catch (Exception ex) {
                    new ErrorDialog("Nezdařilo se smazat vybranou položku").open();
                }
            });
            operationsLayout.add(deleteBtn);
        }
    }

    private void createHWImageOrUpload(final HWItemTO hwItem) {
        if (!tryCreateHWImage(hwItem) && getUser().isAdmin()) createHWItemImageUpload(hwItem);
    }

    /**
     * Pokusí se získat ikonu HW
     */
    private boolean tryCreateHWImage(final HWItemTO hwItem) {
        InputStream iconIs;
        iconIs = hwService.getHWItemIconMiniFileInputStream(hwItem.getId());
        if (iconIs == null) return false;

        hwImageLayout.removeAll();

        // musí se jmenovat s příponou, aby se vůbec zobrazil
        Image image =
                new Image(DownloadHandler.fromInputStream(e -> new DownloadResponse(iconIs, "icon", null, -1)), "icon");
        image.addClassName("thumbnail-200");

        hwImageLayout.add(image);
        hwImageLayout.getStyle().set("border", "");

        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setSpacing(true);
        btnLayout.setPadding(false);
        hwImageLayout.add(btnLayout);

        ComponentFactory componentFactory = new ComponentFactory();

        Button hwItemImageDetailBtn = componentFactory.createDetailButton(
                e -> UI.getCurrent().getPage().open(HWConfiguration.HW_PATH + "/" + hwItem.getId() + "/icon/show"));
        btnLayout.add(hwItemImageDetailBtn);

        if (getUser().isAdmin()) {
            Button hwItemImageDeleteBtn = componentFactory.createDeleteButton(e -> {
                hwService.deleteHWItemIconFile(hwItem.getId());
                createHWItemImageUpload(hwItem);
            });
            btnLayout.add(hwItemImageDeleteBtn);
        }
        return true;
    }

    /**
     * Vytváří form pro vložení ikony HW
     */
    private void createHWItemImageUpload(final HWItemTO hwItem) {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        // https://vaadin.com/components/vaadin-upload/java-examples
        Button uploadButton = new Button("Upload");
        upload.setUploadButton(uploadButton);
        Span dropLabel = new Span("Drop");
        upload.setDropLabel(dropLabel);
        upload.setAcceptedFileTypes("image/jpg", "image/jpeg", "image/png");
        upload.addSucceededListener(e -> {
            hwService.createHWItemIcon(buffer.getInputStream(), e.getFileName(), hwItem.getId());
            tryCreateHWImage(hwItem);
        });
        hwImageLayout.removeAll();
        hwImageLayout.getStyle().set("border", "1px solid lightgray");
        HorizontalLayout hl = new HorizontalLayout();
        hl.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        hl.add(upload);
        hl.setHeightFull();
        hwImageLayout.add(hl);
    }
}
