package cz.gattserver.grass.fm.web;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import com.vaadin.flow.server.streams.UploadHandler;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.util.CZAmountFormatter;
import cz.gattserver.common.vaadin.HtmlDiv;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.common.vaadin.dialogs.DownloadDialog;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.core.events.EventBus;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.services.FileSystemService;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.components.Breadcrumb;
import cz.gattserver.grass.core.ui.dialogs.ProgressDialog;
import cz.gattserver.grass.core.ui.pages.MainView;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.fm.FMExplorer;
import cz.gattserver.grass.fm.FMSection;
import cz.gattserver.grass.fm.FileProcessState;
import cz.gattserver.grass.fm.events.FMZipProcessProgressEvent;
import cz.gattserver.grass.fm.events.FMZipProcessResultEvent;
import cz.gattserver.grass.fm.events.FMZipProcessStartEvent;
import cz.gattserver.grass.fm.interfaces.FMItemTO;
import cz.gattserver.grass.fm.service.FMService;
import lombok.extern.slf4j.Slf4j;
import net.engio.mbassy.listener.Handler;
import net.glxn.qrgen.javase.QRCode;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.Resource;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.*;

@Slf4j
@PageTitle("Správce souborů")
@Route(value = "fm", layout = MainView.class)
public class FMPage extends Div implements HasUrlParameter<String>, BeforeEnterObserver {

    @Serial
    private static final long serialVersionUID = -4876290732058009438L;

    @Resource(name = "fmPageFactory")
    private PageFactory fmPageFactory;

    private final FileSystemService fileSystemService;
    private final EventBus eventBus;
    private final FMService fmService;
    private final SecurityService securityService;
    private final FMSection fmSection;

    private final ComponentFactory componentFactory;
    private final CZAmountFormatter selectFormatter;
    private final CZAmountFormatter listFormatter;

    private ProgressDialog progressDialog;
    private String listFormatterValue;
    private String filterName;

    /**
     * FM Explorer s potřebnými daty a metodami pro procházení souborů
     */
    private FMExplorer explorer;

    /**
     * Files table
     */
    private Grid<FMItemTO> grid;

    /**
     * Status label, vybrané soubory apod.
     */
    private Div statusLabel;

    /**
     * Breadcrumb
     */
    private Breadcrumb breadcrumb;

    private String urlBase;
    private String parameter;

    public FMPage(FileSystemService fileSystemService, EventBus eventBus, FMService fmService,
                  SecurityService securityService, FMSection fmSection) {
        this.fileSystemService = fileSystemService;
        this.eventBus = eventBus;
        this.fmService = fmService;
        this.securityService = securityService;
        this.fmSection = fmSection;
        componentFactory = new ComponentFactory();
        selectFormatter = new CZAmountFormatter("Vybrán %d soubor", "Vybrány %d soubory", "Vybráno %d souborů");
        listFormatter = new CZAmountFormatter("Zobrazen %d soubor", "Zobrazeny %d soubory", "Zobrazeno %d souborů");
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @WildcardParameter String path) {
        this.parameter = path;
        removeAll();
        ComponentFactory componentFactory = new ComponentFactory();

        Div layout = componentFactory.createOneColumnLayout();
        add(layout);

        urlBase = UIUtils.getURLBase();

        statusLabel = new Div();
        statusLabel.getStyle().set("border", "1px solid hsl(220, 14%, 88%)").set("padding", "4px 10px")
                .set("background", "white").set("font-size", "12px").set("border-top", "none")
                .set("color", "hsl(220, 14%, 61%)");
        breadcrumb = new Breadcrumb();

        FileSystem fileSystem = fileSystemService.getFileSystem();

        explorer = new FMExplorer(fileSystem);
        FileProcessState result = explorer.goToDir(path);

        switch (result) {
            case SUCCESS:
                // úspěch - pokračujeme
                String url = urlBase + "/" + path;
                explorer.goToDirByURL(UIUtils.getContextPath(), fmPageFactory.getPageName(), url);
                break;
            case MISSING:
                UIUtils.showWarning("Cíl neexistuje - vracím se do kořenového adresáře");
                break;
            case NOT_VALID:
                UIUtils.showWarning(
                        "Cíl se nachází mimo povolený rozsah souborů k prohlížení - vracím se do kořenového adresáře");
                break;
            case SYSTEM_ERROR:
                UIUtils.showWarning("Z cíle nelze číst - vracím se do kořenového adresáře");
                break;
            default:
                UIUtils.showWarning("Neznámá chyba - vracím se do kořenového adresáře");
                break;
        }

        createBreadcrumb(layout);
        createFilesGrid(layout);

        layout.add(statusLabel);

        Upload upload = getUpload();
        layout.add(upload);

        createButtonsLayout(layout);
    }

