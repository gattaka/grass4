package cz.gattserver.grass.core.ui.components;

import java.io.Serializable;
import java.util.Collection;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;

import cz.gattserver.common.Identifiable;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;

/**
 * Template všech tabů, kde je v tabulce zobrazován přehled entit a dole jsou
 * tlačítka operací Vytvořit/Detail/Upravit/Smazat
 *
 * @param <T> třída zobrazované entity
 * @author Hynek
 *
 */
public abstract class GridOperationsTab<T extends Identifiable, F, C extends Collection<T> & Serializable> extends Div {

    private static final long serialVersionUID = 6844434642906509277L;

    protected Grid<T> grid;
    protected C data;
    protected F filterTO;

    /**
     * Vytvoří okno pro založení entity
     */
    protected abstract Dialog createCreateDialog();

    /**
     * Vytvoří okno pro detail entity
     */
    protected abstract Dialog createDetailDialog(Long id);

    /**
     * Vytvoří okno pro úpravu entity
     */
    protected abstract Dialog createModifyDialog(T dto);

    /**
     * Smaže vybranou entitu
     */
    protected abstract void deleteEntity(T dto);

    /**
     * Upraví tabulku (jmenuje sloupce apod.) - voláno pouze pokud je použit
     * defaultní Grid - viz metoda createGrid
     */
    protected void customizeGrid(Grid<T> grid) {
    }

    protected abstract C getItems(F filterTO);

    protected void populateGrid() {
        data = getItems(filterTO);
        grid.setItems(data);
    }

    public GridOperationsTab(Class<T> clazz) {
        SpringContextHelper.inject(this);

        init();

        grid = new Grid<>(clazz);
        grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        UIUtils.applyGrassDefaultStyle(grid);
        populateGrid();
        customizeGrid(grid);
        add(grid);

        grid.addItemClickListener(e -> {
            if (e.getClickCount() > 1) createDetailDialog(e.getItem().getId()).open();
        });

        ButtonLayout buttonLayout = new ButtonLayout();
        add(buttonLayout);

        ComponentFactory componentFactory = new ComponentFactory();
        Button createBtn = componentFactory.createCreateButton(e -> createCreateDialog().open());
        Button detailBtn =
                componentFactory.createDetailGridButton(item -> createDetailDialog(item.getId()).open(), grid);
        Button modifyBtn = componentFactory.createEditGridButton(item -> createModifyDialog(item).open(), grid);
        Button deleteBtn = componentFactory.createDeleteGridSetButton(items -> {
            items.forEach(this::deleteEntity);
            data.removeAll(items);
            grid.getDataProvider().refreshAll();
        }, grid);

        placeButtons(buttonLayout, createBtn, detailBtn, modifyBtn, deleteBtn);
    }

    protected void placeButtons(ButtonLayout buttonLayout, Button createBtn, Button detailBtn, Button modifyBtn,
                                Button deleteBtn) {
        buttonLayout.add(createBtn);
        buttonLayout.add(detailBtn);
        buttonLayout.add(modifyBtn);
        buttonLayout.add(deleteBtn);
    }

    protected void init() {
    }

}