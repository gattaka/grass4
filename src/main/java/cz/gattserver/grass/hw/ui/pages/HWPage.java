package cz.gattserver.grass.hw.ui.pages;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.*;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.ui.pages.template.OneColumnPage;
import cz.gattserver.grass.hw.HWSection;
import cz.gattserver.grass.hw.ui.tabs.HWItemsTab;
import cz.gattserver.grass.hw.ui.tabs.HWTypesTab;

@Route("hw")
@PageTitle("Evidence HW")
public class HWPage extends OneColumnPage implements HasUrlParameter<Long> {

	private static final long serialVersionUID = 3983638941237624740L;

	private Tabs tabSheet;

	private Tab overviewTab;
	private Tab typesTab;

	private HWItemsTab itemsTab;

	private Div layout;
	private Div pageLayout;
	private Long idParameter;

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
		if (!SpringContextHelper.getBean(HWSection.class).isVisibleForRoles(getUser().getRoles()))
			throw new GrassPageException(403);

		idParameter = parameter;
		if (layout == null) {
			init();
		} else {
			layout.removeAll();
			createContent();
		}

		if (idParameter != null) {
			itemsTab.select(idParameter);
			itemsTab.openDetailWindow(idParameter);
		}
	}

	@Override
	protected void createColumnContent(Div contentLayout) {
		layout = new Div();
		contentLayout.add(layout);
		createContent();
	}

	private void createContent() {
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

		itemsTab = new HWItemsTab();

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
		pageLayout.add(itemsTab);
		tabSheet.setSelectedTab(overviewTab);
		return itemsTab;
	}

	private HWTypesTab switchTypesTab() {
		pageLayout.removeAll();
		HWTypesTab tab = new HWTypesTab();
		pageLayout.add(tab);
		tabSheet.setSelectedTab(typesTab);
		return tab;
	}
}