    private @NonNull Upload getUpload() {
        Upload upload = new Upload(UploadHandler.toTempFile((metadata, file) -> {
            switch (explorer.saveFile(new FileInputStream(file), metadata.fileName())) {
                case SUCCESS:
                    // refresh
                    populateGrid();
                    break;
                case ALREADY_EXISTS:
                    UIUtils.showWarning("Soubor '" + metadata.fileName() +
                            "' nebylo možné uložit - soubor s tímto názvem již existuje.");
                    break;
                case NOT_VALID:
                    UIUtils.showWarning("Soubor '" + metadata.fileName() +
                            "' nebylo možné uložit - cílové umístění souboru se nachází mimo povolený rozsah " +
                            "souborů" + " k prohlížení.");
                    break;
                default:
                    UIUtils.showWarning(
                            "Soubor '" + metadata.fileName() + "' nebylo možné uložit - došlo k systémové chybě.");
            }
        }));
        upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        return upload;
    }

    private void createBreadcrumb(Div layout) {
        layout.add(breadcrumb);
        populateBreadcrumb();
    }

    private void populateBreadcrumb() {
        // pokud zjistím, že cesta neodpovídá, vyhodím 302 (přesměrování) na
        // aktuální polohu cílové kategorie
        List<Breadcrumb.BreadcrumbElement> breadcrumbElements = new ArrayList<>();
        for (FMItemTO c : explorer.getBreadcrumbChunks())
            breadcrumbElements.add(new Breadcrumb.BreadcrumbElement(c.name(), FMPage.class, c.pathFromFMRoot()));
        breadcrumb.resetBreadcrumb(breadcrumbElements);
    }

