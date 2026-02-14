package cz.gattserver.grass.core.ui.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.dom.Element;

import cz.gattserver.common.vaadin.Strong;

/**
 * https://vaadin.com/forum/thread/17632449/new-table-like-component
 * 
 * @author gattaka
 *
 */
@Tag("table")
public class TableLayout extends Component {

	private Element currentRow;
	private Element currentCell;

	public TableLayout newRow() {
		currentRow = new Element("tr");
		getElement().appendChild(currentRow);
		return this;
	}

	public TableLayout addStrong(String value) {
		add(new Strong(value));
		return this;
	}

	public TableLayout addNewCell() {
		if (currentRow == null)
			newRow();
		currentCell = new Element("td");
		currentRow.appendChild(currentCell);
		return this;
	}

	public TableLayout setColSpan(int colspan) {
		if (currentCell != null)
			currentCell.setAttribute("colspan", String.valueOf(colspan));
		return this;
	}

	public TableLayout add(Component component, boolean newCell) {
		if (newCell || currentCell == null)
			addNewCell();
		currentCell.appendChild(component.getElement());
		return this;
	}

	public TableLayout add(Component component) {
		return add(component, true);
	}

	public TableLayout add(String text) {
		if (text == null)
			text = "";
		return add(new Text(text));
	}

	public TableLayout add(String text, boolean newCell) {
		if (text == null)
			text = "";
		return add(new Text(text), newCell);
	}

}
