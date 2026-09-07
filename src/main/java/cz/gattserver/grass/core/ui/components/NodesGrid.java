package cz.gattserver.grass.core.ui.components;

import java.io.Serial;
import java.util.List;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;

import com.vaadin.flow.router.RouterLink;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.grass.core.interfaces.NodeTO;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.grass.core.ui.pages.NodePage;
import cz.gattserver.grass.core.ui.util.GridUtils;
import cz.gattserver.grass.core.ui.util.UIUtils;

public class NodesGrid extends Grid<NodeTO> {

    @Serial
    private static final long serialVersionUID = -4425495493485107174L;

    public NodesGrid(boolean showHidden) {
        UIUtils.applyGrassDefaultStyle(this);

        setHeight(200, Unit.PIXELS);
        setSelectionMode(SelectionMode.NONE);

        String iconBind = "customIcon";
        String nameBind = "customName";

        addColumn(new IconRenderer<>(c -> {
            Image img = ImageIcon.BRIEFCASE_16_ICON.createImage("");
            img.addClassName(UIUtils.GRID_ICON_CSS_CLASS);
            return img;
        }, c -> "")).setFlexGrow(0).setWidth("31px").setHeader("").setTextAlign(ColumnTextAlign.CENTER)
                .setKey(iconBind);

        addColumn(new ComponentRenderer<>(node -> {
            Div div = new Div();
            div.add(new RouterLink(node.getName(), NodePage.class,
                    URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName())));
            if (showHidden) new ComponentFactory().createHiddenSymbols(div, node.getHidden(), node.getHiddenByParent());
            return div;
        })).setHeader("Název").setId(nameBind);
    }

    public void populate(List<NodeTO> nodes) {
        setItems(nodes);
        setHeight(GridUtils.processHeight(nodes.size()) + "px");
    }
}