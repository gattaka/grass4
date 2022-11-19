package cz.gattserver.grass.hw.ui.dialogs;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.pages.HWDetailsDocsTab;
import cz.gattserver.grass.hw.ui.pages.HWDetailsInfoTab;
import cz.gattserver.grass.hw.ui.pages.HWDetailsPhotosTab;
import cz.gattserver.grass.hw.ui.pages.HWDetailsPrint3dTab;
import cz.gattserver.grass.hw.ui.pages.HWDetailsServiceNotesTab;
import cz.gattserver.grass.hw.ui.pages.HWItemsTab;

public class HWItemDetailsDialog extends Dialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient HWService hwService;

	private Tabs tabs;
	private Tab infoTab;
	private Tab serviceNotesTab;
	private Tab photosTab;
	private Tab print3dTab;
	private Tab docsTab;

	private Div tabLayout;

	private HWItemTO hwItem;
	private Long hwItemId;

	private HWItemsTab itemsTab;

	public HWItemDetailsDialog(HWItemsTab itemsTab, Long hwItemId) {
		this.itemsTab = itemsTab;
		this.hwItemId = hwItemId;
		this.hwItem = getHWService().getHWItem(hwItemId);

		setWidth("1080px");

		Div nameDiv = new Div(new Text(hwItem.getName()));
		nameDiv.getStyle().set("font-size", "15px").set("margin-bottom", "var(--lumo-space-m)")
				.set("font-weight", "bold").set("margin-top", "calc(var(--lumo-space-m) / -2)");
		add(nameDiv);

		infoTab = new Tab("Info");
		serviceNotesTab = new Tab(createServiceNotesTabLabel());
		photosTab = new Tab(createPhotosTabLabel());
		print3dTab = new Tab(createPrint3dTabLabel());
		docsTab = new Tab(createDocsTabLabel());

		tabs = new Tabs();
		tabs.add(infoTab, serviceNotesTab, photosTab, print3dTab, docsTab);
		add(tabs);

		tabLayout = new Div();
		tabLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		add(tabLayout);

		tabs.addSelectedChangeListener(e -> switchToTab(tabs.getSelectedIndex()));

		switchInfoTab();
	}

	private void switchToTab(int tabId) {
		switch (tabId) {
		default:
		case 0:
			switchInfoTab();
			break;
		case 1:
			switchServiceNotesTab();
			break;
		case 2:
			switchPhotosTab();
			break;
		case 3:
			switchPrint3dTab();
			break;
		case 4:
			switchDocsTab();
			break;
		}
	}

	public HWItemTO refreshItem() {
		this.hwItem = getHWService().getHWItem(hwItemId);
		refreshTabLabels();
		switchToTab(tabs.getSelectedIndex());
		return hwItem;
	}

	public void refreshTabLabels() {
		serviceNotesTab.setLabel(createServiceNotesTabLabel());
		photosTab.setLabel(createPhotosTabLabel());
		print3dTab.setLabel(createPrint3dTabLabel());
		docsTab.setLabel(createDocsTabLabel());
	}

	private String createServiceNotesTabLabel() {
		return "Záznamy (" + hwItem.getServiceNotes().size() + ")";
	}

	private String createPhotosTabLabel() {
		return "Fotografie (" + getHWService().getHWItemImagesFilesCount(hwItemId) + ")";
	}

	private String createPrint3dTabLabel() {
		return "3D Modely (" + getHWService().getHWItemPrint3dFilesCount(hwItemId) + ")";
	}

	private String createDocsTabLabel() {
		return "Dokumentace (" + getHWService().getHWItemDocumentsFilesCount(hwItemId) + ")";
	}

	private HWService getHWService() {
		if (hwService == null)
			hwService = SpringContextHelper.getBean(HWService.class);
		return hwService;
	}

	public void switchInfoTab() {
		tabLayout.removeAll();
		tabLayout.add(new HWDetailsInfoTab(itemsTab, hwItem, this));
		tabs.setSelectedTab(infoTab);
	}

	public void switchServiceNotesTab() {
		tabLayout.removeAll();
		tabLayout.add(new HWDetailsServiceNotesTab(hwItem, this));
		tabs.setSelectedTab(serviceNotesTab);
	}

	private void switchPhotosTab() {
		tabLayout.removeAll();
		tabLayout.add(new HWDetailsPhotosTab(hwItem, this));
		tabs.setSelectedTab(photosTab);
	}

	private void switchPrint3dTab() {
		tabLayout.removeAll();
		tabLayout.add(new HWDetailsPrint3dTab(hwItem, this));
		tabs.setSelectedTab(print3dTab);
	}

	private void switchDocsTab() {
		tabLayout.removeAll();
		tabLayout.add(new HWDetailsDocsTab(hwItem, this));
		tabs.setSelectedTab(docsTab);
	}

}
