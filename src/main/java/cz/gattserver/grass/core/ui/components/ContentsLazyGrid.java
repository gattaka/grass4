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
import cz.gattserver.grass.core.ui.util.GridUtils;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.spring.SpringContextHelper;

public class ContentsLazyGrid extends Grid<ContentNodeOverviewTO> {

    private static final long serialVersionUID = -5648982639686386190L;

    private boolean dynamicHeight = true;

    public ContentsLazyGrid() {
        super();
        UIUtils.applyGrassDefaultStyle(this);
        setSelectionMode(SelectionMode.NONE);
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
            ContentModule contentService = serviceHolder.getContentModulesByName(c.getContentReaderID());
            Image img =
                    contentService == null ? ImageIcon.WARNING_16_ICON.createImage() : contentService.getContentIcon();
            img.addClassName(UIUtils.GRID_ICON_CSS_CLASS);
            return img;
        }, c -> "")).setFlexGrow(0).setWidth("31px").setHeader("").setTextAlign(ColumnTextAlign.CENTER)
                .setKey(iconBind);

        addColumn(new ComponentRenderer<>(contentNode -> {
            ContentModule contentService = serviceHolder.getContentModulesByName(contentNode.getContentReaderID());
            String url = contentService == null ? UIUtils.getPageURL(noServicePageFactory) :
                    UIUtils.getPageURL(contentService.getContentViewerPageFactory(),
                            URLIdentifierUtils.createURLIdentifier(contentNode.getContentID(), contentNode.getName()));
            return new Anchor(url, contentNode.getName());
        })).setFlexGrow(2).setHeader("Název").setId(nameBind);

        if (showPubLock) {
            addColumn(new IconRenderer<>(c -> {
                if (c.isPublicated()) {
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
            String url = UIUtils.getPageURL(nodePageFactory,
                    URLIdentifierUtils.createURLIdentifier(contentNode.getParentNodeId(),
                            contentNode.getParentNodeName())) + "'>" + contentNode.getParentNodeName();
            return new Anchor(url, contentNode.getParentNodeName());
        })).setFlexGrow(2).setHeader("Kategorie").setId(nodeBind);

        if (!UIUtils.isMobileDevice()) {
            addColumn(new LocalDateTimeRenderer<>(ContentNodeOverviewTO::getCreationDate, "d. M. yyyy")).setHeader(
                            "Vytvořeno").setKey(creationDateBind).setTextAlign(ColumnTextAlign.END).setFlexGrow(0)
                    .setWidth("90px");
            addColumn(new LocalDateTimeRenderer<>(ContentNodeOverviewTO::getLastModificationDate,
                    "d. M. yyyy")).setHeader("Upraveno").setKey(lastModificationDateBind)
                    .setTextAlign(ColumnTextAlign.END).setFlexGrow(0).setWidth("90px");
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
