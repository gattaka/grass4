package cz.gattserver.grass.campgames.ui.pages;

import cz.gattserver.grass.campgames.ui.CampgamesTab;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import cz.gattserver.grass.campgames.ui.CampgameKeywordsTab;
import cz.gattserver.grass.core.ui.pages.template.OneColumnPage;

@Route("campgames")
@PageTitle("Táborové hry")
public class CampgamesPage extends OneColumnPage {

	private static final long serialVersionUID = -5354424168298678698L;

	private Tabs tabSheet;
	private Tab overviewTab;
	private Tab keywordsTab;

	private Div pageLayout;

	public CampgamesPage() {
		init();
	}

	@Override
	protected void createColumnContent(Div layout) {
		tabSheet = new Tabs();
		layout.add(tabSheet);

		pageLayout = new Div();
		layout.add(pageLayout);

		overviewTab = new Tab();
		overviewTab.setLabel("Přehled");
		tabSheet.add(overviewTab);

		keywordsTab = new Tab();
		keywordsTab.setLabel("Klíčová slova");
		tabSheet.add(keywordsTab);

		tabSheet.addSelectedChangeListener(e -> {
			pageLayout.removeAll();
			switch (tabSheet.getSelectedIndex()) {
			default:
			case 0:
				switchCampgamesTab();
				break;
			case 1:
				switchCampgameKeywordsTab();
				break;
			}
		});
		switchCampgamesTab();
	}

	private void switchCampgamesTab() {
		pageLayout.removeAll();
		pageLayout.add(new CampgamesTab());
		tabSheet.setSelectedTab(overviewTab);
	}

	private void switchCampgameKeywordsTab() {
		pageLayout.removeAll();
		pageLayout.add(new CampgameKeywordsTab());
		tabSheet.setSelectedTab(keywordsTab);
	}
}
