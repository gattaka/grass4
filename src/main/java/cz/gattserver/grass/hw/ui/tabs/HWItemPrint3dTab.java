package cz.gattserver.grass.hw.ui.tabs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serial;

import com.vaadin.flow.server.streams.UploadHandler;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.stlviewer.STLViewer;
import cz.gattserver.grass.hw.HWRequestHandlerConfig;
import cz.gattserver.grass.hw.ui.pages.HWItemPage;
import lombok.extern.slf4j.Slf4j;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;

import cz.gattserver.grass.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.service.HWService;
import org.jspecify.annotations.NonNull;

@Slf4j
public class HWItemPrint3dTab extends Div {

    @Serial
    private static final long serialVersionUID = -4313383169978940513L;

    private final HWService hwService;
    private final SecurityService securityService;

    private final HWItemTO hwItem;
    private final HWItemPage hwItemPage;
    private Grid<HWItemFileTO> grid;

    private STLViewer stlViewer;

    public HWItemPrint3dTab(HWItemTO hwItem, HWItemPage hwItemPage) {
        this.securityService = SpringContextHelper.getBean(SecurityService.class);
        this.hwService = SpringContextHelper.getBean(HWService.class);
        this.hwItem = hwItem;
        this.hwItemPage = hwItemPage;
        init();
    }

    private UserInfoTO getUser() {
        return securityService.getCurrentUser();
    }

    private void populateGrid() {
        grid.setItems(hwService.findHWItemPrint3dFiles(hwItem.getId()));
        grid.getDataProvider().refreshAll();
    }

    private String getFileURL(HWItemFileTO item) {
        return UIUtils.getContextPath() + "/" + HWRequestHandlerConfig.HW_PATH + "/" + hwItem.getId() + "/print3d/" +
                item.getName();
    }

    private void downloadPrint3d(HWItemFileTO item) {
        UI.getCurrent().getPage().executeJs("window.open('" + getFileURL(item) + "', '_blank');");
    }

    private void init() {
        grid = new Grid<>();
        grid.setSizeFull();
        UIUtils.applyGrassDefaultStyle(grid);
        grid.addColumn(new TextRenderer<>(HWItemFileTO::getName)).setHeader("Název").setFlexGrow(1)
                .setResizable(true);
        grid.addColumn(new LocalDateTimeRenderer<>(HWItemFileTO::getLastModified, "d. MM. yyyy HH:mm"))
                .setKey("datum").setHeader("Datum").setWidth("150px").setFlexGrow(0).setResizable(true);
        grid.addColumn(new TextRenderer<>(HWItemFileTO::getSize)).setHeader("Velikost")
                .setTextAlign(ColumnTextAlign.END).setWidth("100px").setFlexGrow(0).setResizable(true);

        stlViewer = new STLViewer(null);
        stlViewer.getStyle().set("border", "1px solid #d1d1d1").set("box-sizing", "border-box")
                .set("background", "#fefefe");
        stlViewer.setHeightFull();
        stlViewer.setWidth("600px");

        HorizontalLayout layout = new HorizontalLayout(grid, stlViewer);
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setHeight("400px");
        layout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        add(layout);

        populateGrid();

        if (getUser().isAdmin()) {
            Upload upload = getUpload();
            add(upload);
        }

        grid.addItemClickListener(e -> {
            if (e.getClickCount() > 1) downloadPrint3d(e.getItem());
        });

        grid.addSelectionListener(item -> {
            if (item.getFirstSelectedItem().isEmpty()) return;
            HWItemFileTO to = item.getFirstSelectedItem().get();
            stlViewer.show(getFileURL(to));
        });

        ComponentFactory componentFactory = new ComponentFactory();
        Div operationsLayout = componentFactory.createButtonLayout();
        add(operationsLayout);

        operationsLayout.add(componentFactory.createDownloadGridButton(this::downloadPrint3d, grid));

        if (getUser().isAdmin()) {
            Button deleteBtn = componentFactory.createDeleteGridButton(item -> {
                hwService.deleteHWItemPrint3dFile(hwItem.getId(), item.getName());
                populateGrid();
                hwItemPage.refreshTabLabels();
            }, grid);
            operationsLayout.add(deleteBtn);
        }
    }

    private @NonNull Upload getUpload() {
        Upload upload = new Upload(UploadHandler.toTempFile((metadata, file) -> {
            try {
                hwService.savePrint3dFile(new FileInputStream(file), metadata.fileName(), hwItem.getId());
            } catch (IOException e) {
                String msg = "Nezdařilo se uložit soubor";
                log.error(msg, e);
                new ErrorDialog(msg).open();
            }
        }));
        upload.addAllFinishedListener(e -> {
            populateGrid();
            hwItemPage.refreshTabLabels();
        });
        upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        return upload;
    }
}