package cz.gattserver.grass.print3d.ui.pages;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.ui.UploadBuilder;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.common.vaadin.dialogs.WarnDialog;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.core.events.EventBus;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.interfaces.ContentNodeTO;
import cz.gattserver.grass.core.interfaces.NodeOverviewTO;
import cz.gattserver.grass.core.services.CoreACLService;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.components.DefaultContentOperations;
import cz.gattserver.grass.core.ui.dialogs.ProgressDialog;
import cz.gattserver.grass.core.ui.pages.MainView;
import cz.gattserver.grass.core.ui.pages.NodePage;
import cz.gattserver.grass.core.ui.pages.template.ContentViewer;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.print3d.config.Print3dConfiguration;
import cz.gattserver.grass.print3d.events.impl.Print3dZipProcessProgressEvent;
import cz.gattserver.grass.print3d.events.impl.Print3dZipProcessResultEvent;
import cz.gattserver.grass.print3d.events.impl.Print3dZipProcessStartEvent;
import cz.gattserver.grass.print3d.interfaces.Print3dItemType;
import cz.gattserver.grass.print3d.interfaces.Print3dPayloadTO;
import cz.gattserver.grass.print3d.interfaces.Print3dTO;
import cz.gattserver.grass.print3d.interfaces.Print3dViewItemTO;
import cz.gattserver.grass.print3d.service.Print3dService;
import cz.gattserver.common.stlviewer.STLViewer;
import net.engio.mbassy.listener.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "print3d", layout = MainView.class)
public class Print3DViewerPage extends Div implements HasUrlParameter<String>, HasDynamicTitle {

    private static final Logger logger = LoggerFactory.getLogger(Print3DViewerPage.class);

    private Print3dService print3dService;
    private EventBus eventBus;
    private SecurityService securityService;
    private CoreACLService coreACLService;

    private ProgressDialog progressIndicatorWindow;

    private Print3dTO print3dTO;

    private String projectDir;

    private String identifierToken;
    private String magickToken;

    private List<Print3dViewItemTO> items;
    private Grid<Print3dViewItemTO> grid;

    private STLViewer stlViewer;

    private ComponentFactory componentFactory;

    public Print3DViewerPage(Print3dService print3dService, EventBus eventBus, SecurityService securityService,
                             CoreACLService coreACLService) {
        this.print3dService = print3dService;
        this.eventBus = eventBus;
        this.securityService = securityService;
        this.coreACLService = coreACLService;
        this.componentFactory = new ComponentFactory();
    }

    @Override
    public String getPageTitle() {
        return print3dTO.getContentNode().name();
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @WildcardParameter String parameter) {
        String[] chunks = parameter.split("/");
        if (chunks.length > 0) identifierToken = chunks[0];
        if (chunks.length > 1) magickToken = chunks[1];

        URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(identifierToken);
        if (identifier == null) throw new GrassPageException(404);

        print3dTO = print3dService.getProjectForDetail(identifier.getId());
        if (print3dTO == null) throw new GrassPageException(404);

        if (!"MAG1CK".equals(magickToken) && !print3dTO.getContentNode().publicated() && !isAdminOrAuthor())
            throw new GrassPageException(403);

        projectDir = print3dTO.getPrint3dProjectPath();

        removeAll();
        ContentNodeTO contentNodeTO = print3dTO.getContentNode();
        ContentViewer contentViewer = new ContentViewer(createContent(), contentNodeTO, e -> onDeleteOperation(),
                e -> UI.getCurrent()
                        .navigate(Print3dEditorPage.class, DefaultContentOperations.EDIT.withParameter(parameter)),
                new RouterLink(contentNodeTO.name(), Print3DViewerPage.class, parameter));

        add(contentViewer);
        contentViewer.getOperationsListLayout().add(componentFactory.createZipButton(
                event -> new ConfirmDialog("Přejete si vytvořit ZIP projektu?", e -> {
                    logger.info("zipPrint3dProject thread: {}", Thread.currentThread().threadId());
                    progressIndicatorWindow = new ProgressDialog();
                    eventBus.subscribe(Print3DViewerPage.this);
                    print3dService.zipProject(projectDir);
                }).open()));

        UIUtils.turnOffRouterAnchors();
    }

    private boolean isAdminOrAuthor() {
        return securityService.getCurrentUser().isAdmin() ||
                print3dTO.getContentNode().getAuthor().equals(securityService.getCurrentUser());
    }

