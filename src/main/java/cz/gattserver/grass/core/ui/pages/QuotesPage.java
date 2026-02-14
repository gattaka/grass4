package cz.gattserver.grass.core.ui.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.core.interfaces.QuoteTO;
import cz.gattserver.grass.core.services.CoreACLService;
import cz.gattserver.grass.core.services.QuotesService;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.dialogs.QuoteDialog;
import cz.gattserver.grass.core.ui.util.UIUtils;

@PageTitle("Hlášky")
@Route(value = "quotes", layout = MainView.class)
public class QuotesPage extends Div {

    public QuotesPage(QuotesService quotesService, CoreACLService coreACLService, SecurityService securityService) {
        removeAll();
        ComponentFactory componentFactory = new ComponentFactory();

        Div layout = componentFactory.createOneColumnLayout();
        add(layout);

        layout.add(new H2("Vyhledávání"));

        TextField searchField = new TextField();
        searchField.setPlaceholder("Obsah hlášky");
        searchField.setWidthFull();
        layout.add(searchField);

        layout.add(new H2("Seznam hlášek"));

        Grid<QuoteTO> grid = new Grid<>();
        UIUtils.applyGrassDefaultStyle(grid);
        layout.add(grid);

        List<QuoteTO> data = new ArrayList<>();

        Consumer<String> populateCallback = filter -> {
            data.clear();
            data.addAll(quotesService.getQuotes(filter));
            grid.setItems(data);
        };

        searchField.addValueChangeListener(e -> populateCallback.accept(e.getValue()));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);

        populateCallback.accept(null);

        grid.addColumn(QuoteTO::getId).setHeader("Id").setFlexGrow(0).setWidth("50px");
        grid.addColumn(QuoteTO::getName).setHeader("Obsah");

        Div btnLayout = componentFactory.createButtonLayout();
        layout.add(btnLayout);
        btnLayout.setVisible(coreACLService.canModifyQuotes(securityService.getCurrentUser()));

        btnLayout.add(componentFactory.createCreateButton(e -> new QuoteDialog(q -> {
            quotesService.createQuote(q.getName());
            populateCallback.accept(searchField.getValue());
        }).open()));

        btnLayout.add(componentFactory.createEditGridButton(originQuote -> new QuoteDialog(originQuote, q -> {
            quotesService.modifyQuote(q.getId(), q.getName());
            grid.getDataProvider().refreshItem(q);
            grid.select(q);
        }).open(), grid));

        btnLayout.add(componentFactory.createDeleteGridSetButton(items -> items.forEach(q -> {
            quotesService.deleteQuote(q.getId());
            data.remove(q);
            grid.getDataProvider().refreshAll();
        }), grid));
    }
}
