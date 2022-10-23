package cz.gattserver.grass.core.ui.components.button;

import java.util.Set;
import java.util.function.Function;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.shared.Registration;

public class GridButton<T> extends Button {

	private static final long serialVersionUID = -5924239277930098183L;

	private Grid<T> grid;
	private Registration registration;

	public interface ClickListener<T> {
		public void buttonClick(Set<T> set);
	}

	public GridButton(String caption, Grid<T> grid) {
		this(caption, null, grid);
	}

	public void setClickListener(ClickListener<T> clickListener) {
		super.addClickListener(e -> clickListener.buttonClick(grid.getSelectedItems()));
	}

	public GridButton(String caption, ClickListener<T> clickListener, Grid<T> grid) {
		super(caption);
		this.grid = grid;
		setEnabled(false);
		if (clickListener != null)
			setClickListener(clickListener);
		setEnableResolver(t -> !grid.getSelectedItems().isEmpty());
	}

	public GridButton<T> setEnableResolver(Function<Set<T>, Boolean> enableResolver) {
		if (registration != null)
			registration.remove();
		registration = grid
				.addSelectionListener(e -> GridButton.this.setEnabled(enableResolver.apply(e.getAllSelectedItems())));
		return this;
	}

}
