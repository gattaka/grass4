package cz.gattserver.grass.recipes.web;

import cz.gattserver.common.vaadin.HtmlDiv;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.components.button.ModifyGridButton;
import cz.gattserver.grass.core.ui.pages.template.OneColumnPage;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import cz.gattserver.grass.recipes.facades.RecipesService;
import cz.gattserver.grass.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass.recipes.model.dto.RecipeOverviewTO;

@Route("recipes")
@PageTitle("Recepty")
public class RecipesPage extends OneColumnPage {

	private static final long serialVersionUID = 1214280599196303350L;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private RecipesService recipesService;

	private Grid<RecipeOverviewTO> grid;
	private H2 nameLabel;
	private HtmlDiv contentLabel;
	private RecipeDTO choosenRecipe;
	private RecipeOverviewTO filterTO;

	public RecipesPage() {
		init();
		loadCSS(getContextPath() + "/VAADIN/recipes/style.css");
	}

	private void showDetail(RecipeDTO choosenRecipe) {
		nameLabel.setVisible(true);
		nameLabel.setText(choosenRecipe.getName());
		String value = recipesService.eolToBreakline(choosenRecipe.getDescription());
		contentLabel.setValue(value);
		this.choosenRecipe = choosenRecipe;
	}

	@Override
	protected void createColumnContent(Div layout) {
		Div recipesLayout = new Div();
		recipesLayout.setId("recipes-div");
		layout.add(recipesLayout);

		filterTO = new RecipeOverviewTO();

		grid = new Grid<>();
		UIUtils.applyGrassDefaultStyle(grid);
		grid.setHeightFull();
		Div gridDiv = new Div(grid);
		gridDiv.setId("recipes-grid-div");
		recipesLayout.add(gridDiv);

		Column<RecipeOverviewTO> nazevColumn = grid.addColumn(RecipeOverviewTO::getName).setHeader("Název");

		grid.addSelectionListener((e) -> e.getFirstSelectedItem()
				.ifPresent((v) -> showDetail(recipesService.getRecipeById(v.getId()))));

		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Název
		UIUtils.addHeaderTextField(filteringHeader.getCell(nazevColumn), e -> {
			filterTO.setName(e.getValue());
			populate();
		});

		populate();

		Div contentLayout = new Div();
		contentLayout.setId("recipes-content-div");
		recipesLayout.add(contentLayout);

		nameLabel = new H2();
		contentLayout.add(nameLabel);
		nameLabel.setVisible(false);

		contentLabel = new HtmlDiv();
		contentLabel.setWidthFull();
		contentLayout.add(contentLabel);

		ButtonLayout btnLayout = new ButtonLayout();
		layout.add(btnLayout);

		btnLayout.setVisible(securityService.getCurrentUser().getRoles().contains(CoreRole.ADMIN));

		btnLayout.add(new CreateGridButton("Přidat", event -> {
			new RecipeDialog() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(String name, String desc, Long id) {
					id = recipesService.saveRecipe(name, desc);
					RecipeDTO to = new RecipeDTO(id, name, desc);
					showDetail(to);
					populate();
				}
			}.open();
		}));

		btnLayout.add(new ModifyGridButton<>("Upravit", event -> {
			new RecipeDialog(choosenRecipe) {
				private static final long serialVersionUID = 5264621441522056786L;

				@Override
				protected void onSave(String name, String desc, Long id) {
					recipesService.saveRecipe(name, desc, id);
					RecipeDTO to = new RecipeDTO(id, name, desc);
					showDetail(to);
					populate();
				}
			}.open();
		}, grid));

		btnLayout.add(new DeleteGridButton<>("Odstranit", event -> {
			for (RecipeOverviewTO e : event)
				recipesService.deleteRecipe(e.getId());
			populate();
		}, grid));
	}

	private void populate() {
		FetchCallback<RecipeOverviewTO, Void> fetchCallback = q -> recipesService
				.getRecipes(filterTO.getName(), q.getOffset(), q.getLimit()).stream();
		CountCallback<RecipeOverviewTO, Void> countCallback = q -> recipesService
				.getRecipesCount(filterTO.getName());
		grid.setDataProvider(DataProvider.fromCallbacks(fetchCallback, countCallback));
	}
}
