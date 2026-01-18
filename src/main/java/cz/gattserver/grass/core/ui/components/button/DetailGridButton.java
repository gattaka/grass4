package cz.gattserver.grass.core.ui.components.button;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import cz.gattserver.common.vaadin.ImageIcon;

public class DetailGridButton<T> extends GridButton<T> {

    private static final long serialVersionUID = -5924239277930098183L;

    private static final String DEFAULT_CAPTION = "Detail";

    public interface ClickListener<T> {
        void buttonClick(T item);
    }

    public DetailGridButton(ClickListener<T> clickListener, Grid<T> grid) {
        this(DEFAULT_CAPTION, clickListener, grid);
    }

    public DetailGridButton(String caption, ClickListener<T> clickListener, Grid<T> grid) {
        super(caption, items -> clickListener.buttonClick(items.iterator().next()), grid);
        setIcon(ImageIcon.INFO_16_ICON.createImage(DEFAULT_CAPTION));
        setEnableResolver(items -> items.size() == 1);
    }
}