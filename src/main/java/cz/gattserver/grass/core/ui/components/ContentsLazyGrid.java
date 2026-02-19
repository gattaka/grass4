package cz.gattserver.grass.core.ui.components;

import com.vaadin.flow.component.Text;
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

import com.vaadin.flow.router.RouterLink;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.grass.core.interfaces.ContentNodeOverviewTO;
import cz.gattserver.grass.core.modules.ContentModule;
import cz.gattserver.grass.core.modules.register.ModuleRegister;
import cz.gattserver.grass.core.ui.pages.NodePage;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass.core.ui.util.GridUtils;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.spring.SpringContextHelper;

public class ContentsLazyGrid extends Grid<ContentNodeOverviewTO> {

    private boolean dynamicHeight = true;
    private boolean activeLinks;

    public ContentsLazyGrid() {
        this(true);
    }

    public ContentsLazyGrid(boolean activeLinks) {
        UIUtils.applyGrassDefaultStyle(this);
        setSelectionMode(SelectionMode.NONE);
        this.activeLinks = activeLinks;
    }

    public void populate(boolean showPubLock, FetchCallback<ContentNodeOverviewTO, Void> fetchCallback,
                         CountCallback<ContentNodeOverviewTO, Void> countCallback) {

        PageFactory nodePageFactory = ((PageFactory) SpringContextHelper.getBean("nodePageFactory"));
        PageFactory noServicePageFactory = (PageFactory) SpringContextHelper.getBean("noServicePageFactory");
        ModuleRegister serviceHolder = SpringContextHelper.getContext().getBean(ModuleRegister.class);

        setDataProvider(DataProvider.fromCallbacks(fetchCallback, countCallback));

        String iconBind = "customIcon";
        String nameBind = "customName";
        String lockIconBind = "lockIcon";
        String nodeBind = "customNode";
        String creationDateBind = "customCreationDate";
        String lastModificationDateBind = "customLastModificationDate";

        addColumn(new IconRenderer<>(c -> {
            ContentModule contentService = serviceHolder.getContentModulesByName(c.contentReaderID());
            Image img =
                    contentService == null ? ImageIcon.WARNING_16_ICON.createImage() : contentService.getContentIcon();
            img.addClassName(UIUtils.GRID_ICON_CSS_CLASS);
            return img;
        }, c -> "")).setFlexGrow(0).setWidth("31px").setHeader("").setTextAlign(ColumnTextAlign.CENTER)
                .setKey(iconBind);

        addColumn(new ComponentRenderer<>(contentNode -> {
            ContentModule contentService = serviceHolder.getContentModulesByName(contentNode.contentReaderID());
            if (activeLinks) {
                String url = contentService == null ? UIUtils.getPageURL(noServicePageFactory) :
                        UIUtils.getPageURL(contentService.getContentViewerPageFactory(),
                                URLIdentifierUtils.createURLIdentifier(contentNode.contentID(), contentNode.name()));
                return new Anchor(url, contentNode.name());
            } else {
                return new Text(contentNode.name());
            }
        })).setFlexGrow(2).setHeader("Název").setId(nameBind);

        if (showPubLock) {
            addColumn(new IconRenderer<>(c -> {
                if (c.publicated()) {
                    return new Span();
                } else {
                    Image img = ImageIcon.SHIELD_16_ICON.createImage("locked");
                    img.addClassName(UIUtils.GRID_ICON_CSS_CLASS);
                    return img;
                }
            }, c -> "")).setFlexGrow(0).setTextAlign(ColumnTextAlign.CENTER).setWidth("31px").setHeader("")
                    .setKey(lockIconBind);
        }

        addColumn(new ComponentRenderer<>(contentNode -> {
            if (activeLinks) {
                return new RouterLink(contentNode.parentNodeName(), NodePage.class,
                        URLIdentifierUtils.createURLIdentifier(contentNode.parentNodeId(),
                                contentNode.parentNodeName()));
            } else {
                return new Text(contentNode.parentNodeName());
            }
        })).setFlexGrow(2).setHeader("Kategorie").setId(nodeBind);

        if (!UIUtils.isMobileDevice()) {
            addColumn(new LocalDateTimeRenderer<>(ContentNodeOverviewTO::creationDate, "d. M. yyyy")).setHeader(
                            "Vytvořeno").setKey(creationDateBind).setTextAlign(ColumnTextAlign.END).setFlexGrow(0)
                    .setWidth("90px");
            addColumn(new LocalDateTimeRenderer<>(ContentNodeOverviewTO::lastModificationDate, "d. M. yyyy")).setHeader(
                            "Upraveno").setKey(lastModificationDateBind).setTextAlign(ColumnTextAlign.END).setFlexGrow(0)
                    .setWidth("90px");
        }

        if (dynamicHeight) setHeight(GridUtils.processHeight(countCallback.count(new Query<>())) + "px");
    }

    public boolean isDynamicHeight() {
        return dynamicHeight;
    }

    public void setDynamicHeight(boolean dynamicHeight) {
        this.dynamicHeight = dynamicHeight;
    }
}
