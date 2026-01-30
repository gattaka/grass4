package cz.gattserver.grass.campgames.ui.pages;

import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.campgames.ui.CampgamesTab;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import cz.gattserver.grass.campgames.ui.CampgameKeywordsTab;
import cz.gattserver.grass.core.ui.pages.MainView;

@PageTitle("Táborové hry")
@Route(value = "campgames", layout = MainView.class)
public class CampgamesPage extends Div {

    private static final long serialVersionUID = -5354424168298678698L;

    private Tabs tabSheet;
    private Tab overviewTab;
    private Tab keywordsTab;

    private Div pageLayout;

    public CampgamesPage() {
        removeAll();
        ComponentFactory componentFactory = new ComponentFactory();

        Div layout = componentFactory.createOneColumnLayout();
        add(layout);

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

