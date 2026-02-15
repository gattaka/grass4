package cz.gattserver.grass.hw.ui.pages;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

import com.vaadin.flow.router.*;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.components.Breadcrumb;
import cz.gattserver.grass.core.ui.pages.MainView;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.tabs.HWItemDocsTab;
import cz.gattserver.grass.hw.ui.tabs.HWItemInfoTab;
import cz.gattserver.grass.hw.ui.tabs.HWItemPhotosTab;
import cz.gattserver.grass.hw.ui.tabs.HWItemPrint3dTab;
import cz.gattserver.grass.hw.ui.tabs.HWItemRecordsTab;

import java.util.function.Consumer;

@Route(value = "hw-item", layout = MainView.class)
public class HWItemPage extends Div implements HasUrlParameter<Long>, HasDynamicTitle {

    private transient HWService hwService;

    private Tabs tabs;
    private Tab infoTab;
    private Tab serviceNotesTab;
    private Tab photosTab;
    private Tab print3dTab;
    private Tab docsTab;

    private Div tabLayout;

    private HWItemTO hwItem;

    private Consumer<HWItemTO> onRefreshListener;

    public HWItemPage(HWService hwService) {
        this.hwService = hwService;
    }

    @Override
    public String getPageTitle() {
        return hwItem.getName();
    }

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        hwItem = hwService.findHWItem(parameter);

        if (Boolean.TRUE != hwItem.getPublicItem() &&
                !SpringContextHelper.getBean(SecurityService.class).getCurrentUser().getRoles()
                        .contains(CoreRole.ADMIN))
            throw new GrassPageException(403, "Nemáte oprávnění na tuto operaci");

        removeAll();

        ComponentFactory componentFactory = new ComponentFactory();
        Div layout = componentFactory.createOneColumnLayout();
        layout.addClassName("hw-item-layout");
        add(layout);

        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.resetBreadcrumb(new Breadcrumb.BreadcrumbElement(hwItem.getName(), HWItemPage.class, hwItem.getId()),
                new Breadcrumb.BreadcrumbElement("HW list", HWItemsPage.class));
        layout.add(breadcrumb);

        layout.add(new H2(hwItem.getName()));

        infoTab = new Tab("Info");
        serviceNotesTab = new Tab(createServiceNotesTabLabel());
        photosTab = new Tab(createPhotosTabLabel());
        print3dTab = new Tab(createPrint3dTabLabel());
        docsTab = new Tab(createDocsTabLabel());

        tabs = new Tabs();
        tabs.add(infoTab, serviceNotesTab, photosTab, print3dTab, docsTab);
        tabs.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(tabs);

        tabLayout = new Div();
        layout.add(tabLayout);

        tabs.addSelectedChangeListener(e -> switchToTab(tabs.getSelectedIndex()));

        switchInfoTab();
    }

    public Consumer<HWItemTO> getOnRefreshListener() {
        return onRefreshListener;
    }

    public HWItemPage setOnRefreshListener(Consumer<HWItemTO> onRefreshListener) {
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
        hwItem = getHWService().findHWItem(hwItem.getId());
        refreshTabLabels();
        switchToTab(tabs.getSelectedIndex());
        if (onRefreshListener != null) onRefreshListener.accept(hwItem);
        return hwItem;
    }

    public void refreshTabLabels() {
        serviceNotesTab.setLabel(createServiceNotesTabLabel());
        photosTab.setLabel(createPhotosTabLabel());
        print3dTab.setLabel(createPrint3dTabLabel());
        docsTab.setLabel(createDocsTabLabel());
    }

    private String createServiceNotesTabLabel() {
        return "Záznamy (" + hwItem.getItemRecords().size() + ")";
    }

    private String createPhotosTabLabel() {
        return "Fotografie (" + getHWService().findHWItemImagesMiniFilesCount(hwItem.getId()) + ")";
    }

    private String createPrint3dTabLabel() {
        return "3D Modely (" + getHWService().findHWItemPrint3dFilesCount(hwItem.getId()) + ")";
    }

    private String createDocsTabLabel() {
        return "Dokumentace (" + getHWService().findHWItemDocumentsFilesCount(hwItem.getId()) + ")";
    }

    private HWService getHWService() {
        if (hwService == null) hwService = SpringContextHelper.getBean(HWService.class);
        return hwService;
    }

    public void switchInfoTab() {
        tabLayout.removeAll();
        tabLayout.add(new HWItemInfoTab(hwItem));
        tabs.setSelectedTab(infoTab);
    }

    public void switchServiceNotesTab() {
        tabLayout.removeAll();
        tabLayout.add(new HWItemRecordsTab(hwItem, this));
        tabs.setSelectedTab(serviceNotesTab);
    }

    private void switchPhotosTab() {
        tabLayout.removeAll();
        tabLayout.add(new HWItemPhotosTab(hwItem, this));
        tabs.setSelectedTab(photosTab);
    }

    private void switchPrint3dTab() {
        tabLayout.removeAll();
        tabLayout.add(new HWItemPrint3dTab(hwItem, this));
        tabs.setSelectedTab(print3dTab);
    }

    private void switchDocsTab() {
        tabLayout.removeAll();
        tabLayout.add(new HWItemDocsTab(hwItem, this));
        tabs.setSelectedTab(docsTab);
    }
}