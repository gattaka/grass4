package cz.gattserver.grass.drinks.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.drinks.model.interfaces.OtherOverviewTO;
import cz.gattserver.grass.drinks.model.interfaces.OtherTO;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;

public class OtherTab extends DrinksTab<OtherTO, OtherOverviewTO> {

    private static final long serialVersionUID = -8540314953045422691L;

    @Override
    protected OtherOverviewTO createNewOverviewTO() {
        return new OtherOverviewTO();
    }

    @Override
    protected void configureGrid(Grid<OtherOverviewTO> grid, final OtherOverviewTO filterTO) {
        Column<OtherOverviewTO> ingredientColumn =
                grid.addColumn(OtherOverviewTO::getIngredient).setHeader("Ingredience").setSortProperty("winery");

        addNameColumn(grid);
        addCountryColumn(grid);
        addAlcoholColumn(grid);

        addRatingStarsColumn(grid);

        grid.setWidthFull();
        grid.setHeight("400px");

        add(grid);

        // Vinařství
        UIUtils.addHeaderTextField(getHeaderRow().getCell(ingredientColumn), e -> {
            filterTO.setIngredient(e.getValue());
            populate();
        });
    }

    @Override
    protected void populate() {
        FetchCallback<OtherOverviewTO, OtherOverviewTO> fetchCallback =
                q -> getDrinksFacade().getOthers(filterTO, q.getOffset(), q.getLimit(), q.getSortOrders()).stream();
        CountCallback<OtherOverviewTO, OtherOverviewTO> countCallback = q -> getDrinksFacade().countOthers(filterTO);
        grid.setDataProvider(DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
    }

    @Override
    protected void populateBtnLayout(ButtonLayout btnLayout) {
        ComponentFactory componentFactory = new ComponentFactory();
        btnLayout.add(componentFactory.createCreateButton(event -> new OtherDialog() {
            private static final long serialVersionUID = -4863260002363608014L;

            @Override
            protected void onSave(OtherTO to) {
                to = getDrinksFacade().saveOther(to);
                showDetail(to);
                populate();
            }
        }.open()));

        btnLayout.add(componentFactory.createEditGridButton(event -> new OtherDialog(choosenDrink) {
            private static final long serialVersionUID = 5264621441522056786L;

            @Override
            protected void onSave(OtherTO to) {
                to = getDrinksFacade().saveOther(to);
                showDetail(to);
                populate();
            }
        }.open(), grid));

        btnLayout.add(componentFactory.createDeleteGridSetButton(items -> {
            for (OtherOverviewTO s : items)
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
        return new String[]{"Ingredience", "Alkohol (%)"};
    }

    @Override
    protected String[] getProperties() {
        return new String[]{choosenDrink.getIngredient(),
                choosenDrink.getAlcohol() == null ? "" : String.valueOf(choosenDrink.getAlcohol())};
    }

    @Override
    protected String getURLPath() {
        return "other";
    }

    @Override
    protected OtherTO findById(Long id) {
        return getDrinksFacade().getOtherById(id);
    }

}
