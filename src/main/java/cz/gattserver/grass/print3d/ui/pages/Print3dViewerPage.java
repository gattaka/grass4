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
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.common.vaadin.InlineButton;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.common.vaadin.dialogs.WarnDialog;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.core.events.EventBus;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.interfaces.ContentNodeTO;
import cz.gattserver.grass.core.interfaces.NodeOverviewTO;
import cz.gattserver.grass.core.ui.components.DefaultContentOperations;
import cz.gattserver.grass.core.ui.components.button.ImageButton;
import cz.gattserver.grass.core.ui.dialogs.ProgressDialog;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass.core.ui.pages.template.ContentViewerPage;
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
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

@Route("print3d")
public class Print3dViewerPage extends ContentViewerPage implements HasUrlParameter<String>, HasDynamicTitle {

    private static final long serialVersionUID = 7334408385869747381L;

    private static final Logger logger = LoggerFactory.getLogger(Print3dViewerPage.class);

    @Autowired
    private Print3dService print3dService;

    @Resource(name = "print3dViewerPageFactory")
    private PageFactory print3dViewerPageFactory;

    @Resource(name = "print3dEditorPageFactory")
    private PageFactory print3dEditorPageFactory;

    @Autowired
    private EventBus eventBus;

    private ProgressDialog progressIndicatorWindow;

    private Print3dTO print3dTO;

    private Print3dMultiUpload upload;

    private String projectDir;

    private String identifierToken;
    private String magickToken;

    private List<Print3dViewItemTO> items;
    private Grid<Print3dViewItemTO> grid;

    private STLViewer stlViewer;

    @Override
    public String getPageTitle() {
        return print3dTO.getContentNode().getName();
    }

    @Override
    protected void createContentOperations(Div operationsListLayout) {
        super.createContentOperations(operationsListLayout);

        ImageButton downloadZip = new ImageButton("Zabalit do ZIP", ImageIcon.PRESENT_16_ICON,
                event -> new ConfirmDialog("Přejete si vytvořit ZIP projektu?", e -> {
                    logger.info("zipPrint3dProject thread: {}", Thread.currentThread().getId());
                    progressIndicatorWindow = new ProgressDialog();
                    eventBus.subscribe(Print3dViewerPage.this);
                    print3dService.zipProject(projectDir);
                }).open());
        operationsListLayout.add(downloadZip);
    }

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        String[] chunks = parameter.split("/");
        if (chunks.length > 0) identifierToken = chunks[0];
        if (chunks.length > 1) magickToken = chunks[1];

        URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(identifierToken);
        if (identifier == null) throw new GrassPageException(404);

        print3dTO = print3dService.getProjectForDetail(identifier.getId());
        if (print3dTO == null) throw new GrassPageException(404);

        if (!"MAG1CK".equals(magickToken) && !print3dTO.getContentNode().isPublicated() && !isAdminOrAuthor())
            throw new GrassPageException(403);

        projectDir = print3dTO.getPrint3dProjectPath();

        init();

