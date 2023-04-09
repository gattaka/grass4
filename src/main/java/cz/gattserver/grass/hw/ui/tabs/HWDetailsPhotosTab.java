package cz.gattserver.grass.hw.ui.tabs;

import java.io.IOException;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.StreamResource;

import cz.gattserver.grass.hw.HWConfiguration;
import cz.gattserver.grass.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.dialogs.HWItemDetailsDialog;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.components.OperationsLayout;
import cz.gattserver.grass.core.ui.components.button.DeleteButton;
import cz.gattserver.grass.core.ui.components.button.DetailButton;
import cz.gattserver.grass.core.ui.util.ContainerDiv;
import cz.gattserver.grass.core.ui.util.GrassMultiFileBuffer;
import cz.gattserver.grass.core.ui.util.GridLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;

public class HWDetailsPhotosTab extends Div {

	private static final long serialVersionUID = 8602793883158440889L;

	private static final Logger logger = LoggerFactory.getLogger(HWDetailsPhotosTab.class);

	private transient HWService hwService;
	private transient SecurityService securityFacade;

	private HWItemTO hwItem;
	private HWItemDetailsDialog hwItemDetailDialog;
	private ContainerDiv containerDiv;

	public HWDetailsPhotosTab(HWItemTO hwItem, HWItemDetailsDialog hwItemDetailDialog) {
		SpringContextHelper.inject(this);
		this.hwItem = hwItem;
		this.hwItemDetailDialog = hwItemDetailDialog;
		init();
	}

	private HWService getHWService() {
		if (hwService == null)
			hwService = SpringContextHelper.getBean(HWService.class);
		return hwService;
	}

	private UserInfoTO getUser() {
		if (securityFacade == null)
			securityFacade = SpringContextHelper.getBean(SecurityService.class);
		return securityFacade.getCurrentUser();
	}

	private void init() {
		containerDiv = new ContainerDiv();
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

		OperationsLayout operationsLayout = new OperationsLayout(e -> hwItemDetailDialog.close());
		add(operationsLayout);
	}

	private void populateImages() {
		containerDiv.removeAll();
		GridLayout gridLayout = new GridLayout();
		containerDiv.add(gridLayout);

		int counter = 0;
		for (HWItemFileTO item : getHWService().getHWItemImagesFiles(hwItem.getId())) {
			if (counter == 0)
				gridLayout.newRow();
			counter = (counter + 1) % 5;

			Div itemDiv = new Div();
			itemDiv.getStyle().set("text-align", "center");
			itemDiv.setHeight("calc(" + UIUtils.BUTTON_SIZE_CSS_VAR + " + 200px + " + UIUtils.SPACING_CSS_VAR + ")");
			itemDiv.setWidth("200px");
			gridLayout.add(itemDiv);

			Image img = new Image(
					new StreamResource(item.getName(),
							() -> getHWService().getHWItemImagesFileInputStream(hwItem.getId(), item.getName())),
					item.getName());
			img.addClassName("thumbnail-200");
			itemDiv.add(img);

			Div buttonLayout = new Div();
			itemDiv.add(buttonLayout);

			DetailButton detailButton = new DetailButton(e -> UI.getCurrent().getPage()
					.open(HWConfiguration.HW_PATH + "/" + hwItem.getId() + "/img/" + item.getName()));
			detailButton.getStyle().set("margin-right", "var(--lumo-space-m)");
			buttonLayout.add(detailButton);

			if (getUser().isAdmin()) {
				Button delBtn = new DeleteButton(e -> new ConfirmDialog("Opravdu smazat foto HW položky ?", ev -> {
					getHWService().deleteHWItemImagesFile(hwItem.getId(), item.getName());
					populateImages();
					hwItemDetailDialog.refreshTabLabels();
				}).open());
				buttonLayout.add(delBtn);
			}
		}
	}
}