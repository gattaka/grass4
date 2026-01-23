package cz.gattserver.grass.drinks.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import cz.gattserver.grass.drinks.model.interfaces.BeerOverviewTO;
import cz.gattserver.grass.drinks.model.interfaces.BeerTO;
import cz.gattserver.grass.core.ui.components.button.ModifyGridButton;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;

import java.text.NumberFormat;
import java.util.Locale;

public class BeersTab extends DrinksTab<BeerTO, BeerOverviewTO> {

	private static final long serialVersionUID = 594189301140808163L;

	@Override
	protected BeerOverviewTO createNewOverviewTO() {
		return new BeerOverviewTO();
	}

	@Override
	protected void configureGrid(Grid<BeerOverviewTO> grid, final BeerOverviewTO filterTO) {
		Column<BeerOverviewTO> breweryColumn = grid.addColumn(BeerOverviewTO::getBrewery).setHeader("Pivovar")
				.setSortProperty("brewery");

		addNameColumn(grid);

		Column<BeerOverviewTO> categoryColumn = grid.addColumn(BeerOverviewTO::getCategory).setHeader("Kategorie")
				.setWidth("100px").setFlexGrow(0).setSortProperty("category");
		Column<BeerOverviewTO> degreesColumn = grid
				.addColumn(new NumberRenderer<BeerOverviewTO>(BeerOverviewTO::getDegrees,
						NumberFormat.getNumberInstance(new Locale("cs", "CZ"))))
				.setHeader("Stupně (°)").setWidth("100px").setFlexGrow(0).setSortProperty("degrees");

		addAlcoholColumn(grid);

		Column<BeerOverviewTO> ibuColumn = grid.addColumn(BeerOverviewTO::getIbu).setHeader("IBU").setWidth("50px")
				.setFlexGrow(0).setSortProperty("ibu");
		Column<BeerOverviewTO> maltsColumn = grid.addColumn(new TextRenderer<BeerOverviewTO>(to -> to.getMalts()))
				.setHeader("Slad").setWidth("60px").setFlexGrow(0).setSortProperty("malts");
		Column<BeerOverviewTO> hopsColumn = grid.addColumn(new TextRenderer<BeerOverviewTO>(to -> to.getHops()))
				.setHeader("Chmel").setWidth("80px").setFlexGrow(0).setSortProperty("hops");

		addRatingStarsColumn(grid);

		grid.setWidthFull();
		grid.setHeight("400px");
		add(grid);

		// Pivovar
		UIUtils.addHeaderTextField(getHeaderRow().getCell(breweryColumn), e -> {
			filterTO.setBrewery(e.getValue());
			populate();
		});

		// Kategorie
		UIUtils.addHeaderTextField(getHeaderRow().getCell(categoryColumn), e -> {
			filterTO.setCategory(e.getValue());
			populate();
		});

		// Stupně
		UIUtils.addHeaderTextField(getHeaderRow().getCell(degreesColumn), e -> {
			filterTO.setDegrees(Double.parseDouble(e.getValue()));
			populate();
		});

		// Hořkost
		UIUtils.addHeaderTextField(getHeaderRow().getCell(ibuColumn), e -> {
			filterTO.setIbu(Integer.parseInt(e.getValue()));
			populate();
		});

		// Sladu
		UIUtils.addHeaderTextField(getHeaderRow().getCell(maltsColumn), e -> {
			filterTO.setMalts(e.getValue());
			populate();
		});

		// Chmele
		UIUtils.addHeaderTextField(getHeaderRow().getCell(hopsColumn), e -> {
			filterTO.setHops(e.getValue());
			populate();
		});
	}

	@Override
	protected void populate() {
		FetchCallback<BeerOverviewTO, BeerOverviewTO> fetchCallback = q -> getDrinksFacade()
				.getBeers(filterTO, q.getOffset(), q.getLimit(), q.getSortOrders()).stream();
		CountCallback<BeerOverviewTO, BeerOverviewTO> countCallback = q -> getDrinksFacade().countBeers(filterTO);
		grid.setDataProvider(DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
	}

	@Override
	protected void populateBtnLayout(ButtonLayout btnLayout) {
		btnLayout.add(new CreateGridButton("Přidat", event -> new BeerDialog() {
			private static final long serialVersionUID = -4863260002363608014L;

			@Override
			protected void onSave(BeerTO to) {
				to = getDrinksFacade().saveBeer(to);
				showDetail(to);
				populate();
			}
		}.open()));

		btnLayout.add(new ModifyGridButton<BeerOverviewTO>("Upravit", event -> new BeerDialog(choosenDrink) {
			private static final long serialVersionUID = 5264621441522056786L;

			@Override
			protected void onSave(BeerTO to) {
				to = getDrinksFacade().saveBeer(to);
				showDetail(to);
				populate();
			}
		}.open(), grid));

		btnLayout.add(new DeleteGridButton<BeerOverviewTO>("Smazat", items -> {
			for (BeerOverviewTO s : items)
				getDrinksFacade().deleteDrink(s.getId());
			populate();
			showDetail(null);
		}, grid));
	}

	@Override
	protected String getItemHeader() {
		return choosenDrink.getBrewery() + " " + choosenDrink.getName() + " (" + choosenDrink.getCountry() + ")";
	}

	@Override
	protected String[] getPropertiesHeaders() {
		return new String[] { "Kategorie", "Stupně (°)", "Alkohol (%)", "Hořkost (IBU)", "Typ sladu", "Slady",
				"Chmely" };
	}

	@Override
	protected String[] getProperties() {
		return new String[] { choosenDrink.getCategory(), String.valueOf(choosenDrink.getDegrees()),
				String.valueOf(choosenDrink.getAlcohol()),
				choosenDrink.getIbu() == null ? "" : String.valueOf(choosenDrink.getIbu()),
				choosenDrink.getMaltType().getCaption(), choosenDrink.getMalts(), choosenDrink.getHops() };
	}

	@Override
	protected String getURLPath() {
		return "beer";
	}

	@Override
	protected BeerTO findById(Long id) {
		return getDrinksFacade().getBeerById(id);
	}

}
