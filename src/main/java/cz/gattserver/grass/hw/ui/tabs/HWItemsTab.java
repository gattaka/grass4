package cz.gattserver.grass.hw.ui.tabs;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.QueryParameters;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.hw.interfaces.HWFilterTO;
import cz.gattserver.grass.hw.ui.HWUIUtils;
import cz.gattserver.grass.hw.ui.pages.HWItemPage;
import cz.gattserver.grass.hw.ui.pages.HWPage;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;

import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.HWItemsGrid;
import cz.gattserver.grass.hw.ui.dialogs.HWItemEditDialog;

import java.util.Map;

public class HWItemsTab extends Div {

    private static final long serialVersionUID = -5013459007975657195L;

    @Autowired
    private HWService hwService;

    @Autowired
    private SecurityService securityFacade;

    private HWItemsGrid itemsGrid;
    private HWPage hwPage;

    public HWItemsTab(HWPage hwPage) {
        SpringContextHelper.inject(this);
        this.hwPage = hwPage;

        itemsGrid = new HWItemsGrid(to -> navigateToDetail(to.getId()));
        itemsGrid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);

        add(itemsGrid);

        ComponentFactory componentFactory = new ComponentFactory();

        Div buttonLayout = componentFactory.createButtonLayout();
        add(buttonLayout);

        if (securityFacade.getCurrentUser().isAdmin()) {

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

        if (securityFacade.getCurrentUser().isAdmin()) {
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
        new HWItemEditDialog(hwItem == null ? null : hwService.getHWItem(hwItem.getId()), to -> {
            populate();
            HWItemOverviewTO filterTO = new HWItemOverviewTO();
            filterTO.setId(to.getId());
            itemsGrid.getGrid().select(filterTO);
            if (hwItemOverviewTO == null) navigateToDetail(to.getId());
        }).open();
    }

    private void copyItemWindow(Long id) {
        Long newId = hwService.copyHWItem(id);
        populate();
        HWItemOverviewTO newTO = new HWItemOverviewTO();
        newTO.setId(newId);
        itemsGrid.getGrid().select(newTO);
        navigateToDetail(newId);
    }

    public void navigateToDetail(Long id) {
        Map<String, String> filterQuery = HWUIUtils.processFilterToQuery(itemsGrid.getFilterTO());
        UI.getCurrent().navigate(HWItemPage.class, id, QueryParameters.simple(filterQuery));
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
}