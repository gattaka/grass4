package cz.gattserver.grass.drinks.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.TextRenderer;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.drinks.model.domain.WineType;
import cz.gattserver.grass.drinks.model.interfaces.WineOverviewTO;
import cz.gattserver.grass.drinks.model.interfaces.WineTO;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;

public class WineTab extends DrinksTab<WineTO, WineOverviewTO> {

    private static final long serialVersionUID = -8540314953045422691L;

    @Override
    protected WineOverviewTO createNewOverviewTO() {
        return new WineOverviewTO();
    }

    @Override
    protected void configureGrid(Grid<WineOverviewTO> grid, final WineOverviewTO filterTO) {
        Column<WineOverviewTO> wineryColumn =
                grid.addColumn(WineOverviewTO::getWinery).setHeader("Vinařství").setSortProperty("winery");

        addNameColumn(grid);
        addCountryColumn(grid);
        addAlcoholColumn(grid);

        Column<WineOverviewTO> yearsColumn =
                grid.addColumn(WineOverviewTO::getYear).setHeader("Rok").setWidth("90px").setFlexGrow(0)
                        .setSortProperty("year");

        // Rok
        UIUtils.addHeaderTextField(getHeaderRow().getCell(yearsColumn), e -> {
            filterTO.setYear(Integer.parseInt(e.getValue()));
            populate();
        });

        Column<WineOverviewTO> wineTypeColumn =
                grid.addColumn(new TextRenderer<>(to -> to.getWineType().getCaption())).setHeader("Typ vína")
                        .setWidth("100px").setFlexGrow(0).setSortProperty("wineType");

        addRatingStarsColumn(grid);

        grid.setWidthFull();
        grid.setHeight("400px");

        add(grid);

        // Vinařství
        UIUtils.addHeaderTextField(getHeaderRow().getCell(wineryColumn), e -> {
            filterTO.setWinery(e.getValue());
            populate();
        });

        // Typ vína
        UIUtils.addHeaderComboBox(getHeaderRow().getCell(wineTypeColumn), WineType.class, WineType::getCaption, e -> {
            filterTO.setWineType(e.getValue());
            populate();
        });
    }

    @Override
    protected void populate() {
        FetchCallback<WineOverviewTO, WineOverviewTO> fetchCallback =
                q -> getDrinksFacade().getWines(filterTO, q.getOffset(), q.getLimit(), q.getSortOrders()).stream();
        CountCallback<WineOverviewTO, WineOverviewTO> countCallback = q -> getDrinksFacade().countWines(filterTO);
        grid.setDataProvider(DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
    }

    @Override
    protected void populateBtnLayout(ButtonLayout btnLayout) {
        ComponentFactory componentFactory = new ComponentFactory();
        btnLayout.add(componentFactory.createCreateButton(event -> new WineDialog() {
            private static final long serialVersionUID = -4863260002363608014L;

            @Override
            protected void onSave(WineTO to) {
                to = getDrinksFacade().saveWine(to);
                showDetail(to);
                populate();
            }
        }.open()));

        btnLayout.add(componentFactory.createEditGridButton(event -> new WineDialog(choosenDrink) {
            private static final long serialVersionUID = 5264621441522056786L;

            @Override
            protected void onSave(WineTO to) {
                to = getDrinksFacade().saveWine(to);
                showDetail(to);
                populate();
            }
        }.open(), grid));

        btnLayout.add(componentFactory.createDeleteGridSetButton(items -> {
            for (WineOverviewTO s : items)
                getDrinksFacade().deleteDrink(s.getId());
            populate();
            showDetail(null);
        }, grid));
    }

    @Override
    protected String getItemHeader() {
        return choosenDrink.getWinery() + " " + choosenDrink.getName() + " (" + choosenDrink.getCountry() + ")";
    }

    @Override
    protected String[] getPropertiesHeaders() {
        return new String[]{"Rok", "Alkohol (%)", "Typ vína"};
    }

    @Override
    protected String[] getProperties() {
        return new String[]{choosenDrink.getYear() == null ? "" : String.valueOf(choosenDrink.getYear()),
                choosenDrink.getAlcohol() == null ? "" : String.valueOf(choosenDrink.getAlcohol()),
                choosenDrink.getWineType().getCaption()};
    }

    @Override
    protected String getURLPath() {
        return "wine";
    }

    @Override
    protected WineTO findById(Long id) {
        return getDrinksFacade().getWineById(id);
    }

}
