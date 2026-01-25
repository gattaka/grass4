package cz.gattserver.grass.hw.ui.dialogs;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.campgames.CampgamesRole;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.tabs.HWDetailsDocsTab;
import cz.gattserver.grass.hw.ui.tabs.HWDetailsInfoTab;
import cz.gattserver.grass.hw.ui.tabs.HWDetailsPhotosTab;
import cz.gattserver.grass.hw.ui.tabs.HWDetailsPrint3dTab;
import cz.gattserver.grass.hw.ui.tabs.HWDetailsServiceNotesTab;
import cz.gattserver.grass.hw.ui.tabs.HWItemsTab;

import java.util.function.Consumer;

public class HWItemDetailsDialog extends WebDialog {

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

	private HWItemsTab itemsTab;

	private Consumer<HWItemTO> onRefreshListener;

	public HWItemDetailsDialog(HWItemsTab itemsTab, HWItemTO hwItem) {
        super(hwItem.getName());
        this.hwItem = hwItem;
        this.itemsTab = itemsTab;

        setDraggable(true);

		if (Boolean.TRUE != hwItem.getPublicItem() && !SpringContextHelper.getBean(SecurityService.class).getCurrentUser().getRoles()
				.contains(CoreRole.ADMIN))
			throw new GrassPageException(403, "Nemáte oprávnění na tuto operaci");

		setWidth("1120px");

		infoTab = new Tab("Info");
		serviceNotesTab = new Tab(createServiceNotesTabLabel());
		photosTab = new Tab(createPhotosTabLabel());
		print3dTab = new Tab(createPrint3dTabLabel());
		docsTab = new Tab(createDocsTabLabel());

		tabs = new Tabs();
		tabs.add(infoTab, serviceNotesTab, photosTab, print3dTab, docsTab);
		add(tabs);

		tabLayout = new Div();
		add(tabLayout);

		tabs.addSelectedChangeListener(e -> switchToTab(tabs.getSelectedIndex()));

		switchInfoTab();
	}

	public Consumer<HWItemTO> getOnRefreshListener() {
		return onRefreshListener;
	}

	public HWItemDetailsDialog setOnRefreshListener(Consumer<HWItemTO> onRefreshListener) {
		this.onRefreshListener = onRefreshListener;
		return this;
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
		hwItem = getHWService().getHWItem(hwItem.getId());
		refreshTabLabels();
		switchToTab(tabs.getSelectedIndex());
		if (onRefreshListener != null)
			onRefreshListener.accept(hwItem);
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
		return "Fotografie (" + getHWService().getHWItemImagesMiniFilesCount(hwItem.getId()) + ")";
	}

	private String createPrint3dTabLabel() {
		return "3D Modely (" + getHWService().getHWItemPrint3dFilesCount(hwItem.getId()) + ")";
	}

	private String createDocsTabLabel() {
		return "Dokumentace (" + getHWService().getHWItemDocumentsFilesCount(hwItem.getId()) + ")";
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