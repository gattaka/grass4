package cz.gattserver.grass.core.ui.components;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.pages.NodePage;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass.core.ui.util.GridUtils;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.spring.SpringContextHelper;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

public class ContentsLazyGrid extends Grid<ContentNodeOverviewTO> {

    @Serial
    private static final long serialVersionUID = -3307437175337095155L;

    @Setter
    @Getter
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

    public void populate(FetchCallback<ContentNodeOverviewTO, Void> fetchCallback,
                         CountCallback<ContentNodeOverviewTO, Void> countCallback) {

        PageFactory noServicePageFactory = (PageFactory) SpringContextHelper.getBean("noServicePageFactory");
        ModuleRegister serviceHolder = SpringContextHelper.getContext().getBean(ModuleRegister.class);

        setDataProvider(DataProvider.fromCallbacks(fetchCallback, countCallback));

        String iconBind = "customIcon";
        String nameBind = "customName";
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
            Div div = new Div();
            ContentModule contentService = serviceHolder.getContentModulesByName(contentNode.contentReaderID());
            if (activeLinks) {
                String url = contentService == null ? UIUtils.getPageURL(noServicePageFactory) :
                        UIUtils.getPageURL(contentService.getContentViewerPageFactory(),
                                URLIdentifierUtils.createURLIdentifier(contentNode.contentID(), contentNode.name()));
                div.add(new Anchor(url, contentNode.name()));
            } else {
                div.add(new Text(contentNode.name()));
            }

            if (SpringContextHelper.getBean(SecurityService.class).getCurrentUser().isAdmin() && contentNode.hidden()) {
                Icon icon = VaadinIcon.EYE_SLASH.create();
                icon.setColor("#7f7f7f");
                div.add(" ");
                div.add(icon);
            }
            return div;
        })).setFlexGrow(2).setHeader("Název").setId(nameBind);

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
}