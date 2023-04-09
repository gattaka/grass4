package cz.gattserver.grass.hw.ui.tabs;

import java.io.IOException;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.components.OperationsLayout;
import cz.gattserver.grass.core.ui.components.button.DeleteGridButton;
import cz.gattserver.grass.core.ui.components.button.GridButton;
import cz.gattserver.grass.core.ui.util.GrassMultiFileBuffer;
import cz.gattserver.grass.core.ui.util.UIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;

import cz.gattserver.grass.hw.HWConfiguration;
import cz.gattserver.grass.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.dialogs.HWItemDetailsDialog;

public class HWDetailsDocsTab extends Div {

	private static final long serialVersionUID = 8602793883158440889L;

	private static final Logger logger = LoggerFactory.getLogger(HWDetailsDocsTab.class);

	private transient HWService hwService;
	private transient SecurityService securityFacade;

	private HWItemTO hwItem;
	private HWItemDetailsDialog hwItemDetailDialog;
	private Grid<HWItemFileTO> docsGrid;

	public HWDetailsDocsTab(HWItemTO hwItem, HWItemDetailsDialog hwItemDetailDialog) {
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

	private void populateDocsGrid() {
		docsGrid.setItems(getHWService().getHWItemDocumentsFiles(hwItem.getId()));
		docsGrid.getDataProvider().refreshAll();
	}

	private void downloadDocument(HWItemFileTO item) {
		UI.getCurrent().getPage().executeJs("window.open('" + UIUtils.getContextPath() + "/" + HWConfiguration.HW_PATH
				+ "/" + hwItem.getId() + "/doc/" + item.getName() + "', '_blank');");
	}

	private void init() {
		docsGrid = new Grid<>();
		docsGrid.setWidthFull();
		UIUtils.applyGrassDefaultStyle(docsGrid);
		docsGrid.addColumn(new TextRenderer<HWItemFileTO>(HWItemFileTO::getName)).setHeader("Název");
		docsGrid.addColumn(new LocalDateTimeRenderer<HWItemFileTO>(HWItemFileTO::getLastModified, "d.MM.yyyy HH:mm"))
				.setKey("datum").setHeader("Datum");
		docsGrid.addColumn(new TextRenderer<HWItemFileTO>(HWItemFileTO::getSize)).setHeader("Velikost")
				.setTextAlign(ColumnTextAlign.END);
		add(docsGrid);

		populateDocsGrid();

		if (getUser().isAdmin()) {
			GrassMultiFileBuffer buffer = new GrassMultiFileBuffer();
			Upload upload = new Upload(buffer);
			upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
			upload.addSucceededListener(event -> {
				try {
					getHWService().saveDocumentsFile(buffer.getInputStream(event.getFileName()), event.getFileName(),
							hwItem.getId());
					// refresh listu
					populateDocsGrid();
					hwItemDetailDialog.refreshTabLabels();
				} catch (IOException e) {
					String msg = "Nezdařilo se uložit soubor";
					logger.error(msg, e);
					new ErrorDialog(msg).open();
				}
			});
			add(upload);
		}

		docsGrid.addItemClickListener(e -> {
			if (e.getClickCount() > 1)
				downloadDocument(e.getItem());
		});

		OperationsLayout operationsLayout = new OperationsLayout(e -> hwItemDetailDialog.close());
		add(operationsLayout);

		GridButton<HWItemFileTO> downloadBtn = new GridButton<>("Stáhnout",
				set -> downloadDocument(set.iterator().next()), docsGrid);
		downloadBtn.setEnableResolver(set -> set.size() == 1);
		downloadBtn.setIcon(new Image(ImageIcon.DOWN_16_ICON.createResource(), "Stáhnout"));
		operationsLayout.add(downloadBtn);

		if (getUser().isAdmin()) {
			Button deleteBtn = new DeleteGridButton<>("Smazat záznam", items -> {
				HWItemFileTO item = items.iterator().next();
				getHWService().deleteHWItemDocumentsFile(hwItem.getId(), item.getName());
				populateDocsGrid();
				hwItemDetailDialog.refreshTabLabels();
			}, docsGrid);
			operationsLayout.add(deleteBtn);
		}
	}

}