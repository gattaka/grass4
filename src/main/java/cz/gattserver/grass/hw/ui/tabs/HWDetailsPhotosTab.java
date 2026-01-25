package cz.gattserver.grass.hw.ui.tabs;

import java.io.IOException;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.upload.Upload;

import cz.gattserver.grass.hw.HWConfiguration;
import cz.gattserver.grass.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.dialogs.HWItemDetailsDialog;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.util.GrassMultiFileBuffer;
import cz.gattserver.grass.core.ui.util.UIUtils;

public class HWDetailsPhotosTab extends Div {

    private static final long serialVersionUID = 8602793883158440889L;

    private static final Logger logger = LoggerFactory.getLogger(HWDetailsPhotosTab.class);

    private transient HWService hwService;
    private transient SecurityService securityFacade;

    private HWItemTO hwItem;
    private HWItemDetailsDialog hwItemDetailDialog;
    private Div containerDiv;

    public HWDetailsPhotosTab(HWItemTO hwItem, HWItemDetailsDialog hwItemDetailDialog) {
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

    private void init() {
        containerDiv = new Div();
        containerDiv.setId("hw-description-div");
        containerDiv.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        containerDiv.setHeight("500px");
        add(containerDiv);

        if (getUser().isAdmin()) {
            GrassMultiFileBuffer buffer = new GrassMultiFileBuffer();
            Upload upload = new Upload(buffer);
            upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
            upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
            upload.addSucceededListener(event -> {
                try {
                    getHWService().saveImagesFile(buffer.getInputStream(event.getFileName()), event.getFileName(),
                            hwItem);
                    populateImages();
                    hwItemDetailDialog.refreshTabLabels();
                } catch (IOException e) {
                    String msg = "Nezdařilo se uložit obrázek";
                    logger.error(msg, e);
                    new ErrorDialog(msg).open();
                }
            });
            add(upload);
        }
        populateImages();

        ComponentFactory componentFactory = new ComponentFactory();
        add(componentFactory.createDialogStornoLayout(e -> hwItemDetailDialog.close()));
    }

    private void populateImages() {
        containerDiv.removeAll();
        Div gridLayout = new Div();
        gridLayout.addClassName("hw-photos-div");
        containerDiv.add(gridLayout);

        for (HWItemFileTO item : getHWService().getHWItemImagesMiniFiles(hwItem.getId())) {

            Div itemDiv = new Div();
            itemDiv.addClassName("hw-photos-div-item");
            gridLayout.add(itemDiv);

            Image img = new Image(DownloadHandler.fromInputStream(e -> new DownloadResponse(
                    getHWService().getHWItemImagesMiniFileInputStream(hwItem.getId(), item.getName()), item.getName(),
                    null, -1)), item.getName());
            itemDiv.add(img);

            Div buttonLayout = new Div();
            itemDiv.add(buttonLayout);

            ComponentFactory componentFactory = new ComponentFactory();
            Div detailLink = componentFactory.createInlineButton("Detail", e -> UI.getCurrent().getPage()
                    .open(HWConfiguration.HW_PATH + "/" + hwItem.getId() + "/img/" + item.getName(), "_blank"));
            buttonLayout.add(detailLink);

            if (getUser().isAdmin()) {
                Button delBtn = componentFactory.createDeleteButton(ev -> {
                    getHWService().deleteHWItemImagesFile(hwItem.getId(), item.getName());
                    populateImages();
                    hwItemDetailDialog.refreshTabLabels();
                });
                buttonLayout.add(delBtn);
            }
        }
    }
}