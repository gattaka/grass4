package cz.gattserver.grass.ui.components;

import java.io.Serializable;
import java.util.Collection;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;

import cz.gattserver.web.common.Identifiable;
import cz.gattserver.grass.ui.components.button.CreateGridButton;
import cz.gattserver.grass.ui.components.button.DeleteGridButton;
import cz.gattserver.grass.ui.components.button.DetailGridButton;
import cz.gattserver.grass.ui.components.button.ModifyGridButton;
import cz.gattserver.grass.ui.util.ButtonLayout;
import cz.gattserver.grass.ui.util.UIUtils;

/**
 * Template všech tabů, kde je v tabulce zobrazován přehled entit a dole jsou
 * tlačítka operací Vytvořit/Detail/Upravit/Smazat
 * 
 * @author Hynek
 * @param <T>
 *            třída zobrazované entity
 * 
 */
public abstract class GridOperationsTab<T extends Identifiable, C extends Collection<T> & Serializable> extends Div {

	private static final long serialVersionUID = 6844434642906509277L;

	protected Grid<T> grid;
	protected C data;

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

	protected abstract C getItems();

	public GridOperationsTab(Class<T> clazz) {
		init();

		grid = new Grid<>(clazz);
		grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		UIUtils.applyGrassDefaultStyle(grid);
		grid.setItems(getItems());
		customizeGrid(grid);
		add(grid);

		grid.addItemClickListener(e -> {
			if (e.getClickCount() > 1)
				createDetailDialog(e.getItem().getId()).open();
		});

		ButtonLayout buttonLayout = new ButtonLayout();
		add(buttonLayout);

		final Button createBtn = new CreateGridButton("Založit", e -> createCreateDialog().open());
		final Button detailBtn = new DetailGridButton<>("Detail", item -> createDetailDialog(item.getId()).open(),
				grid);
		final Button modifyBtn = new ModifyGridButton<>("Upravit", item -> createModifyDialog(item).open(), grid);
		final Button deleteBtn = new DeleteGridButton<>("Smazat", items -> {
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
