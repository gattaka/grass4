package cz.gattserver.grass.campgames.ui.dialogs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
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
import com.vaadin.flow.server.streams.UploadHandler;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.campgames.CampgamesConfiguration;
import cz.gattserver.grass.campgames.interfaces.CampgameFileTO;
import cz.gattserver.grass.campgames.interfaces.CampgameTO;
import cz.gattserver.grass.campgames.service.CampgamesService;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.util.TokenField;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.grass.medic.interfaces.MedicamentTO;
import cz.gattserver.grass.medic.web.MedicamentDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class CampgameDialog extends EditWebDialog {

    private static Logger logger = LoggerFactory.getLogger(CampgameDialog.class);

    private final CampgamesService campgamesService;

    private CampgameTO originalTO;
    private Consumer<CampgameTO> onSave;
    private Upload upload;

    public static CampgameDialog detail(CampgameTO originalTO) {
        return new CampgameDialog(originalTO, null, true);
    }

    public static CampgameDialog edit(CampgameTO originalTO, Consumer<CampgameTO> onSave) {
        return new CampgameDialog(originalTO, onSave, false);
    }

    public static CampgameDialog create(Consumer<CampgameTO> onSave) {
        return new CampgameDialog(null, onSave, false);
    }

    private CampgameDialog(CampgameTO originalTO, Consumer<CampgameTO> onSave, boolean readOnly) {
        super("Hra", readOnly);

        setResizable(true);
        setWidth(800, Unit.PIXELS);
        setHeight(700, Unit.PIXELS);

        this.campgamesService = SpringContextHelper.getBean(CampgamesService.class);
        this.originalTO = originalTO;
        this.onSave = onSave;

        Tabs tabs = new Tabs();
        tabs.setWidthFull();
        layout.add(tabs);
        layout.setSizeFull();

        Tab detailsTab = new Tab("Info");
        tabs.add(detailsTab);

        Tab imgTab = new Tab("Přílohy");
        tabs.add(imgTab);

        tabs.addSelectedChangeListener(e -> {
            layout.removeAll();
            layout.add(tabs); // musí být taky layout, jinak začne blbnout výška textarea
            switch (tabs.getSelectedIndex()) {
                default:
                case 0:
                    createItemDetailsLayout(layout);
                    break;
                case 1:
                    createImgTab(layout);
                    break;
            }
        });
        createItemDetailsLayout(layout);
    }

    private void createItemDetailsLayout(VerticalLayout tabLayout) {
        CampgameTO formTO = new CampgameTO();
        Binder<CampgameTO> binder = new Binder<>(CampgameTO.class);
        binder.setBean(formTO);

        TextField nameField = new TextField("Název");
        nameField.setWidthFull();
        nameField.setReadOnly(readOnly);
        nameField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
        binder.forField(nameField).asRequired(componentFactory.createRequiredLabel())
                .bind(CampgameTO::getName, CampgameTO::setName);
        tabLayout.add(nameField);

        TokenField keywords = new TokenField(null, campgamesService.getAllCampgameKeywordNames());
        keywords.setAllowNewItems(true);
        keywords.setReadOnly(readOnly);
        keywords.getInputField().setPlaceholder("klíčové slovo");
        binder.forField(keywords).bind(CampgameTO::getKeywords, CampgameTO::setKeywords);
        tabLayout.add(keywords);

        HorizontalLayout itemDetailsLayout = new HorizontalLayout();
        itemDetailsLayout.setWidthFull();
        tabLayout.add(itemDetailsLayout);

        TextField originField = new TextField("Původ hry");
        originField.setWidthFull();
        originField.setReadOnly(readOnly);
        binder.forField(originField).bind(CampgameTO::getOrigin, CampgameTO::setOrigin);
        itemDetailsLayout.add(originField);

        TextField playersField = new TextField("Počet hráčů");
        playersField.setWidthFull();
        playersField.setReadOnly(readOnly);
        binder.forField(playersField).bind(CampgameTO::getPlayers, CampgameTO::setPlayers);
        itemDetailsLayout.add(playersField);

        TextField playTimeField = new TextField("Délka hry");
        playTimeField.setWidthFull();
        playTimeField.setReadOnly(readOnly);
        binder.forField(playTimeField).bind(CampgameTO::getPlayTime, CampgameTO::setPlayTime);
        itemDetailsLayout.add(playTimeField);

        TextField preparationTimeField = new TextField("Délka přípravy");
        preparationTimeField.setWidthFull();
        preparationTimeField.setReadOnly(readOnly);
        binder.forField(preparationTimeField).bind(CampgameTO::getPreparationTime, CampgameTO::setPreparationTime);
        itemDetailsLayout.add(preparationTimeField);

        TextArea descriptionField = new TextArea("Popis");
        descriptionField.setSizeFull();
        descriptionField.setMinHeight("0"); // magie, která řeší roztahování na výšku dle obsahu, namísto scrollbaru
        descriptionField.setReadOnly(readOnly);
        binder.forField(descriptionField).bind(CampgameTO::getDescription, CampgameTO::setDescription);
        tabLayout.add(descriptionField);

        getFooter().removeAll();
        getFooter().add(componentFactory.createDialogSubmitOrStornoLayout(e -> {
            try {
                CampgameTO beanTO = originalTO == null ? new CampgameTO() : originalTO;
                binder.writeBean(beanTO);
                beanTO.setKeywords(keywords.getValue());
                onSave.accept(beanTO);
                close();
            } catch (Exception ve) {
                if (!(ve instanceof ValidationException))
                    new ErrorDialog("Uložení se nezdařilo" + ve.getMessage()).open();
            }
        }, e -> close(), !readOnly));

        if (originalTO != null) binder.readBean(originalTO);
    }

    private void createImgTab(VerticalLayout tabLayout) {
        boolean isAdmin =
                SpringContextHelper.getBean(SecurityService.class).getCurrentUser().getRoles().contains(CoreRole.ADMIN);
        upload = new Upload(UploadHandler.toTempFile((metadata, file) -> {
            try {
                CampgameFileTO to = SpringContextHelper.getBean(CampgamesService.class)
                        .saveImagesFile(new FileInputStream(file), metadata.fileName(), originalTO.getId());
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
        }));
        // protože se jinak šířka uplatní bez ohledu na zmenšení o okraje
        upload.getStyle().set("width", "calc(100% - 2 * var(--lumo-space-m))");
        upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.setVisible(isAdmin);

        tabLayout.add(createGrid(tabLayout, isAdmin, upload));
        tabLayout.add(upload);
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

        if (isAdmin) {
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
                return button;
            })).setHeader("Smazat").setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);
        }

        grid.addColumn(new TextRenderer<>(CampgameFileTO::getSize)).setHeader("Velikost")
                .setTextAlign(ColumnTextAlign.END).setFlexGrow(0).setWidth("100px");

        return grid;
    }
}