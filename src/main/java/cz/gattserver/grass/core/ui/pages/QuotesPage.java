package cz.gattserver.grass.core.ui.pages;

import java.util.List;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import cz.gattserver.grass.core.interfaces.QuoteTO;
import cz.gattserver.grass.core.ui.dialogs.QuoteDialog;
import cz.gattserver.grass.core.ui.pages.template.OneColumnPage;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;

@Route("quotes")
@PageTitle("Hlášky")
public class QuotesPage extends OneColumnPage {

    private static final long serialVersionUID = 6209768531464272839L;

    private List<QuoteTO> data;
    private Grid<QuoteTO> grid;

    public QuotesPage() {
        init();
    }

    private void populateData(String filter) {
        data = quotesFacade.getQuotes(filter);
        grid.setItems(data);
    }

    @Override
    protected void createColumnContent(Div layout) {
        layout.add(new H2("Vyhledávání"));

        TextField searchField = new TextField();
        searchField.setPlaceholder("Obsah hlášky");
        searchField.setWidthFull();
        layout.add(searchField);

        searchField.addValueChangeListener(e -> populateData(e.getValue()));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);

        layout.add(new H2("Seznam hlášek"));

        grid = new Grid<>();
        UIUtils.applyGrassDefaultStyle(grid);
        layout.add(grid);

        populateData(null);

        grid.addColumn(QuoteTO::getId).setHeader("Id").setFlexGrow(0).setWidth("50px");
        grid.addColumn(QuoteTO::getName).setHeader("Obsah");

        ButtonLayout btnLayout = new ButtonLayout();
        layout.add(btnLayout);
        btnLayout.setVisible(coreACL.canModifyQuotes(getUser()));

        btnLayout.add(componentFactory.createCreateButton(e -> new QuoteDialog(q -> {
            quotesFacade.createQuote(q.getName());
            populateData(searchField.getValue());
        }).open()));

        btnLayout.add(componentFactory.createEditGridButton(originQuote -> new QuoteDialog(originQuote, q -> {
            quotesFacade.modifyQuote(q.getId(), q.getName());
            grid.getDataProvider().refreshItem(q);
            grid.select(q);
        }).open(), grid));

        btnLayout.add(componentFactory.createDeleteGridSetButton(items -> items.forEach(q -> {
            quotesFacade.deleteQuote(q.getId());
            data.remove(q);
            grid.getDataProvider().refreshAll();
        }), grid));
    }
}
