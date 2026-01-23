package cz.gattserver.grass.core.ui.components;

import java.util.List;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;

import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.grass.core.interfaces.NodeOverviewTO;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass.core.ui.util.GridUtils;
import cz.gattserver.grass.core.ui.util.UIUtils;

public class NodesGrid extends Grid<NodeOverviewTO> {

    private static final long serialVersionUID = -2220485504407844582L;

    public NodesGrid() {
        // inject nefunguje kvůli něčemu v předkovi
        final PageFactory nodePageFactory = (PageFactory) SpringContextHelper.getBean("nodePageFactory");

        UIUtils.applyGrassDefaultStyle(this);

        setHeight("200px");
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
            String url = UIUtils.getPageURL(nodePageFactory,
                    URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName()));
            return new Anchor(url, node.getName());
        })).setHeader("Kategorie").setId(nameBind);
    }

    public void populate(List<NodeOverviewTO> nodes) {
        setItems(nodes);
        setHeight(GridUtils.processHeight(nodes.size()) + "px");
    }
}