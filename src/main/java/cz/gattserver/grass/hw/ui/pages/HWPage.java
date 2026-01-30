package cz.gattserver.grass.hw.ui.pages;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.*;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.pages.MainView;
import cz.gattserver.grass.hw.HWSection;
import cz.gattserver.grass.hw.interfaces.HWFilterTO;
import cz.gattserver.grass.hw.ui.HWUIUtils;
import cz.gattserver.grass.hw.ui.tabs.HWItemsTab;
import cz.gattserver.grass.hw.ui.tabs.HWTypesTab;

import java.util.*;

@PageTitle("Evidence HW")
@Route(value = "hw", layout = MainView.class)
public class HWPage extends Div implements HasUrlParameter<Long> {

    private static final long serialVersionUID = 3983638941237624740L;

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
        SecurityService securityService = SpringContextHelper.getBean(SecurityService.class);
        if (!SpringContextHelper.getBean(HWSection.class)
                .isVisibleForRoles(securityService.getCurrentUser().getRoles())) throw new GrassPageException(403);

        removeAll();
        ComponentFactory componentFactory = new ComponentFactory();

        Div layout = componentFactory.createOneColumnLayout();
        add(layout);

        Tabs tabSheet = new Tabs();
        layout.add(tabSheet);
        Tab overviewTab = new Tab("Přehled");
        tabSheet.add(overviewTab);

        if (securityService.getCurrentUser().isAdmin()) {
            Tab typesTab = new Tab("Typy zařízení");
            tabSheet.add(typesTab);
        }

        Div tabLayout = new Div();
        layout.add(tabLayout);

        HWItemsTab itemsTabContent = new HWItemsTab(this);

        tabSheet.addSelectedChangeListener(e -> {
            tabLayout.removeAll();
            switch (tabSheet.getSelectedIndex()) {
                default:
                case 0:
                    tabLayout.add(itemsTabContent);
                    break;
                case 1:
                    tabLayout.add(new HWTypesTab());
                    break;
            }
        });
        tabLayout.add(itemsTabContent);

        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();

        Map<String, List<String>> parametersMap = queryParameters.getParameters();
        HWFilterTO filterTO = HWUIUtils.processToQueryFilter(parametersMap);

        itemsTabContent.search(filterTO);

        if (parameter != null) {
            itemsTabContent.select(parameter);
            itemsTabContent.openDetailWindow(parameter);
        }
    }
}