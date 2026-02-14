package cz.gattserver.grass.hw.ui.tabs;

import java.io.FileInputStream;
import java.io.IOException;

import com.vaadin.flow.server.streams.UploadHandler;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.hw.ui.pages.HWItemPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;

import cz.gattserver.grass.hw.HWConfiguration;
import cz.gattserver.grass.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.service.HWService;

public class HWItemDocsTab extends Div {

    private static final Logger logger = LoggerFactory.getLogger(HWItemDocsTab.class);

    private final HWService hwService;
    private final SecurityService securityFacade;

    private HWItemTO hwItem;
    private HWItemPage hwItemPage;
    private Grid<HWItemFileTO> grid;

    public HWItemDocsTab(HWItemTO hwItem, HWItemPage hwItemPage) {
        this.securityFacade = SpringContextHelper.getBean(SecurityService.class);
        this.hwService = SpringContextHelper.getBean(HWService.class);
        this.hwItem = hwItem;
        this.hwItemPage = hwItemPage;
        init();
    }

    private UserInfoTO getUser() {
        return securityFacade.getCurrentUser();
    }

    private void populateGrid() {
        grid.setItems(hwService.getHWItemDocumentsFiles(hwItem.getId()));
        grid.getDataProvider().refreshAll();
    }

    private void downloadDocument(HWItemFileTO item) {
        UI.getCurrent().getPage().executeJs(
                "window.open('" + UIUtils.getContextPath() + "/" + HWConfiguration.HW_PATH + "/" + hwItem.getId() +
                        "/doc/" + item.getName() + "', '_blank');");
    }

    private void init() {
        grid = new Grid<>();
        grid.setWidthFull();
        grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        UIUtils.applyGrassDefaultStyle(grid);
        grid.addColumn(new TextRenderer<>(HWItemFileTO::getName)).setHeader("Název");
        grid.addColumn(new LocalDateTimeRenderer<>(HWItemFileTO::getLastModified, "d.MM.yyyy HH:mm"))
                .setKey("datum").setHeader("Datum");
        grid.addColumn(new TextRenderer<>(HWItemFileTO::getSize)).setHeader("Velikost")
                .setTextAlign(ColumnTextAlign.END);
        add(grid);

        populateGrid();

        if (getUser().isAdmin()) {
            Upload upload = new Upload(UploadHandler.toTempFile((metadata, file) -> {
                try {
                    hwService.saveDocumentsFile(new FileInputStream(file), metadata.fileName(), hwItem.getId());
                } catch (IOException e) {
                    String msg = "Nezdařilo se uložit soubor";
                    logger.error(msg, e);
                    new ErrorDialog(msg).open();
                }
            }));

            upload.addAllFinishedListener(e -> {
                populateGrid();
                hwItemPage.refreshTabLabels();
            });
            upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
            add(upload);
        }

        grid.addItemClickListener(e -> {
            if (e.getClickCount() > 1) downloadDocument(e.getItem());
        });

        ComponentFactory componentFactory = new ComponentFactory();
        Div operationsLayout = componentFactory.createButtonLayout();
        add(operationsLayout);

        operationsLayout.add(componentFactory.createDownloadGridButton(item -> downloadDocument(item), grid));

        if (getUser().isAdmin()) {
            Button deleteBtn = componentFactory.createDeleteGridButton(item -> {
                hwService.deleteHWItemDocumentsFile(hwItem.getId(), item.getName());
                populateGrid();
                hwItemPage.refreshTabLabels();
            }, grid);
            operationsLayout.add(deleteBtn);
        }
    }
}