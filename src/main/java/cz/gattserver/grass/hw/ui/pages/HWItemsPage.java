package cz.gattserver.grass.hw.ui.pages;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.*;
import com.vaadin.flow.router.internal.HasUrlParameterFormat;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.articles.ui.pages.ArticlesViewer;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.pages.MainView;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.hw.interfaces.HWFilterTO;
import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.HWItemsGrid;
import cz.gattserver.grass.hw.ui.HWUIUtils;
import cz.gattserver.grass.hw.ui.dialogs.HWItemDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PageTitle("Evidence HW")
@Route(value = "hw", layout = MainView.class)
public class HWItemsPage extends Div implements HasUrlParameter<String>, BeforeLeaveObserver {

    private static final long serialVersionUID = 3983638941237624740L;

    private final HWService hwService;
    private final SecurityService securityService;

    private HWItemsGrid itemsGrid;

    public HWItemsPage(HWService hwService, SecurityService securityService) {
        this.hwService = hwService;
        this.securityService = securityService;
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        removeAll();
        ComponentFactory componentFactory = new ComponentFactory();

        Div layout = componentFactory.createOneColumnLayout();
        add(layout);

        layout.add(HWUIUtils.createNavigationLayout());

        itemsGrid = new HWItemsGrid();
        itemsGrid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(itemsGrid);

        // Restore state when returning via back button
        QueryParameters params = event.getLocation().getQueryParameters();
        Map<String, List<String>> parametersMap = params.getParameters();
        HWFilterTO filterTO = HWUIUtils.processToQueryFilter(parametersMap);
        search(filterTO);
        if (filterTO.getId() != null) select(filterTO.getId());

        Div buttonLayout = componentFactory.createButtonLayout();
        layout.add(buttonLayout);

        if (securityService.getCurrentUser().isAdmin()) {
            // Založení nové položky HW
            Button newHWBtn = componentFactory.createCreateButton(e -> openItemWindow(null));
            buttonLayout.add(newHWBtn);

            // Kopie položky HW
            buttonLayout.add(
                    componentFactory.createCopyGridButton(item -> copyItemWindow(item.getId()), itemsGrid.getGrid()));
        }

        // Zobrazení detailů položky HW
        buttonLayout.add(
                componentFactory.createDetailGridButton(item -> navigateToDetail(item.getId()), itemsGrid.getGrid()));

        if (securityService.getCurrentUser().isAdmin()) {
            // Oprava údajů existující položky HW
            buttonLayout.add(componentFactory.createEditGridButton(item -> openItemWindow(item), itemsGrid.getGrid()));

            // Smazání položky HW
            Button deleteBtn = componentFactory.createDeleteGridButton(item -> deleteItem(item), itemsGrid.getGrid());
            buttonLayout.add(deleteBtn);
        }
    }


    private void deleteItem(HWItemOverviewTO item) {
        try {
            hwService.deleteHWItem(item.getId());
            populate();
        } catch (Exception ex) {
            new ErrorDialog("Nezdařilo se smazat vybranou položku").open();
        }
    }

    private void openItemWindow(HWItemOverviewTO hwItemOverviewTO) {
        HWItemTO hwItem = null;
        if (hwItemOverviewTO != null) hwItem = hwService.getHWItem(hwItemOverviewTO.getId());
        new HWItemDialog(hwItem == null ? null : hwService.getHWItem(hwItem.getId()), to -> {
            to.setId(hwService.saveHWItem(to));
            populate();
            HWItemOverviewTO filterTO = new HWItemOverviewTO();
            filterTO.setId(to.getId());
            itemsGrid.getGrid().select(filterTO);
            if (hwItemOverviewTO == null) navigateToDetail(to.getId());
        }).open();
    }

    public void navigateToDetail(Long id) {
        UI.getCurrent().navigate(HWItemPage.class, id);
    }

    private void copyItemWindow(Long id) {
        Long newId = hwService.copyHWItem(id);
        populate();
        HWItemOverviewTO newTO = new HWItemOverviewTO();
        newTO.setId(newId);
        itemsGrid.getGrid().select(newTO);
    }

    public void openDetailWindow(Long id) {
        UI.getCurrent().navigate(HWItemPage.class, id);
    }

    public void populate() {
        itemsGrid.populate();
    }

    public void select(Long idParameter) {
        itemsGrid.selectAndScroll(idParameter);
    }

    public void search(HWFilterTO filterTO) {
        itemsGrid.setFilterTO(filterTO);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        if (event.getNavigationTarget() == HWItemPage.class) {
            // TODO filtry
            Long targetId = event.getRouteParameters().getLong(HasUrlParameterFormat.PARAMETER_NAME).get();
            Map<String, List<String>> params = new HashMap<>();
            params.put("id", List.of(targetId.toString()));
            QueryParameters queryParams = new QueryParameters(params);
            String listURL = RouteConfiguration.forSessionScope().getUrl(HWItemsPage.class);
            UI.getCurrent().getPage().getHistory().replaceState(null, listURL + "?" + queryParams.getQueryString());

            String detailURL = RouteConfiguration.forSessionScope().getUrl(HWItemPage.class, targetId);
            UI.getCurrent().getPage().getHistory().pushState(null, detailURL);
        }
    }
}