        UIUtils.turnOffRouterAnchors();
    }

    private boolean isAdminOrAuthor() {
        return getUser().isAdmin() || print3dTO.getContentNode().getAuthor().equals(getUser());
    }

    @Override
    protected ContentNodeTO getContentNodeDTO() {
        return print3dTO.getContentNode();
    }

    @Override
    protected void createContent(Div layout) {
        // pokud je obsah porušený, pak nic nevypisuj
        try {
            if (!print3dService.checkProject(projectDir)) {
                layout.add(new Span("Chyba: Projekt je porušen -- kontaktujte administrátora (ID: " +
                        print3dTO.getPrint3dProjectPath() + ")"));
                return;
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

        Column<Print3dViewItemTO> iconColumn = grid.addColumn(new IconRenderer<Print3dViewItemTO>(p -> {
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
                                return Long.compare(Files.size(o1.getFile()), Files.size(o2.getFile()));
                            } catch (IOException e) {
                                logger.error(
                                        "Nezdařilo se porovnat soubory 3D projektu " + o1.getName() + " a " + o2.getName());
                                return 0;
                            }
                        });

        Column<Print3dViewItemTO> fullnameColumn = grid.getColumnByKey("name");
        Column<Print3dViewItemTO> typeColumn = grid.getColumnByKey("type");
        Column<Print3dViewItemTO> fileColumn = grid.getColumnByKey("file");

        fullnameColumn.setVisible(false);
        typeColumn.setVisible(false);
        fileColumn.setVisible(false);

        grid.setColumnOrder(Arrays.asList(iconColumn, nameColumn, extensionColumn, sizeColumn, typeColumn, fileColumn,
                fullnameColumn));

        grid.addColumn(new ComponentRenderer<>(item -> {
            String url = getItemURL(item.getName());
            Anchor link = new Anchor(url, "Stáhnout");
            link.addClassName(UIUtils.BUTTON_LINK_CSS_CLASS);
            link.setTarget("_blank");
            return link;
        })).setHeader("Stáhnout").setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);

        if (coreACL.canModifyContent(getContentNodeDTO(), getUser())) {
            grid.addColumn(new ComponentRenderer<>(item -> new InlineButton("Smazat", be -> {
                new ConfirmDialog("Opravdu smazat soubor?", e -> {
                    print3dService.deleteFile(item, projectDir);
                    UI.getCurrent().getPage().reload();
                }).open();
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

        upload = new Print3dMultiUpload(projectDir);
        Button uploadButton = new Button("Upload");
        upload.setUploadButton(uploadButton);
        Span dropLabel = new Span("Drop");
        upload.setDropLabel(dropLabel);
        upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        upload.addFinishedListener(e -> {
            eventBus.subscribe(Print3dViewerPage.this);
            progressIndicatorWindow = new ProgressDialog();
            Print3dPayloadTO payloadTO = new Print3dPayloadTO(print3dTO.getContentNode().getName(), projectDir,
                    print3dTO.getContentNode().getContentTagsAsStrings(), print3dTO.getContentNode().isPublicated());
            print3dService.modifyProject(print3dTO.getId(), payloadTO);
            UI.getCurrent().getPage().reload();
        });
        if (coreACL.canModifyContent(print3dTO.getContentNode(), getUser())) layout.add(upload);

        Div statusRow = new Div();
        statusRow.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        statusRow.getStyle().set("background", "#fdfaf2").set("padding", "3px 10px").set("line-height", "20px")
                .set("font-size", "12px").set("color", "#777");
        statusRow.setSizeUndefined();
        statusRow.setText("Projekt: " + print3dTO.getPrint3dProjectPath() + " celkem položek: " + items.size());
        layout.add(statusRow);
    }

    @Handler
    protected void onProcessStart(final Print3dZipProcessStartEvent event) {
        progressIndicatorWindow.runInUI(() -> {
            progressIndicatorWindow.setTotal(event.getCountOfStepsToDo());
            progressIndicatorWindow.open();
        });
    }

    @Handler
    protected void onProcessProgress(Print3dZipProcessProgressEvent event) {
        progressIndicatorWindow.runInUI(() -> progressIndicatorWindow.indicateProgress(event.getStepDescription()));
    }

    @Handler
    protected void onProcessResult(final Print3dZipProcessResultEvent event) {
        progressIndicatorWindow.runInUI(() -> {
            if (progressIndicatorWindow != null) progressIndicatorWindow.close();

            if (event.isSuccess()) {
                WebDialog win = new WebDialog();
                win.addDialogCloseActionListener(e -> print3dService.deleteZipFile(event.getZipFile()));

                Anchor link = new Anchor(DownloadHandler.fromInputStream(e -> {
                    try {
                        String zipName = print3dTO.getPrint3dProjectPath() + ".zip";
                        return new DownloadResponse(Files.newInputStream(event.getZipFile()), zipName, null, -1);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        return null;
                    }
                }), "Stáhnout ZIP souboru");
                link.setTarget("_blank");
                win.addComponent(link, Alignment.CENTER);

                Button proceedButton = new Button("Zavřít", e -> win.close());
                win.addComponent(proceedButton, Alignment.CENTER);

                win.open();
            } else {
                UIUtils.showWarning(event.getResultDetails());
            }
        });
        eventBus.unsubscribe(Print3dViewerPage.this);
    }

    private String getItemURL(String file) {
        return getContextPath() + "/" + Print3dConfiguration.PRINT3D_PATH + "/" + print3dTO.getPrint3dProjectPath() +
                "/" + file;
    }

    @Override
    protected PageFactory getContentViewerPageFactory() {
        return print3dViewerPageFactory;
    }

    @Override
    protected void onDeleteOperation() {
        ConfirmDialog confirmSubwindow = new ConfirmDialog("Opravdu si přejete smazat tento projekt ?", ev -> {
            NodeOverviewTO nodeDTO = print3dTO.getContentNode().getParent();

            final String nodeURL = getPageURL(nodePageFactory,
                    URLIdentifierUtils.createURLIdentifier(nodeDTO.getId(), nodeDTO.getName()));

            // zdařilo se ? Pokud ano, otevři info okno a při
            // potvrzení jdi na kategorii
            if (print3dService.deleteProject(print3dTO.getId())) {
                UIUtils.redirect(nodeURL);
            } else {
                // Pokud ne, otevři warn okno a při
                // potvrzení jdi na kategorii
                WarnDialog warnSubwindow = new WarnDialog("Při mazání projektu se nezdařilo smazat některé soubory.");
                warnSubwindow.addDialogCloseActionListener(e -> UIUtils.redirect(nodeURL));
                warnSubwindow.open();
            }
        });
        confirmSubwindow.open();
    }

    @Override
    protected void onEditOperation() {
        UIUtils.redirect(getPageURL(print3dEditorPageFactory, DefaultContentOperations.EDIT.toString(),
                URLIdentifierUtils.createURLIdentifier(print3dTO.getId(), print3dTO.getContentNode().getName())));
    }
}