    protected Div createContent() {
        Div layout = new Div();
        // pokud je obsah porušený, pak nic nevypisuj
        try {
            if (!print3dService.checkProject(projectDir)) {
                layout.add(new Span("Chyba: Projekt je porušen -- kontaktujte administrátora (ID: " +
                        print3dTO.getPrint3dProjectPath() + ")"));
                return layout;
            }
        } catch (IllegalStateException e) {
            throw new GrassPageException(500, e);
        } catch (IllegalArgumentException e) {
            throw new GrassPageException(404, e);
        }

        stlViewer = new STLViewer(e -> {
            for (Print3dViewItemTO to : items) {
                if (to.getType() == Print3dItemType.MODEL) {
                    grid.select(to);
                    break;
                }
            }
        });

        stlViewer.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        stlViewer.getStyle().set("border", "1px solid #d1d1d1").set("box-sizing", "border-box")
                .set("background", "#fefefe");
        layout.add(stlViewer);
        stlViewer.setWidthFull();
        stlViewer.setHeight("500px");

        String imgContId = "imgcont";
        Div imgDiv = new Div();
        imgDiv.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        imgDiv.getStyle().set("border", "1px solid #d1d1d1").set("box-sizing", "border-box").set("text-align", "center")
                .set("background", "#fefefe");
        imgDiv.setId(imgContId);
        layout.add(imgDiv);
        imgDiv.setWidthFull();
        imgDiv.setHeight("500px");
        imgDiv.setVisible(false);

        Span span = new Span();
        span.getStyle().set("vertical-align", "middle").set("display", "inline-block");
        span.setHeightFull();
        imgDiv.add(span);

        Image img = new Image("", "");
        img.getStyle().set("vertical-align", "middle");
        img.setMaxHeight("490px");
        img.setMaxWidth("690px");
        imgDiv.add(img);

        try {
            items = print3dService.getItems(print3dTO.getPrint3dProjectPath());
        } catch (Exception e) {
            throw new GrassPageException(500, e);
        }

        grid = new Grid<>(Print3dViewItemTO.class);
        grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        grid.setItems(items);
        grid.setWidthFull();
        grid.setHeight("300px");
        UIUtils.applyGrassDefaultStyle(grid);
        layout.add(grid);

        Column<Print3dViewItemTO> iconColumn = grid.addColumn(new IconRenderer<>(p -> {
            ImageIcon icon = ImageIcon.DOCUMENT_16_ICON;
            if (p.getType() == Print3dItemType.IMAGE) icon = ImageIcon.IMG_16_ICON;
            if (p.getType() == Print3dItemType.MODEL) icon = ImageIcon.STOP_16_ICON;
            Image iconImg = icon.createImage();
            iconImg.addClassName(UIUtils.GRID_ICON_CSS_CLASS);
            return iconImg;
        }, c -> "")).setFlexGrow(0).setWidth("31px").setHeader("").setTextAlign(ColumnTextAlign.CENTER).setKey("icon");

        Column<Print3dViewItemTO> nameColumn =
                grid.getColumnByKey("onlyName").setHeader("Název").setFlexGrow(100).setSortable(true);

        Column<Print3dViewItemTO> extensionColumn =
                grid.getColumnByKey("extension").setHeader("Typ").setWidth("80px").setTextAlign(ColumnTextAlign.CENTER)
                        .setFlexGrow(0).setSortable(true);

        Column<Print3dViewItemTO> sizeColumn =
                grid.getColumnByKey("size").setHeader("Velikost").setWidth("80px").setTextAlign(ColumnTextAlign.END)
                        .setFlexGrow(0).setSortable(true).setComparator((o1, o2) -> {
                            try {
                                return Long.compare(Files.size(o1.getPath()), Files.size(o2.getPath()));
                            } catch (IOException e) {
                                logger.error(
                                        "Nezdařilo se porovnat soubory 3D projektu " + o1.getName() + " a " + o2.getName());
                                return 0;
                            }
                        });

        Column<Print3dViewItemTO> fullnameColumn = grid.getColumnByKey("name");
        Column<Print3dViewItemTO> typeColumn = grid.getColumnByKey("type");
        Column<Print3dViewItemTO> pathColumn = grid.getColumnByKey("path");

        fullnameColumn.setVisible(false);
        typeColumn.setVisible(false);
        pathColumn.setVisible(false);

        grid.setColumnOrder(Arrays.asList(iconColumn, nameColumn, extensionColumn, sizeColumn, typeColumn, pathColumn,
                fullnameColumn));

        grid.addColumn(new ComponentRenderer<>(item -> {
            String url = getItemURL(item.getName());
            Anchor link = new Anchor(url, "Stáhnout");
            link.setTarget("_blank");
            return link;
        })).setHeader("Stáhnout").setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);

