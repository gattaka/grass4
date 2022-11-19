package cz.gattserver.grass.hw.ui.pages;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.ui.pages.template.OneColumnPage;
import cz.gattserver.grass.hw.HWSection;

@Route("hw")
@PageTitle("Evidence HW")
public class HWPage extends OneColumnPage {

	private static final long serialVersionUID = 3983638941237624740L;

	private Tabs tabSheet;

	private Tab overviewTab;
	private Tab typesTab;

	private Div pageLayout;

	public HWPage() {
		if (!SpringContextHelper.getBean(HWSection.class).isVisibleForRoles(getUser().getRoles()))
			throw new GrassPageException(403);
		init();
	}

	@Override
	protected void createColumnContent(Div layout) {
		tabSheet = new Tabs();
		layout.add(tabSheet);

		overviewTab = new Tab("Přehled");
		tabSheet.add(overviewTab);

		if (getUser().isAdmin()) {
			typesTab = new Tab("Typy zařízení");
			tabSheet.add(typesTab);
		}

		pageLayout = new Div();
		layout.add(pageLayout);

		tabSheet.addSelectedChangeListener(e -> {
			pageLayout.removeAll();
			switch (tabSheet.getSelectedIndex()) {
			default:
			case 0:
				switchOverviewTab();
				break;
			case 1:
				switchTypesTab();
				break;
			}
		});
		switchOverviewTab();
	}

	private HWItemsTab switchOverviewTab() {
		pageLayout.removeAll();
		HWItemsTab tab = new HWItemsTab();
		pageLayout.add(tab);
		tabSheet.setSelectedTab(overviewTab);
		return tab;
	}

	private HWTypesTab switchTypesTab() {
		pageLayout.removeAll();
		HWTypesTab tab = new HWTypesTab();
		pageLayout.add(tab);
		tabSheet.setSelectedTab(typesTab);
		return tab;
	}
}
