package cz.gattserver.grass.hw.ui.tabs;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import com.vaadin.flow.server.streams.UploadHandler;
import cz.gattserver.common.slideshow.ImageSlideshow;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.hw.ui.pages.HWItemPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.upload.Upload;

import cz.gattserver.grass.hw.HWConfiguration;
import cz.gattserver.grass.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.util.GrassMultiFileBuffer;
import cz.gattserver.grass.core.ui.util.UIUtils;

public class HWItemPhotosTab extends Div {

    private static final Logger logger = LoggerFactory.getLogger(HWItemPhotosTab.class);

    private final HWService hwService;
    private final SecurityService securityFacade;

    private HWItemTO hwItem;
    private Div containerDiv;
    private HWItemPage hwItemPage;

    public HWItemPhotosTab(HWItemTO hwItem, HWItemPage hwItemPage) {
        this.securityFacade = SpringContextHelper.getBean(SecurityService.class);
        this.hwService = SpringContextHelper.getBean(HWService.class);
        this.hwItem = hwItem;
        this.hwItemPage = hwItemPage;
        init();
    }


    private UserInfoTO getUser() {
        return securityFacade.getCurrentUser();
    }

    private void init() {
        containerDiv = new Div();
        containerDiv.setId("hw-description-div");
        containerDiv.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        containerDiv.setHeight("500px");
        add(containerDiv);

        if (getUser().isAdmin()) {
            Upload upload = new Upload(UploadHandler.toTempFile((metadata, file) -> {
                try {
                    hwService.saveImagesFile(new FileInputStream(file), metadata.fileName(), hwItem);
                } catch (IOException e) {
                    String msg = "Nezdařilo se uložit obrázek";
                    logger.error(msg, e);
                    new ErrorDialog(msg).open();
                }
            }));
            upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
            upload.addAllFinishedListener(e -> {
                populateImages();
                hwItemPage.refreshTabLabels();
            });
            add(upload);
        }
        populateImages();
    }

    private void populateImages() {
        containerDiv.removeAll();
        Div gridLayout = new Div();
        gridLayout.addClassName("hw-photos-div");
        containerDiv.add(gridLayout);

        List<HWItemFileTO> images = hwService.getHWItemImagesMiniFiles(hwItem.getId());
        for (int i = 0; i < images.size(); i++) {

            HWItemFileTO item = images.get(i);

            Div itemDiv = new Div();
            itemDiv.addClassName("hw-photos-div-item");
            gridLayout.add(itemDiv);

            Image img = new Image(DownloadHandler.fromInputStream(e -> new DownloadResponse(
                    hwService.getHWItemImagesMiniFileInputStream(hwItem.getId(), item.getName()), item.getName(),
                    null, -1)), item.getName());
            itemDiv.add(img);

            itemDiv.add(new Span(item.getName()));

            int finalIndex = i;
            img.addClickListener(e -> showItem(images, finalIndex));

            Div buttonLayout = new Div();
            itemDiv.add(buttonLayout);

            ComponentFactory componentFactory = new ComponentFactory();
            Div detailLink = componentFactory.createInlineButton("Detail",
                    e -> UI.getCurrent().getPage().open(createPhotoItemURL(item), "_blank"));
            buttonLayout.add(detailLink);

            if (getUser().isAdmin()) {
                Div deleteButton = componentFactory.createInlineButton("Smazat", e -> new ConfirmDialog(e2 -> {
                    hwService.deleteHWItemImagesFile(hwItem.getId(), item.getName());
                    populateImages();
                    hwItemPage.refreshTabLabels();
                }).open());
                deleteButton.getStyle().set("color", "red");
                buttonLayout.add(deleteButton);
            }
        }
    }

    private void showItem(List<HWItemFileTO> images, int index) {
        // nepoužíváme
        Consumer<Integer> pageUpdateListener = currentIndex -> {
        };

        Function<Integer, HWItemFileTO> itemByIndexProvider = i -> images.get(i);

        // Nemáme slideshow verze -- jen miniatury a full
        Function<HWItemFileTO, String> itemPathProvider = item -> createPhotoItemURL(item);

        ImageSlideshow<HWItemFileTO> slideshow =
                new ImageSlideshow<>(images.size(), pageUpdateListener, itemByIndexProvider, itemPathProvider,
                        itemPathProvider);
        hwItemPage.add(slideshow);
        slideshow.showItem(index);
    }

    private String createPhotoItemURL(HWItemFileTO item) {
        return HWConfiguration.HW_PATH + "/" + hwItem.getId() + "/img/" + item.getName();
    }
}