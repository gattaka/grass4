package cz.gattserver.grass.core.ui.components;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;

import cz.gattserver.grass.core.interfaces.ContentNodeOverviewTO;
import cz.gattserver.grass.core.modules.ContentModule;
import cz.gattserver.grass.core.modules.register.ModuleRegister;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass.core.ui.pages.template.MenuPage;
import cz.gattserver.grass.core.ui.util.GridUtils;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcon;

public class ContentsLazyGrid extends Grid<ContentNodeOverviewTO> {

	private static final long serialVersionUID = -5648982639686386190L;

	public ContentsLazyGrid() {
		super();
		UIUtils.applyGrassDefaultStyle(this);
	}

	public void populate(boolean showPubLock, final MenuPage page,
			FetchCallback<ContentNodeOverviewTO, Void> fetchCallback,
			CountCallback<ContentNodeOverviewTO, Void> countCallback) {

		PageFactory nodePageFactory = ((PageFactory) SpringContextHelper.getBean("nodePageFactory"));
		PageFactory noServicePageFactory = (PageFactory) SpringContextHelper.getBean("noServicePageFactory");
		ModuleRegister serviceHolder = SpringContextHelper.getContext().getBean(ModuleRegister.class);

		setDataProvider(DataProvider.fromCallbacks(fetchCallback, countCallback));
		setSelectionMode(SelectionMode.NONE);

		String iconBind = "customIcon";
		String nameBind = "customName";
		String lockIconBind = "lockIcon";
		String nodeBind = "customNode";
		String creationDateBind = "customCreationDate";
		String lastModificationDateBind = "customLastModificationDate";

		addColumn(new IconRenderer<ContentNodeOverviewTO>(c -> {
			ContentModule contentService = serviceHolder.getContentModulesByName(c.getContentReaderID());
			Image img = new Image(contentService == null ? ImageIcon.WARNING_16_ICON.createResource()
					: contentService.getContentIcon(), "");
			img.addClassName(UIUtils.GRID_ICON_CSS_CLASS);
			return img;
		}, c -> "")).setFlexGrow(0).setWidth("31px").setHeader("").setTextAlign(ColumnTextAlign.CENTER)
				.setKey(iconBind);

		addColumn(new ComponentRenderer<Anchor, ContentNodeOverviewTO>(contentNode -> {
			ContentModule contentService = serviceHolder.getContentModulesByName(contentNode.getContentReaderID());
			String url = contentService == null ? UIUtils.getPageURL(noServicePageFactory)
					: UIUtils.getPageURL(contentService.getContentViewerPageFactory(),
							URLIdentifierUtils.createURLIdentifier(contentNode.getContentID(), contentNode.getName()));
			return new Anchor(url, contentNode.getName());
		})).setFlexGrow(2).setHeader("Název").setId(nameBind);

		if (showPubLock) {
			addColumn(new IconRenderer<ContentNodeOverviewTO>(c -> {
				if (c.isPublicated()) {
					return new Span();
				} else {
					Image img = new Image(ImageIcon.SHIELD_16_ICON.createResource(), "locked");
					img.addClassName(UIUtils.GRID_ICON_CSS_CLASS);
					return img;
				}
			}, c -> "")).setFlexGrow(0).setWidth("26px").setHeader("").setKey(lockIconBind);
		}

		addColumn(new ComponentRenderer<Anchor, ContentNodeOverviewTO>(contentNode -> {
			String url = UIUtils.getPageURL(nodePageFactory, URLIdentifierUtils
					.createURLIdentifier(contentNode.getParentNodeId(), contentNode.getParentNodeName())) + "'>"
					+ contentNode.getParentNodeName();
			return new Anchor(url, contentNode.getParentNodeName());
		})).setFlexGrow(2).setHeader("Kategorie").setId(nodeBind);

		if (!UIUtils.isMobileDevice()) {
			addColumn(new LocalDateTimeRenderer<>(ContentNodeOverviewTO::getCreationDate, "d.M.yyyy"))
					.setHeader("Vytvořeno").setKey(creationDateBind).setClassNameGenerator(item -> "v-align-right")
					.setFlexGrow(0).setWidth("90px");

			addColumn(new LocalDateTimeRenderer<>(ContentNodeOverviewTO::getLastModificationDate, "d.M.yyyy"))
					.setHeader("Upraveno").setKey(lastModificationDateBind)
					.setClassNameGenerator(item -> "v-align-right").setFlexGrow(0).setWidth("90px");
		}

		setHeight(GridUtils.processHeight(countCallback.count(new Query<>())) + "px");
	}

}
