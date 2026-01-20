package cz.gattserver.grass.campgames.ui.dialogs;

import java.io.IOException;
import java.util.List;

import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.campgames.CampgamesConfiguration;
import cz.gattserver.grass.campgames.interfaces.CampgameFileTO;
import cz.gattserver.grass.campgames.interfaces.CampgameTO;
import cz.gattserver.grass.campgames.service.CampgamesService;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.components.button.CloseButton;
import cz.gattserver.grass.core.ui.components.button.ModifyButton;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.GrassMultiFileBuffer;
import cz.gattserver.grass.core.ui.util.GridLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.HtmlDiv;
import cz.gattserver.common.vaadin.InlineButton;

public class CampgameDetailDialog extends WebDialog {

    private static final long serialVersionUID = -6773027334692911384L;

    private static final Logger logger = LoggerFactory.getLogger(CampgameDetailDialog.class);

    private transient CampgamesService campgamesService;

    private CampgameTO campgameTO;
    private Long campgameId;

    private ChangeListener changeListener;

    private Tabs tabs;
    private Tab detailsTab;
    private Tab imgTab;

    private Div pageLayout;

    public CampgameDetailDialog(Long campgameId) {
        this.campgameId = campgameId;
        this.campgameTO = getCampgamesService().getCampgame(campgameId);

        tabs = new Tabs();
        tabs.setWidthFull();
        layout.add(tabs);

        pageLayout = new Div();
        pageLayout.setHeight("600px");
        pageLayout.setWidth("720px");
        layout.add(pageLayout);

        detailsTab = new Tab("Info");
        tabs.add(detailsTab);

        imgTab = new Tab(createImgTabCaption());
        tabs.add(imgTab);

        tabs.addSelectedChangeListener(e -> {
            pageLayout.removeAll();
            switch (tabs.getSelectedIndex()) {
                default:
                case 0:
                    switchDetailsTab();
                    break;
                case 1:
                    switchImgTab();
                    break;
            }
        });
        switchDetailsTab();
    }

    private void switchDetailsTab() {
        pageLayout.removeAll();
        pageLayout.add(createItemDetailsLayout(campgameTO));
        tabs.setSelectedTab(detailsTab);
    }

    private void switchImgTab() {
        pageLayout.removeAll();
        pageLayout.add(createImgTab());
        tabs.setSelectedTab(imgTab);
    }

    private String createImgTabCaption() {
        return "Přílohy (" + getCampgamesService().getCampgameImagesFilesCount(campgameId) + ")";
    }

    private CampgamesService getCampgamesService() {
        if (campgamesService == null) campgamesService = SpringContextHelper.getBean(CampgamesService.class);
        return campgamesService;
    }

