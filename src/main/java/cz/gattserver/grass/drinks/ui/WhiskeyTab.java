package cz.gattserver.grass.drinks.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.TextRenderer;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.drinks.model.domain.WhiskeyType;
import cz.gattserver.grass.drinks.model.interfaces.WhiskeyOverviewTO;
import cz.gattserver.grass.drinks.model.interfaces.WhiskeyTO;
import cz.gattserver.grass.core.ui.util.UIUtils;

public class WhiskeyTab extends DrinksTab<WhiskeyTO, WhiskeyOverviewTO> {

    private static final long serialVersionUID = 594189301140808163L;

    @Override
    protected WhiskeyOverviewTO createNewOverviewTO() {
        return new WhiskeyOverviewTO();
    }

    @Override
    protected void configureGrid(Grid<WhiskeyOverviewTO> grid, final WhiskeyOverviewTO filterTO) {
        addNameColumn(grid);
        addCountryColumn(grid);
        addAlcoholColumn(grid);

        Column<WhiskeyOverviewTO> yearsColumn =
                grid.addColumn(WhiskeyOverviewTO::getYears).setHeader("Stáří (roky)").setWidth("90px").setFlexGrow(0)
                        .setSortProperty("years");
        Column<WhiskeyOverviewTO> whiskeyTypeColumn =
                grid.addColumn(new TextRenderer<>(to -> to.getWhiskeyType().getCaption())).setHeader("Typ whiskey")
                        .setWidth("150px").setFlexGrow(0).setSortProperty("whiskeyType");

        addRatingStarsColumn(grid);

        grid.setWidthFull();
        grid.setHeight("400px");
        add(grid);

        // Stáří (roky)
        UIUtils.addHeaderTextField(getHeaderRow().getCell(yearsColumn), e -> {
            filterTO.setYears(Integer.parseInt(e.getValue()));
            populate();
        });

        // Typ Whiskeyu
        UIUtils.addHeaderComboBox(getHeaderRow().getCell(whiskeyTypeColumn), WhiskeyType.class, WhiskeyType::getCaption,
                e -> {
                    filterTO.setWhiskeyType(e.getValue());
                    populate();
                });
    }

    @Override
    protected void populate() {
        FetchCallback<WhiskeyOverviewTO, WhiskeyOverviewTO> fetchCallback =
                q -> getDrinksFacade().getWhiskeys(filterTO, q.getOffset(), q.getLimit(), q.getSortOrders()).stream();
        CountCallback<WhiskeyOverviewTO, WhiskeyOverviewTO> countCallback =
                q -> getDrinksFacade().countWhiskeys(filterTO);
        grid.setDataProvider(DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
    }

    @Override
    protected void populateBtnLayout(Div btnLayout) {
        ComponentFactory componentFactory = new ComponentFactory();
        btnLayout.add(componentFactory.createCreateButton(event -> new WhiskeyDialog() {
            private static final long serialVersionUID = -4863260002363608014L;

            @Override
            protected void onSave(WhiskeyTO to) {
                to = getDrinksFacade().saveWhiskey(to);
                showDetail(to);
                populate();
            }
        }.open()));

        btnLayout.add(componentFactory.createEditGridButton(event -> new WhiskeyDialog(choosenDrink) {
            private static final long serialVersionUID = 5264621441522056786L;

            @Override
            protected void onSave(WhiskeyTO to) {
                to = getDrinksFacade().saveWhiskey(to);
                showDetail(to);
                populate();
            }
        }.open(), grid));

        btnLayout.add(componentFactory.createDeleteGridSetButton(items -> {
            for (WhiskeyOverviewTO s : items)
                getDrinksFacade().deleteDrink(s.getId());
            populate();
            showDetail(null);
        }, grid));
    }

    @Override
    protected String getItemHeader() {
        return choosenDrink.getName() + " (" + choosenDrink.getCountry() + ")";
    }

    @Override
    protected String[] getPropertiesHeaders() {
        return new String[]{"Stáří (roky)", "Alkohol (%)", "Typ whiskey"};
    }

    @Override
    protected String[] getProperties() {
        return new String[]{choosenDrink.getYears() == null ? "" : String.valueOf(choosenDrink.getYears()),
                String.valueOf(choosenDrink.getAlcohol()), choosenDrink.getWhiskeyType().getCaption()};
    }

    @Override
    protected String getURLPath() {
        return "whiskey";
    }

    @Override
    protected WhiskeyTO findById(Long id) {
        return getDrinksFacade().getWhiskeyById(id);
    }

}