    private void createFilesGrid(Div layout) {
        grid = new Grid<>();
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.setColumnReorderingAllowed(true);
        UIUtils.applyGrassDefaultStyle(grid);
        grid.removeThemeVariants(GridVariant.LUMO_COMPACT);
        grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(grid);

        grid.addColumn(new IconRenderer<>(to -> {
            Image img =
                    to.directory() ? ImageIcon.FOLDER_16_ICON.createImage() : ImageIcon.DOCUMENT_16_ICON.createImage();
            img.addClassName(UIUtils.GRID_ICON_CSS_CLASS);
            return img;
        }, to -> "")).setFlexGrow(0).setWidth("36px").setHeader("").setTextAlign(ColumnTextAlign.CENTER);

        Column<FMItemTO> nameColumn =
                grid.addColumn(FMItemTO::name).setHeader("Název").setFlexGrow(100).setSortProperty("name");

        grid.addColumn(FMItemTO::size).setHeader("Velikost").setTextAlign(ColumnTextAlign.END).setWidth("100px")
                .setFlexGrow(0).setSortProperty("size");

        grid.addColumn(new ComponentRenderer<>(to -> {
            Div button = componentFactory.createInlineButton("URL", e -> {
                Dialog ww = new Dialog();
                String id = UUID.randomUUID().toString();
                String checkId = "check-" + id;
                HtmlDiv text = new HtmlDiv(
                        "<input style=\"width: inherit\" id=\"" + id + "\" value=\"" + getDownloadLink(to) + "\"/>" +
                                "<br/><span id=\"" + checkId + "\" onload=''></span>");
                text.getStyle().set("width", "400px").set("text-align", "center").set("line-height", "30px")
                        .set("color", "dodgerblue").set("font-weight", "bold");
                ww.add(text);
                ww.open();

                // musí mít mírný timeout, jinak bude referencovat ještě
                // nevykreslený element a dotaz podle ID bude null
                UI.getCurrent().getPage().executeJs(
                        "setTimeout(function(){" + "document.getElementById(\"" + id + "\").select(); " +
                                "if (document.execCommand(\"copy\")) { " + "document.getElementById(\"" + checkId +
                                "\").innerHTML = \"URL zkopírováno do schránky\";" + "}" + "},10)");
            });
            button.setVisible(!to.directory());
            return button;
        })).setHeader("URL").setTextAlign(ColumnTextAlign.CENTER).setWidth("50px").setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(
                        to -> componentFactory.createInlineButton("Stáhnout", e -> handleDownloadAction(to))))
                .setHeader("Stažení").setTextAlign(ColumnTextAlign.CENTER).setWidth("90px").setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(to -> {
            String link = explorer.getDownloadLink(urlBase, to.name());
            return componentFactory.createInlineButton("QR", e -> {
                WebDialog qrDialog = new WebDialog("QR kód");
                qrDialog.setCloseOnEsc(true);
                qrDialog.setCloseOnOutsideClick(true);
                Image image = new Image(DownloadHandler.fromInputStream(de -> {
                    try {
                        File file = QRCode.from(link).file();
                        return new DownloadResponse(new FileInputStream(file), link, null, -1);
                    } catch (IOException ex) {
                        String msg = "Nezdařilo se vytvořit QR kód";
                        log.error(msg, ex);
                        throw new RuntimeException(msg);
                    }
                }), link);
                qrDialog.addComponent(image);
                qrDialog.setComponentAlignment(image, Alignment.CENTER);
                qrDialog.open();
            });
        })).setHeader("QR").setTextAlign(ColumnTextAlign.CENTER).setWidth("45px").setFlexGrow(0);

        grid.addColumn(new LocalDateTimeRenderer<>(FMItemTO::lastModified, "d. M. yyyy HH:mm")).setHeader("Upraveno")
                .setAutoWidth(true).setTextAlign(ColumnTextAlign.END).setSortProperty("lastModified");

        grid.addSelectionListener(e -> {
            Set<FMItemTO> value = e.getAllSelectedItems();
            statusLabel.setText(value.isEmpty() ? listFormatterValue : selectFormatter.format(value.size()));
        });

        grid.addItemClickListener(e -> {
            if (e.getClickCount() > 1) handleGridDblClick(e.getItem());
            else handleGridSingleClick(e.getItem(), e.isShiftKey());
        });

        HeaderRow filteringHeader = grid.appendHeaderRow();

        // Obsah
        UIUtils.addHeaderTextField(filteringHeader.getCell(nameColumn), e -> {
            filterName = e.getValue();
            populateGrid();
        });

        populateGrid();
    }

    private void handleGridDblClick(FMItemTO item) {
        if (item.directory()) handleGotoDirFromCurrentDirAction(item);
        else handleDownloadAction(item);
    }

    private void handleGridSingleClick(FMItemTO item, boolean shift) {
        if (shift) {
            if (grid.getSelectedItems().contains(item)) grid.deselect(item);
            else grid.select(item);
        } else {
            if (grid.getSelectedItems().size() == 1 && grid.getSelectedItems().iterator().next().equals(item)) {
                grid.deselect(item);
            } else {
                grid.deselectAll();
                grid.select(item);
            }
        }
    }

    private void populateGrid() {
        int size = explorer.listCount(filterName);
        grid.setDataProvider(DataProvider.fromFilteringCallbacks(
                q -> explorer.listing(filterName, q.getOffset(), q.getLimit(), q.getSortOrders()), q -> size));
        listFormatterValue = listFormatter.format(size);
        statusLabel.setText(listFormatterValue);
    }

    private void createButtonsLayout(Div layout) {
        ComponentFactory componentFactory = new ComponentFactory();

        Div buttonsLayout = componentFactory.createButtonLayout();
        buttonsLayout.add(componentFactory.createCreateDirButton(e -> handleNewDirectory()));
        buttonsLayout.add(componentFactory.createDownloadGridButton(this::handleDownloadAction, grid));
        buttonsLayout.add(componentFactory.createGridSetButton("Otevřít", VaadinIcon.FOLDER_OPEN.create(),
                items -> handleGotoDirFromCurrentDirAction(items.iterator().next()), grid,
                items -> items.size() == 1 && items.iterator().next().directory()));
        buttonsLayout.add(componentFactory.createGridSingleButton("Přejmenovat", VaadinIcon.PENCIL.create(),
                this::handleRenameAction, grid));
        buttonsLayout.add(componentFactory.createDeleteGridSetButton(this::handleDeleteAction, grid));

        layout.add(buttonsLayout);
    }

    private void handleNewDirectory() {
        new FileNameDialog(to -> {
            switch (explorer.createNewDir(to.getName())) {
                case SUCCESS:
                    populateGrid();
                    break;
                case ALREADY_EXISTS:
                    UIUtils.showWarning("Nezdařilo se vytvořit nový adresář - adresář s tímto jménem již existuje.");
                    break;
                case NOT_VALID:
                    UIUtils.showWarning(
                            "Nezdařilo se vytvořit nový adresář - cílové umístění adresáře se nachází mimo povolený " +
                                    "rozsah souborů k prohlížení.");
                    break;
                default:
                    UIUtils.showWarning("Nezdařilo se vytvořit nový adresář - došlo k systémové chybě.");
            }
        }).open();
    }

    private void handleDeleteAction(Set<FMItemTO> items) {
        FileProcessState overallResult = FileProcessState.SUCCESS;
        for (FMItemTO p : items) {
            FileProcessState partialResult = explorer.deleteFile(p.name());
            if (!partialResult.equals(FileProcessState.SUCCESS)) overallResult = partialResult;
        }
        if (!overallResult.equals(FileProcessState.SUCCESS))
            UIUtils.showWarning("Některé soubory se nezdařilo smazat.");
        populateGrid();
    }

    private void handleGotoDirFromCurrentDirAction(FMItemTO item) {
        String target = item.name();
        if ("..".equals(target)) {
            int lastDelimiter = parameter.lastIndexOf("/");
            if (lastDelimiter < 0) {
                target = "";
            } else {
                target = parameter.substring(0, lastDelimiter);
            }
        } else {
            if (StringUtils.isNotBlank(parameter)) target = parameter + "/" + target;
        }
        UI.getCurrent().navigate(FMPage.class, target);
    }

    private void handleRenameAction(final FMItemTO item) {
        new FileNameDialog(item.name(), to -> {
            switch (explorer.renameFile(item.name(), to.getName())) {
                case SUCCESS:
                    populateGrid();
                    break;
                case ALREADY_EXISTS:
                    UIUtils.showWarning("Přejmenování se nezdařilo - soubor s tímto názvem již existuje.");
                    break;
                case NOT_VALID:
                    UIUtils.showWarning(
                            "Přejmenování se nezdařilo - cílové umístění souboru se nachází mimo povolený rozsah " +
                                    "souborů k prohlížení.");
                    break;
                default:
                    UIUtils.showWarning("Přejmenování se nezdařilo - došlo k systémové chybě.");
                    break;
            }
        }).open();
    }

    private String getDownloadLink(FMItemTO item) {
        return urlBase + explorer.getDownloadLink(UIUtils.getContextPath(), item.name());
    }

    private void handleDownloadAction(FMItemTO item) {
        handleDownloadAction(Set.of(item));
    }

    private void handleDownloadAction(Set<FMItemTO> items) {
        FMItemTO item = items.iterator().next();
        if (items.size() == 1 && !item.directory()) {
            UI.getCurrent().getPage().open(getDownloadLink(item));
        } else {
            log.info("zipFMthread: {}", Thread.currentThread().threadId());
            progressDialog = new ProgressDialog();
            eventBus.subscribe(FMPage.this);
            explorer.zipFiles(items);
        }
    }

    @Handler
    protected void onProcessStart(final FMZipProcessStartEvent event) {
        progressDialog.runInUI(() -> {
            progressDialog.setTotal(event.steps());
            progressDialog.open();
        });
    }

    @Handler
    protected void onProcessProgress(FMZipProcessProgressEvent event) {
        progressDialog.runInUI(() -> progressDialog.indicateProgress(event.description()));
    }

    @Handler
    protected void onProcessResult(final FMZipProcessResultEvent event) {
        progressDialog.runInUI(() -> {
            if (progressDialog != null) progressDialog.close();
            if (event.success()) {
                String fileName = "fm_" + System.currentTimeMillis() + ".zip";
                new DownloadDialog("Komprese", () -> {
                    try {
                        return new DownloadResponse(Files.newInputStream(event.zipFile()), fileName, null, -1);
                    } catch (IOException e1) {
                        throw new RuntimeException(e1);
                    }
                }, d -> fmService.deleteZipFile(event.zipFile())).open();
            } else {
                UIUtils.showWarning(event.resultDetails());
            }
        });
        eventBus.unsubscribe(FMPage.this);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!fmSection.isVisibleForRoles(securityService.getCurrentUser().getRoles()))
            throw new GrassPageException(403);
    }
}