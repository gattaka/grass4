package cz.gattserver.grass.hw.ui.pages;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.*;
import com.vaadin.flow.router.internal.HasUrlParameterFormat;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
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

import java.util.List;
import java.util.Map;

@PageTitle("Evidence HW")
@Route(value = "hw", layout = MainView.class)
public class HWItemsPage extends Div implements HasUrlParameter<String> {

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

        QueryParameters params = event.getLocation().getQueryParameters();
        Map<String, List<String>> parametersMap = params.getParameters();
        HWFilterTO filterTO = HWItemsGrid.processQueryToFilter(parametersMap);

        itemsGrid = new HWItemsGrid(filterTO,to -> onDetail(to.getId()));
        itemsGrid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(itemsGrid);

        Div buttonLayout = componentFactory.createButtonLayout();
        layout.add(buttonLayout);

        if (securityService.getCurrentUser().isAdmin()) {
            // Založení nové položky HW
            Button newHWBtn = componentFactory.createCreateButton(e -> onEdit(null));
            buttonLayout.add(newHWBtn);

            // Kopie položky HW
            buttonLayout.add(componentFactory.createCopyGridButton(item -> onCopy(item.getId()), itemsGrid.getGrid()));
        }

        // Zobrazení detailů položky HW
        buttonLayout.add(componentFactory.createDetailGridButton(item -> onDetail(item.getId()), itemsGrid.getGrid()));

        if (securityService.getCurrentUser().isAdmin()) {
            // Oprava údajů existující položky HW
            buttonLayout.add(componentFactory.createEditGridButton(item -> onEdit(item), itemsGrid.getGrid()));

            // Smazání položky HW
            Button deleteBtn = componentFactory.createDeleteGridButton(item -> deleteItem(item), itemsGrid.getGrid());
            buttonLayout.add(deleteBtn);
        }

        if (filterTO.getId() != null) select(filterTO.getId());
    }

    private void deleteItem(HWItemOverviewTO item) {
        try {
            hwService.deleteHWItem(item.getId());
            populate();
        } catch (Exception ex) {
            new ErrorDialog("Nezdařilo se smazat vybranou položku").open();
        }
    }

    private void onEdit(HWItemOverviewTO hwItemOverviewTO) {
        HWItemTO hwItem = null;
        if (hwItemOverviewTO != null) hwItem = hwService.getHWItem(hwItemOverviewTO.getId());
        new HWItemDialog(hwItem == null ? null : hwService.getHWItem(hwItem.getId()), to -> {
            to.setId(hwService.saveHWItem(to));
            populate();
            HWItemOverviewTO filterTO = new HWItemOverviewTO();
            filterTO.setId(to.getId());
            itemsGrid.getGrid().select(filterTO);
            if (hwItemOverviewTO == null) onDetail(to.getId());
        }).open();
    }

    private void replaceHistory(Long id) {
        Map<String, String> params = HWItemsGrid.processFilterToQuery(itemsGrid.getFilterTO());
        params.put("id", id.toString());
        String listURL = RouteConfiguration.forSessionScope().getUrl(HWItemsPage.class);
        QueryParameters queryParams = QueryParameters.simple(params);
        UI.getCurrent().getPage().getHistory().replaceState(null, listURL + "?" + queryParams.getQueryString());
    }

    public void onDetail(Long id) {
        replaceHistory(id);
        UI.getCurrent().navigate(HWItemPage.class, id);
    }

    private void onCopy(Long id) {
        Long newId = hwService.copyHWItem(id);
        populate();
        HWItemOverviewTO newTO = new HWItemOverviewTO();
        newTO.setId(newId);
        itemsGrid.getGrid().select(newTO);
    }

    public void populate() {
        itemsGrid.populate();
    }

    public void select(Long idParameter) {
        itemsGrid.selectAndScroll(idParameter);
    }

}