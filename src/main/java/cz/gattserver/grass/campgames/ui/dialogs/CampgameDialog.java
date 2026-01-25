package cz.gattserver.grass.campgames.ui.dialogs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.campgames.CampgamesConfiguration;
import cz.gattserver.grass.campgames.interfaces.CampgameFileTO;
import cz.gattserver.grass.campgames.interfaces.CampgameTO;
import cz.gattserver.grass.campgames.service.CampgamesService;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.util.GrassMultiFileBuffer;
import cz.gattserver.grass.core.ui.util.TokenField;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.spring.SpringContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class CampgameDialog extends EditWebDialog {

    private static final long serialVersionUID = -6773027334692911384L;

    private static Logger logger = LoggerFactory.getLogger(CampgameDialog.class);

    private transient CampgamesService campgamesService;

    private CampgameTO originalTO;
    private Consumer<CampgameTO> onSave;

    public CampgameDialog(CampgameTO originalTO, Consumer<CampgameTO> onSave) {
        super("Hra");
        init(originalTO, onSave);
    }

    private CampgamesService getCampgameService() {
        if (campgamesService == null) campgamesService = SpringContextHelper.getBean(CampgamesService.class);
        return campgamesService;
    }

    /**
     * @param originalTO opravuji údaje existující položky, nebo vytvářím novou (
     *                   {@code null}) ?
     */
    private void init(CampgameTO originalTO, Consumer<CampgameTO> onSave) {
        this.originalTO = originalTO;
        this.onSave = onSave;

        Tabs tabs = new Tabs();
        tabs.setWidthFull();
        layout.add(tabs);

        Tab detailsTab = new Tab("Info");
        tabs.add(detailsTab);

        Tab imgTab = new Tab("Přílohy");
        tabs.add(imgTab);

        VerticalLayout tabLayout = new VerticalLayout();
        tabLayout.setPadding(false);
        layout.add(tabLayout);

        tabs.addSelectedChangeListener(e -> {
            tabLayout.removeAll();
            switch (tabs.getSelectedIndex()) {
                default:
                case 0:
                    tabLayout.add(createItemDetailsLayout());
                    break;
                case 1:
                    tabLayout.add(createImgTab());
                    break;
            }
        });
        tabLayout.add(createItemDetailsLayout());
    }

    private Component createItemDetailsLayout() {
        VerticalLayout detailsLayout = new VerticalLayout();
        detailsLayout.setPadding(false);

        CampgameTO formTO = new CampgameTO();
        Binder<CampgameTO> binder = new Binder<>(CampgameTO.class);
        binder.setBean(formTO);

        TextField nameField = new TextField("Název");
        nameField.setWidthFull();
        nameField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
        binder.forField(nameField).asRequired("Název položky je povinný").bind("name");
        detailsLayout.add(nameField);

        TokenField keywords = new TokenField(getCampgameService().getAllCampgameKeywordNames());
        keywords.isEnabled();
        keywords.setAllowNewItems(true);
        keywords.getInputField().setPlaceholder("klíčové slovo");

        if (originalTO != null) for (String keyword : originalTO.getKeywords())
            keywords.addToken(keyword);
        detailsLayout.add(keywords);

        FormLayout winLayout = new FormLayout();
        winLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("600px", 4));
        winLayout.setWidth("600px");
        detailsLayout.add(winLayout);

        TextField originField = new TextField("Původ hry");
        originField.setWidthFull();
        binder.forField(originField).bind("origin");
        winLayout.add(originField);

        TextField playersField = new TextField("Počet hráčů");
        playersField.setWidthFull();
        binder.forField(playersField).bind("players");
        winLayout.add(playersField);

        TextField playTimeField = new TextField("Délka hry");
        playTimeField.setWidthFull();
        binder.forField(playTimeField).bind("playTime");
        winLayout.add(playTimeField);

        TextField preparationTimeField = new TextField("Délka přípravy");
        preparationTimeField.setWidthFull();
        binder.forField(preparationTimeField).bind("preparationTime");
        winLayout.add(preparationTimeField);

        TextArea descriptionField = new TextArea("Popis");
        descriptionField.setHeight("300px");
        binder.forField(descriptionField).bind("description");
        winLayout.add(descriptionField, 2);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        buttonLayout.setSpacing(false);
        buttonLayout.setWidthFull();
        layout.add(buttonLayout);

        Button createBtn = componentFactory.createSaveButton(e -> {
            try {
                CampgameTO beanTO = originalTO == null ? new CampgameTO() : originalTO;
                binder.writeBean(beanTO);
                beanTO.setKeywords(keywords.getValues());
                onSave.accept(beanTO);
                close();
            } catch (Exception ve) {
                if (!(ve instanceof ValidationException))
                    new ErrorDialog("Uložení se nezdařilo" + ve.getMessage()).open();
            }
        });
        buttonLayout.add(createBtn);

        buttonLayout.add(componentFactory.createStornoButton(e -> close()));

        if (originalTO != null) binder.readBean(originalTO);

        return detailsLayout;
    }

    private Component createImgTab() {
        VerticalLayout tabLayout = new VerticalLayout();
        tabLayout.setSizeFull();
        tabLayout.setPadding(false);
        tabLayout.setSpacing(false);

        boolean isAdmin =
                SpringContextHelper.getBean(SecurityService.class).getCurrentUser().getRoles().contains(CoreRole.ADMIN);
        GrassMultiFileBuffer buffer = new GrassMultiFileBuffer();

        // TODO tohle aktuálně bude fungovat jen u existujících záznamů
        Upload upload = new Upload(buffer);
        // protože se jinak šířka uplatní bez ohledu na zmenšení o okraje
        upload.getStyle().set("width", "calc(100% - 2 * var(--lumo-space-m))");
        upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.addSucceededListener(event -> {
            try {
                CampgameFileTO to = SpringContextHelper.getBean(CampgamesService.class)
                        .saveImagesFile(buffer.getInputStream(event.getFileName()), event.getFileName(),
                                originalTO.getId());
                tabLayout.removeAll();
                Grid<CampgameFileTO> grid = createGrid(tabLayout, isAdmin, upload);
                tabLayout.add(grid);
                tabLayout.add(upload);
                grid.select(to);
                // TODO
                //imgTab.setLabel(createImgTabCaption());
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
        List<CampgameFileTO> items =
                SpringContextHelper.getBean(CampgamesService.class).getCampgameImagesFiles(originalTO.getId());
        grid.setItems(items);
        grid.setSizeFull();
        UIUtils.applyGrassDefaultStyle(grid);
        grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        grid.getStyle().set("height", "calc(100% - 85px)");

        grid.addColumn(new TextRenderer<>(to -> to.getName())).setHeader("Název").setFlexGrow(100);

        grid.addColumn(new ComponentRenderer<>(to -> componentFactory.createInlineButton("Detail",
                        e -> UI.getCurrent().getPage()
                                .open(CampgamesConfiguration.CAMPGAMES_PATH + "/" + originalTO.getId() + "/" + to.getName()))))
                .setHeader("Detail").setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);

        grid.addColumn(new ComponentRenderer<>(to -> {
            Div button = componentFactory.createInlineButton("Smazat", be -> {
                new ConfirmDialog("Opravdu smazat?", e -> {
                    SpringContextHelper.getBean(CampgamesService.class)
                            .deleteCampgameImagesFile(originalTO.getId(), to.getName());
                    tabLayout.removeAll();
                    tabLayout.add(createGrid(tabLayout, isAdmin, upload));
                    tabLayout.add(upload);
                    // TODO
                    //imgTab.setLabel(createImgTabCaption());
                }).open();
            });
            button.setVisible(isAdmin);
            return button;
        })).setHeader("Smazat").setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);
        grid.addColumn(new TextRenderer<>(CampgameFileTO::getSize)).setHeader("Velikost")
                .setTextAlign(ColumnTextAlign.END).setFlexGrow(0).setWidth("60px");

        return grid;
    }

}