        if (coreACLService.canModifyContent(print3dTO.getContentNode(), securityService.getCurrentUser())) {
            grid.addColumn(new ComponentRenderer<>(item -> componentFactory.createDeleteInlineButton(e -> {
                print3dService.deleteFile(item, projectDir);
                UI.getCurrent().getPage().reload();
            }))).setHeader("Smazat").setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);
        }

        grid.sort(Arrays.asList(new GridSortOrder<>(extensionColumn, SortDirection.ASCENDING),
                new GridSortOrder<>(nameColumn, SortDirection.ASCENDING)));

        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.addSelectionListener(item -> {
            if (!item.getFirstSelectedItem().isPresent()) return;
            Print3dViewItemTO to = item.getFirstSelectedItem().get();
            if (to.getType() == Print3dItemType.MODEL) {
                stlViewer.setVisible(true);
                imgDiv.setVisible(false);
                stlViewer.show(getItemURL(to.getName()));
            }
            if (to.getType() == Print3dItemType.IMAGE) {
                imgDiv.setVisible(true);
                stlViewer.setVisible(false);
                img.setSrc(getItemURL(to.getName()));
            }
        });

        UploadBuilder uploadBuilder = new UploadBuilder();
        Upload upload = uploadBuilder.createUpload(set -> {
            if (set.isEmpty()) return;
            for (UploadBuilder.UploadFile file : set) {
                try {
                    print3dService.uploadFile(new FileInputStream(file.getFile()), file.getMetadata().fileName(),
                            projectDir);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            Print3dPayloadTO payloadTO = new Print3dPayloadTO(print3dTO.getContentNode().name(), projectDir,
                    print3dTO.getContentNode().getContentTagsAsStrings(), print3dTO.getContentNode().publicated());
            print3dService.modifyProject(print3dTO.getId(), payloadTO);
            UI.getCurrent().getPage().reload();
        }, () -> print3dService.getItems(projectDir).stream().map(Print3dViewItemTO::getName)
                .collect(Collectors.toSet()));
        upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        if (coreACLService.canModifyContent(print3dTO.getContentNode(), securityService.getCurrentUser()))
            layout.add(upload);

        Div statusRow = new Div();
        statusRow.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        statusRow.getStyle().set("background", "#fdfaf2").set("padding", "3px 10px").set("line-height", "20px")
                .set("font-size", "12px").set("color", "#777");
        statusRow.setSizeUndefined();
        statusRow.setText("Projekt: " + print3dTO.getPrint3dProjectPath() + " celkem položek: " + items.size());
        layout.add(statusRow);

        return layout;
    }

    @Handler
    protected void onProcessStart(final Print3dZipProcessStartEvent event) {
        progressIndicatorWindow.runInUI(() -> {
            progressIndicatorWindow.setTotal(event.steps());
            progressIndicatorWindow.open();
        });
    }

    @Handler
    protected void onProcessProgress(Print3dZipProcessProgressEvent event) {
        progressIndicatorWindow.runInUI(() -> progressIndicatorWindow.indicateProgress(event.description()));
    }

    @Handler
    protected void onProcessResult(final Print3dZipProcessResultEvent event) {
        progressIndicatorWindow.runInUI(() -> {
            if (progressIndicatorWindow != null) progressIndicatorWindow.close();

            if (event.success()) {
                WebDialog win = new WebDialog("Komprese");
                win.addDialogCloseActionListener(e -> print3dService.deleteZipFile(event.getZipFile()));

                Anchor link = new Anchor(DownloadHandler.fromInputStream(e -> {
                    try {
                        String zipName = print3dTO.getPrint3dProjectPath() + ".zip";
                        return new DownloadResponse(Files.newInputStream(event.getZipFile()), zipName, null, -1);
                    } catch (IOException ex) {
                        logger.error("Během komprese souborů 3D projektu došlo k chybě", ex);
                        return null;
                    }
                }), "Stáhnout ZIP souboru");
                link.setTarget("_blank");
                win.addComponent(link, Alignment.CENTER);

                Button proceedButton = new Button("Zavřít", e -> win.close());
                win.addComponent(proceedButton, Alignment.CENTER);

                win.open();
            } else {
                UIUtils.showWarning(event.resultDetails());
            }
        });
        eventBus.unsubscribe(Print3DViewerPage.this);
    }

    private String getItemURL(String file) {
        return UIUtils.getContextPath() + "/" + Print3dConfiguration.PRINT3D_PATH + "/" +
                print3dTO.getPrint3dProjectPath() + "/" + file;
    }

    protected void onDeleteOperation() {
        ConfirmDialog confirmSubwindow = new ConfirmDialog("Opravdu si přejete smazat tento projekt ?", ev -> {
            NodeOverviewTO nodeDTO = print3dTO.getContentNode().getParent();

            String urlIdentifier = URLIdentifierUtils.createURLIdentifier(nodeDTO.getId(), nodeDTO.getName());

            // zdařilo se ? Pokud ano, otevři info okno a při
            // potvrzení jdi na kategorii
            if (print3dService.deleteProject(print3dTO.getId())) {
                UI.getCurrent().navigate(NodePage.class, urlIdentifier);
            } else {
                // Pokud ne, otevři warn okno a při
                // potvrzení jdi na kategorii
                WarnDialog warnSubwindow = new WarnDialog("Při mazání projektu se nezdařilo smazat některé soubory.");
                warnSubwindow.addDialogCloseActionListener(
                        e -> UI.getCurrent().navigate(NodePage.class, urlIdentifier));
                warnSubwindow.open();
            }
        });
        confirmSubwindow.open();
    }
}
