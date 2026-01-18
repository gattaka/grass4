package cz.gattserver.grass.core.ui.components;

import java.util.List;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;

import cz.gattserver.grass.core.interfaces.NodeTO;
import cz.gattserver.grass.core.modules.ContentModule;
import cz.gattserver.grass.core.modules.register.ModuleRegister;
import cz.gattserver.grass.core.ui.pages.template.MenuPage;
import cz.gattserver.grass.core.ui.util.GridUtils;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.spring.SpringContextHelper;

public class NewContentNodeGrid extends Grid<ContentModule> {

	private static final long serialVersionUID = -2220485504407844582L;

	public NewContentNodeGrid(MenuPage page, final NodeTO node) {
		// inject nefunguje kvůli něčemu v předkovi
		final ModuleRegister serviceHolder = SpringContextHelper.getContext().getBean(ModuleRegister.class);

		setSelectionMode(SelectionMode.NONE);
		UIUtils.applyGrassDefaultStyle(this);

		String iconBind = "customIcon";
		String nameBind = "customName";

		// jaké služby obsahů mám k dispozici ?
		List<ContentModule> contentServices = serviceHolder.getContentModules();
		setItems(contentServices);

		addColumn(new IconRenderer<>(c -> {
			Image img = c.getContentIcon();
			img.addClassName(UIUtils.GRID_ICON_CSS_CLASS);
			return img;
		}, c -> "")).setHeader("").setFlexGrow(0).setWidth("28px").setHeader("").setTextAlign(ColumnTextAlign.CENTER)
				.setKey(iconBind);

		addColumn(new ComponentRenderer<>(c -> {
            String url = UIUtils.getPageURL(c.getContentEditorPageFactory(), DefaultContentOperations.NEW.toString(),
                    URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName()));
            return new Anchor(url, c.getCreateNewContentLabel());
        })).setHeader("Obsah").setKey(nameBind);

		setHeight(GridUtils.processHeight(contentServices.size()) + "px");
	}
}