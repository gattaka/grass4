package cz.gattserver.grass.recipes.web;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.grass.core.ui.components.SaveCloseLayout;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import cz.gattserver.grass.recipes.facades.RecipesService;
import cz.gattserver.grass.recipes.model.dto.RecipeDTO;

public abstract class RecipeDialog extends EditWebDialog {

	private static final long serialVersionUID = 6803519662032576371L;

	@Autowired
	private RecipesService recipesFacade;

	public RecipeDialog() {
		this(null);
	}

	protected abstract void onSave(String name, String desc, Long id);

	public RecipeDialog(final RecipeDTO to) {
		SpringContextHelper.inject(this);
		setWidth("600px");

		final TextField name = new TextField("Název");
		name.setWidthFull();
		if (to != null)
			name.setValue(to.getName());
		add(name);

		final TextArea desc = new TextArea("Popis");
		desc.setWidthFull();
		desc.setHeight("500px");
		if (to != null)
			desc.setValue(recipesFacade.breaklineToEol(to.getDescription()));
		add(desc);

		add(new SaveCloseLayout(event -> {
			onSave(name.getValue(), desc.getValue(), to == null ? null : to.getId());
			close();
		}, e -> close()));
	}

}
