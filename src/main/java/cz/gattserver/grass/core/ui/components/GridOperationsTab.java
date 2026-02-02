package cz.gattserver.grass.core.ui.components;

import java.io.Serializable;
import java.util.Collection;
import java.util.function.Consumer;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;

import cz.gattserver.common.Identifiable;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.core.ui.util.UIUtils;

/**
 * Template všech tabů, kde je v tabulce zobrazován přehled entit a dole jsou
 * tlačítka operací Vytvořit/Detail/Upravit/Smazat
 *
 * @param <T> třída zobrazované entity
 * @author Hynek
 *
 */
public class GridOperationsTab<T extends Identifiable> extends Div {

    private static final long serialVersionUID = 6844434642906509277L;

    private Grid<T> grid;

    // TODO builder?
    public GridOperationsTab(Class<T> clazz, Consumer<T> onDetail, Runnable onCreate, Consumer<T> onEdit,
                             Consumer<T> onDelete, Consumer<Grid<T>> onPopulate, Consumer<Grid<T>> gridCustomizer) {
        SpringContextHelper.inject(this);

        grid = new Grid<>(clazz);
        grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        UIUtils.applyGrassDefaultStyle(grid);
        gridCustomizer.accept(grid);
        onPopulate.accept(grid);
        add(grid);

        grid.addItemDoubleClickListener(e -> onDetail.accept(e.getItem()));

        ComponentFactory componentFactory = new ComponentFactory();

        Div buttonLayout = componentFactory.createButtonLayout();
        add(buttonLayout);

        Button createBtn = componentFactory.createCreateButton(e -> onCreate.run());
        Button detailBtn = componentFactory.createDetailGridButton(item -> onDetail.accept(item), grid);
        Button modifyBtn = componentFactory.createEditGridButton(item -> onEdit.accept(item), grid);
        Button deleteBtn = componentFactory.createDeleteGridSetButton(items -> {
            items.forEach(onDelete);
            onPopulate.accept(grid);
        }, grid);

        placeButtons(buttonLayout, createBtn, detailBtn, modifyBtn, deleteBtn);
    }

    private void placeButtons(Div buttonLayout, Button createBtn, Button detailBtn, Button modifyBtn,
                              Button deleteBtn) {
        buttonLayout.add(createBtn);
        buttonLayout.add(detailBtn);
        buttonLayout.add(modifyBtn);
        buttonLayout.add(deleteBtn);
    }
}