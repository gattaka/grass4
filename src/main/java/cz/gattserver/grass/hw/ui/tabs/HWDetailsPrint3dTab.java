package cz.gattserver.grass.hw.ui.tabs;

import java.io.IOException;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.util.GrassMultiFileBuffer;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.stlviewer.STLViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;

import cz.gattserver.grass.hw.HWConfiguration;
import cz.gattserver.grass.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.dialogs.HWItemDetailsDialog;

public class HWDetailsPrint3dTab extends Div {

    private static final long serialVersionUID = 8602793883158440889L;

    private static final Logger logger = LoggerFactory.getLogger(HWDetailsPrint3dTab.class);

    private transient HWService hwService;
    private transient SecurityService securityFacade;

    private HWItemTO hwItem;
    private HWItemDetailsDialog hwItemDetailDialog;
    private Grid<HWItemFileTO> print3dGrid;

    private STLViewer stlViewer;

    public HWDetailsPrint3dTab(HWItemTO hwItem, HWItemDetailsDialog hwItemDetailDialog) {
        SpringContextHelper.inject(this);
        this.hwItem = hwItem;
        this.hwItemDetailDialog = hwItemDetailDialog;
        init();
    }

    private HWService getHWService() {
        if (hwService == null) hwService = SpringContextHelper.getBean(HWService.class);
        return hwService;
    }

    private UserInfoTO getUser() {
        if (securityFacade == null) securityFacade = SpringContextHelper.getBean(SecurityService.class);
        return securityFacade.getCurrentUser();
    }

    private void populatePrint3dGrid() {
        print3dGrid.setItems(getHWService().getHWItemPrint3dFiles(hwItem.getId()));
        print3dGrid.getDataProvider().refreshAll();
    }

    private String getFileURL(HWItemFileTO item) {
        return UIUtils.getContextPath() + "/" + HWConfiguration.HW_PATH + "/" + hwItem.getId() + "/print3d/" +
                item.getName();
    }

    private void downloadPrint3d(HWItemFileTO item) {
        UI.getCurrent().getPage().executeJs("window.open('" + getFileURL(item) + "', '_blank');");
    }

    private void init() {
        print3dGrid = new Grid<>();
        print3dGrid.setSizeFull();
        UIUtils.applyGrassDefaultStyle(print3dGrid);
        print3dGrid.addColumn(new TextRenderer<>(HWItemFileTO::getName)).setHeader("Název");
        print3dGrid.addColumn(new LocalDateTimeRenderer<>(HWItemFileTO::getLastModified, "d.MM.yyyy HH:mm"))
                .setKey("datum").setHeader("Datum");
        print3dGrid.addColumn(new TextRenderer<>(HWItemFileTO::getSize)).setHeader("Velikost")
                .setTextAlign(ColumnTextAlign.END);

        stlViewer = new STLViewer(null);
        stlViewer.getStyle().set("border", "1px solid #d1d1d1").set("box-sizing", "border-box")
                .set("background", "#fefefe");
        stlViewer.setHeightFull();
        stlViewer.setWidth("400px");

        HorizontalLayout layout = new HorizontalLayout(print3dGrid, stlViewer);
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setHeight("400px");
        layout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        add(layout);

        populatePrint3dGrid();

        if (getUser().isAdmin()) {
            GrassMultiFileBuffer buffer = new GrassMultiFileBuffer();
            Upload upload = new Upload(buffer);
            upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
            upload.addSucceededListener(event -> {
                try {
                    getHWService().savePrint3dFile(buffer.getInputStream(event.getFileName()), event.getFileName(),
                            hwItem.getId());
                    // refresh listu
                    populatePrint3dGrid();
                    hwItemDetailDialog.refreshTabLabels();
                } catch (IOException e) {
                    String msg = "Nezdařilo se uložit soubor";
                    logger.error(msg, e);
                    new ErrorDialog(msg).open();
                }
            });
            add(upload);
        }

        print3dGrid.addItemClickListener(e -> {
            if (e.getClickCount() > 1) downloadPrint3d(e.getItem());
        });

        print3dGrid.addSelectionListener(item -> {
            if (!item.getFirstSelectedItem().isPresent()) return;
            HWItemFileTO to = item.getFirstSelectedItem().get();
            stlViewer.show(getFileURL(to));
        });

        ComponentFactory componentFactory = new ComponentFactory();
        HorizontalLayout operationsLayout = componentFactory.createDialogStornoLayout(e -> hwItemDetailDialog.close());
        add(operationsLayout);

        operationsLayout.addToStart(
                componentFactory.createDownloadGridButton(item -> downloadPrint3d(item), print3dGrid));

        if (getUser().isAdmin()) {
            Button deleteBtn = componentFactory.createDeleteGridButton(item -> {
                getHWService().deleteHWItemPrint3dFile(hwItem.getId(), item.getName());
                populatePrint3dGrid();
                hwItemDetailDialog.refreshTabLabels();
            }, print3dGrid);
            operationsLayout.add(deleteBtn);
        }
    }

}