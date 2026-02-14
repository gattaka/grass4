package cz.gattserver.grass.recipes.web;

import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.HtmlDiv;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.pages.MainView;
import cz.gattserver.grass.core.ui.util.UIUtils;

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

@PageTitle("Recepty")
@Route(value = "recipes", layout = MainView.class)
public class RecipesPage extends Div {

    private RecipesService recipesService;

    private Grid<RecipeOverviewTO> grid;
    private H2 nameLabel;
    private HtmlDiv contentLabel;
    private RecipeDTO choosenRecipe;
    private RecipeOverviewTO filterTO;

    public RecipesPage(RecipesService recipesService, SecurityService securityService) {
        this.recipesService = recipesService;

        removeAll();
        ComponentFactory componentFactory = new ComponentFactory();

        Div layout = componentFactory.createOneColumnLayout();
        add(layout);

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

        grid.addSelectionListener(
                (e) -> e.getFirstSelectedItem().ifPresent((v) -> showDetail(recipesService.getRecipeById(v.getId()))));

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

        Div btnLayout = componentFactory.createButtonLayout();
        layout.add(btnLayout);

        btnLayout.setVisible(securityService.getCurrentUser().getRoles().contains(CoreRole.ADMIN));

        btnLayout.add(componentFactory.createCreateButton(event -> {
            new RecipeDialog() {

                @Override
                protected void onSave(String name, String desc, Long id) {
                    id = recipesService.saveRecipe(name, desc);
                    RecipeDTO to = new RecipeDTO(id, name, desc);
                    showDetail(to);
                    populate();
                }
            }.open();
        }));

        btnLayout.add(componentFactory.createEditGridButton(event -> {
            new RecipeDialog(choosenRecipe) {

                @Override
                protected void onSave(String name, String desc, Long id) {
                    recipesService.saveRecipe(name, desc, id);
                    RecipeDTO to = new RecipeDTO(id, name, desc);
                    showDetail(to);
                    populate();
                }
            }.open();
        }, grid));

        btnLayout.add(componentFactory.createDeleteGridSetButton(items -> {
            for (RecipeOverviewTO item : items)
                recipesService.deleteRecipe(item.getId());
            populate();
        }, grid));
    }


    private void showDetail(RecipeDTO choosenRecipe) {
        nameLabel.setVisible(true);
        nameLabel.setText(choosenRecipe.getName());
        String value = recipesService.eolToBreakline(choosenRecipe.getDescription());
        contentLabel.setValue(value);
        this.choosenRecipe = choosenRecipe;
    }

    private void populate() {
        FetchCallback<RecipeOverviewTO, Void> fetchCallback =
                q -> recipesService.getRecipes(filterTO.getName(), q.getOffset(), q.getLimit()).stream();
        CountCallback<RecipeOverviewTO, Void> countCallback = q -> recipesService.getRecipesCount(filterTO.getName());
        grid.setDataProvider(DataProvider.fromCallbacks(fetchCallback, countCallback));
    }
}
