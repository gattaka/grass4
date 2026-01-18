package cz.gattserver.grass.core.ui.components.button;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import cz.gattserver.common.vaadin.ImageIcon;


public class ModifyGridButton<T> extends GridButton<T> {

    private static final long serialVersionUID = -5924239277930098183L;

    private static final String DEFAULT_CAPTION = "Upravit";

    public interface ClickListener<T> {
        void buttonClick(T item);
    }

    public ModifyGridButton(ClickListener<T> clickListener, Grid<T> grid) {
        this(DEFAULT_CAPTION, clickListener, grid);
    }

    public ModifyGridButton(String caption, ClickListener<T> clickListener, Grid<T> grid) {
        super(caption, items -> clickListener.buttonClick(items.iterator().next()), grid);
        setIcon(ImageIcon.PENCIL_16_ICON.createImage("Upravit"));
        setEnableResolver(items -> items.size() == 1);
    }

}
