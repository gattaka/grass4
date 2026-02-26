package cz.gattserver.grass.recipes.ui;

import com.vaadin.flow.component.Unit;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;

import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import cz.gattserver.grass.recipes.service.RecipesService;
import cz.gattserver.grass.recipes.interfaces.RecipeTO;

import java.io.Serial;

public abstract class RecipeDialog extends EditWebDialog {

    @Serial
    private static final long serialVersionUID = 5037484251046991162L;

    public RecipeDialog() {
        this(null);
    }

    protected abstract void onSave(String name, String desc, Long id);

    public RecipeDialog(final RecipeTO to) {
        super("Recept");
        RecipesService recipesService = SpringContextHelper.getBean(RecipesService.class);
        setWidth(600, Unit.PIXELS);

        final TextField name = new TextField("Název");
        name.setWidthFull();
        if (to != null) name.setValue(to.getName());
        add(name);

        final TextArea desc = new TextArea("Popis");
        desc.setWidthFull();
        desc.setHeight(500, Unit.PIXELS);
        if (to != null) desc.setValue(recipesService.breaklineToEol(to.getDescription()));
        add(desc);

        add(componentFactory.createDialogSubmitOrStornoLayout(event -> {
            onSave(name.getValue(), desc.getValue(), to == null ? null : to.getId());
            close();
        }, e -> close()));
    }
}