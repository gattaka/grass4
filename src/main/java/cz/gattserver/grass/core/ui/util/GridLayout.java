package cz.gattserver.grass.core.ui.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

import cz.gattserver.common.vaadin.Strong;

/**
 * https://divtable.com/generator/
 * 
 * @author gattaka
 *
 */
public class GridLayout extends Div {

	private static final long serialVersionUID = 8940148875365037922L;

	private Div currentRow;
	private Div currentCell;

	public GridLayout() {
		getStyle().set("display", "table");
	}

	public GridLayout newRow() {
		currentRow = new Div();
		currentRow.getStyle().set("display", "table-row");
		super.add(currentRow);
		return this;
	}

	public GridLayout addStrong(String value) {
		add(new Strong(value));
		return this;
	}

	public GridLayout addNewCell() {
		if (currentRow == null)
			newRow();
		currentCell = new Div();
		if (currentRow.getChildren().count() != 0)
			currentCell.getStyle().set("padding-left", "var(--lumo-space-m)");
		currentCell.getStyle().set("display", "table-cell").set("padding-bottom", "var(--lumo-space-m)");
		currentRow.add(currentCell);
		return this;
	}

	public void add(Component... components) {
		addNewCell();
		currentCell.add(components);
	}

}