    private Component createItemDetailsLayout(CampgameTO campgameTO) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);

        /**
         * Keywords
         */
        ButtonLayout tags = new ButtonLayout();
        campgameTO.getKeywords().forEach(keyword -> {
            Button token = new Button(keyword);
            tags.add(token);
        });
        if (!campgameTO.getKeywords().isEmpty()) layout.add(tags);

        GridLayout gridLayout = new GridLayout();
        gridLayout.addStrong("Název:").add(campgameTO.getName());

        gridLayout.newRow().addStrong("Původ:").add(campgameTO.getOrigin());
        gridLayout.addStrong("Počet hráčů:").add(campgameTO.getPlayers());

        gridLayout.newRow().addStrong("Délka hry:").add(campgameTO.getPlayTime());
        gridLayout.addStrong("Délky přípravy:").add(campgameTO.getPreparationTime());

        gridLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(gridLayout);

        HtmlDiv descDiv = new HtmlDiv(campgameTO.getDescription().replaceAll("\n", "<br/>"));
        descDiv.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        descDiv.addClassName("scroll-div");
        descDiv.setSizeFull();
        layout.add(descDiv);

        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        btnLayout.setWidthFull();
        btnLayout.setSpacing(false);
        btnLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        layout.add(btnLayout);

        ButtonLayout operationsLayout = new ButtonLayout();
        btnLayout.add(operationsLayout);
        operationsLayout.setVisible(SpringContextHelper.getBean(SecurityService.class).getCurrentUser().getRoles()
                .contains(CoreRole.ADMIN));

        /**
         * Oprava údajů existující hry
         */
        final ModifyButton fixBtn = new ModifyButton(e -> new CampgameCreateDialog(campgameTO) {
            private static final long serialVersionUID = -1397391593801030584L;

            @Override
            protected void onSuccess(CampgameTO dto) {
                if (changeListener != null) changeListener.onChange();
                switchDetailsTab();
            }
        }.open());
        operationsLayout.add(fixBtn);

        /**
         * Smazání hry
         */
        final Button deleteBtn = componentFactory.createDeleteButton(e -> {
            try {
                getCampgamesService().deleteCampgame(campgameId);
                if (changeListener != null) changeListener.onChange();
                CampgameDetailDialog.this.close();
            } catch (Exception ex) {
                new ErrorDialog("Nezdařilo se smazat vybranou položku").open();
            }
        });
        operationsLayout.add(deleteBtn);

        CloseButton closeBtn = new CloseButton(e -> close());
        btnLayout.add(closeBtn);

        return layout;
    }

    private Component createImgTab() {
        VerticalLayout tabLayout = new VerticalLayout();
        tabLayout.setSizeFull();
        tabLayout.setPadding(false);
        tabLayout.setSpacing(false);

        boolean isAdmin =
                SpringContextHelper.getBean(SecurityService.class).getCurrentUser().getRoles().contains(CoreRole.ADMIN);
        GrassMultiFileBuffer buffer = new GrassMultiFileBuffer();

        Upload upload = new Upload(buffer);
        // protože se jinak šířka uplatní bez ohledu na zmenšení o okraje
        upload.getStyle().set("width", "calc(100% - 2 * var(--lumo-space-m))");
        upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.addSucceededListener(event -> {
            try {
                CampgameFileTO to = getCampgamesService().saveImagesFile(buffer.getInputStream(event.getFileName()),
                        event.getFileName(), campgameTO);
                tabLayout.removeAll();
                Grid<CampgameFileTO> grid = createGrid(tabLayout, isAdmin, upload);
                tabLayout.add(grid);
                tabLayout.add(upload);
                grid.select(to);
                imgTab.setLabel(createImgTabCaption());
            } catch (IOException e) {
                String msg = "Nezdařilo se uložit obrázek ke hře";
                logger.error(msg, e);
                new ErrorDialog(msg).open();
            }
        });
        upload.setVisible(isAdmin);

        tabLayout.add(createGrid(tabLayout, isAdmin, upload));
        tabLayout.add(upload);

        return tabLayout;
    }

    private Grid<CampgameFileTO> createGrid(VerticalLayout tabLayout, boolean isAdmin, Upload upload) {
        Grid<CampgameFileTO> grid = new Grid<>();
        List<CampgameFileTO> items = getCampgamesService().getCampgameImagesFiles(campgameId);
        grid.setItems(items);
        grid.setSizeFull();
        UIUtils.applyGrassDefaultStyle(grid);
        grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        grid.getStyle().set("height", "calc(100% - 85px)");

        pageLayout.add(grid);

        grid.addColumn(new TextRenderer<>(to -> to.getName())).setHeader("Název").setFlexGrow(100);

        grid.addColumn(new ComponentRenderer<>(to -> new InlineButton("Detail", e -> UI.getCurrent().getPage()
                        .open(CampgamesConfiguration.CAMPGAMES_PATH + "/" + campgameTO.getId() + "/" + to.getName()))))
                .setHeader("Detail").setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);

        grid.addColumn(new ComponentRenderer<>(to -> {
            InlineButton button = new InlineButton("Smazat", be -> {
                new ConfirmDialog("Opravdu smazat?", e -> {
                    getCampgamesService().deleteCampgameImagesFile(campgameId, to.getName());
                    tabLayout.removeAll();
                    tabLayout.add(createGrid(tabLayout, isAdmin, upload));
                    tabLayout.add(upload);
                    imgTab.setLabel(createImgTabCaption());
                }).open();
            });
            button.setVisible(isAdmin);
            return button;
        })).setHeader("Smazat").setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);
        grid.addColumn(new TextRenderer<>(CampgameFileTO::getSize)).setHeader("Velikost")
                .setTextAlign(ColumnTextAlign.END).setFlexGrow(0).setWidth("60px");

        return grid;
    }

    public ChangeListener getChangeListener() {
        return changeListener;
    }

    public CampgameDetailDialog setChangeListener(ChangeListener changeListener) {
        this.changeListener = changeListener;
        return this;
    }

}
