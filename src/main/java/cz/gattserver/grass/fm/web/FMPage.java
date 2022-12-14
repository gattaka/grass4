package cz.gattserver.grass.fm.web;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.page.History;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServletRequest;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.util.CZAmountFormatter;
import cz.gattserver.common.vaadin.HtmlDiv;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.common.vaadin.LinkButton;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.core.events.EventBus;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.services.FileSystemService;
import cz.gattserver.grass.core.ui.components.Breadcrumb;
import cz.gattserver.grass.core.ui.components.button.CreateGridButton;
import cz.gattserver.grass.core.ui.components.button.DeleteGridButton;
import cz.gattserver.grass.core.ui.components.button.GridButton;
import cz.gattserver.grass.core.ui.components.button.ModifyGridButton;
import cz.gattserver.grass.core.ui.dialogs.ProgressDialog;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass.core.ui.pages.template.OneColumnPage;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.GrassMultiFileBuffer;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.fm.FMExplorer;
import cz.gattserver.grass.fm.FMSection;
import cz.gattserver.grass.fm.FileProcessState;
import cz.gattserver.grass.fm.events.FMZipProcessProgressEvent;
import cz.gattserver.grass.fm.events.FMZipProcessResultEvent;
import cz.gattserver.grass.fm.events.FMZipProcessStartEvent;
import cz.gattserver.grass.fm.interfaces.FMItemTO;
import cz.gattserver.grass.fm.service.FMService;
import net.engio.mbassy.listener.Handler;
import net.glxn.qrgen.javase.QRCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Route("fm")
@PageTitle("Spr??vce soubor??")
public class FMPage extends OneColumnPage implements HasUrlParameter<String> {

	private static final long serialVersionUID = -5884444775720831930L;

	private static final Logger logger = LoggerFactory.getLogger(FMPage.class);

	@Resource(name = "fmPageFactory")
	private PageFactory fmPageFactory;

	@Autowired
	private FileSystemService fileSystemService;

	@Autowired
	private EventBus eventBus;

	@Autowired
	private FMService fmService;

	private ProgressDialog progressIndicatorWindow;

	private final CZAmountFormatter selectFormatter;
	private final CZAmountFormatter listFormatter;
	private String listFormatterValue;

	private FileSystem fileSystem;

	private TextField filterNameField;
	private String filterName;

	/**
	 * FM Explorer s pot??ebn??mi daty a metodami pro proch??zen?? soubor??
	 */
	private FMExplorer explorer;

	/**
	 * Files table
	 */
	private Grid<FMItemTO> grid;

	/**
	 * Status label, vybran?? soubory apod.
	 */
	private Div statusLabel;

	/**
	 * Breadcrumb
	 */
	private Breadcrumb breadcrumb;

	private String urlBase;
	private String parameter;

	@Override
	public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
		this.parameter = parameter;
		init();
	}

	public FMPage() {
		if (!SpringContextHelper.getBean(FMSection.class).isVisibleForRoles(getUser().getRoles()))
			throw new GrassPageException(403);
		selectFormatter = new CZAmountFormatter("Vybr??n %d soubor", "Vybr??ny %d soubory", "Vybr??no %d soubor??");
		listFormatter = new CZAmountFormatter("Zobrazen %d soubor", "Zobrazeny %d soubory", "Zobrazeno %d soubor??");
	}

	@Override
	protected void createColumnContent(Div layout) {

		VaadinRequest vaadinRequest = VaadinRequest.getCurrent();
		VaadinServletRequest vaadinServletRequest = (VaadinServletRequest) vaadinRequest;

		// nap??. /web/fm/Android
		String requestURI = ((VaadinServletRequest) vaadinRequest).getRequestURI();

		// nap??. http://localhost:8180/web/fm/Android
		String fullURL = vaadinServletRequest.getRequestURL().toString();

		// nap??. http://localhost:8180
		urlBase = fullURL.substring(0, fullURL.length() - requestURI.length());

		statusLabel = new Div();
		statusLabel.getStyle().set("border", "1px solid hsl(220, 14%, 88%)").set("padding", "4px 10px")
				.set("background", "white").set("font-size", "12px").set("border-top", "none")
				.set("color", "hsl(220, 14%, 61%)");
		breadcrumb = new Breadcrumb();

		fileSystem = fileSystemService.getFileSystem();

		String path = parameter;

		explorer = new FMExplorer(fileSystem);
		FileProcessState result = explorer.goToDir(path);

		switch (result) {
		case SUCCESS:
			// ??sp??ch - pokra??ujeme
			History history = UI.getCurrent().getPage().getHistory();
			history.setHistoryStateChangeHandler(e -> {
				String url = urlBase + UIUtils.getContextPath() + "/" + e.getLocation().getPath();
				if (FileProcessState.SUCCESS
						.equals(explorer.goToDirByURL(UIUtils.getContextPath(), fmPageFactory.getPageName(), url))) {
					refreshView();
				}
			});
			updatePageState();
			break;
		case MISSING:
			UIUtils.showWarning("C??l neexistuje - vrac??m se do ko??enov??ho adres????e");
			break;
		case NOT_VALID:
			UIUtils.showWarning(
					"C??l se nach??z?? mimo povolen?? rozsah soubor?? k prohl????en?? - vrac??m se do ko??enov??ho adres????e");
			break;
		case SYSTEM_ERROR:
			UIUtils.showWarning("Z c??le nelze ????st - vrac??m se do ko??enov??ho adres????e");
			break;
		default:
			UIUtils.showWarning("Nezn??m?? chyba - vrac??m se do ko??enov??ho adres????e");
			break;
		}

		createBreadcrumb(layout);
		createFilesGrid(layout);

		layout.add(statusLabel);

		createButtonsLayout(layout);

		GrassMultiFileBuffer buffer = new GrassMultiFileBuffer();

		Upload upload = new Upload(buffer);
		upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		upload.addSucceededListener(event -> {
			switch (explorer.saveFile(buffer.getInputStream(event.getFileName()), event.getFileName())) {
			case SUCCESS:
				// refresh
				populateGrid();
				break;
			case ALREADY_EXISTS:
				UIUtils.showWarning("Soubor '" + event.getFileName()
						+ "' nebylo mo??n?? ulo??it - soubor s t??mto n??zvem ji?? existuje.");
				break;
			case NOT_VALID:
				UIUtils.showWarning("Soubor '" + event.getFileName()
						+ "' nebylo mo??n?? ulo??it - c??lov?? um??st??n?? souboru se nach??z?? mimo povolen?? rozsah soubor?? k prohl????en??.");
				break;
			default:
				UIUtils.showWarning(
						"Soubor '" + event.getFileName() + "' nebylo mo??n?? ulo??it - do??lo k syst??mov?? chyb??.");
			}
		});
		layout.add(upload);
	}

	private void createBreadcrumb(Div layout) {
		layout.add(breadcrumb);
		populateBreadcrumb();
	}

	private void populateBreadcrumb() {
		// pokud zjist??m, ??e cesta neodpov??d??, vyhod??m 302 (p??esm??rov??n??) na
		// aktu??ln?? polohu c??lov?? kategorie
		List<Breadcrumb.BreadcrumbElement> breadcrumbElements = new ArrayList<>();
		for (FMItemTO c : explorer.getBreadcrumbChunks())
			breadcrumbElements
					.add(new Breadcrumb.BreadcrumbElement(c.getName(), getPageURL(fmPageFactory, c.getPathFromFMRoot())));
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

		grid.addColumn(new IconRenderer<FMItemTO>(to -> {
			Image img = new Image(to.isDirectory() ? ImageIcon.FOLDER_16_ICON.createResource()
					: ImageIcon.DOCUMENT_16_ICON.createResource(), "");
			img.addClassName(UIUtils.GRID_ICON_CSS_CLASS);
			return img;
		}, to -> "")).setFlexGrow(0).setWidth("36px").setHeader("").setTextAlign(ColumnTextAlign.CENTER);

		Column<FMItemTO> nameColumn = grid.addColumn(FMItemTO::getName).setHeader("N??zev").setFlexGrow(100)
				.setSortProperty("name");

		grid.addColumn(FMItemTO::getSize).setHeader("Velikost").setTextAlign(ColumnTextAlign.END).setWidth("100px")
				.setFlexGrow(0).setSortProperty("size");

		grid.addColumn(new ComponentRenderer<Button, FMItemTO>(to -> {
			Button button = new LinkButton("URL", e -> {
				Dialog ww = new Dialog();
				String id = UUID.randomUUID().toString();
				String checkId = "check-" + id;
				HtmlDiv text = new HtmlDiv("<input style=\"width: inherit\" id=\"" + id + "\" value=\""
						+ getDownloadLink(to) + "\"/>" + "<br/><span id=\"" + checkId + "\" onload=''></span>");
				text.getStyle().set("width", "400px").set("text-align", "center").set("line-height", "30px")
						.set("color", "dodgerblue").set("font-weight", "bold");
				ww.add(text);
				ww.open();

				// mus?? m??t m??rn?? timeout, jinak bude referencovat je??t??
				// nevykreslen?? element a dotaz podle ID bude null
				UI.getCurrent().getPage()
						.executeJs("setTimeout(function(){" + "document.getElementById(\"" + id + "\").select(); "
								+ "if (document.execCommand(\"copy\")) { " + "document.getElementById(\"" + checkId
								+ "\").innerHTML = \"URL zkop??rov??no do schr??nky\";" + "}" + "},10)");
			});
			button.setVisible(!to.isDirectory());
			return button;
		})).setHeader("URL").setTextAlign(ColumnTextAlign.CENTER).setWidth("50px").setFlexGrow(0);

		grid.addColumn(new ComponentRenderer<Button, FMItemTO>(
				to -> new LinkButton("St??hnout", e -> handleDownloadAction(to)))).setHeader("Sta??en??")
				.setTextAlign(ColumnTextAlign.CENTER).setWidth("90px").setFlexGrow(0);

		grid.addColumn(new ComponentRenderer<Button, FMItemTO>(to -> {
			String link = explorer.getDownloadLink(urlBase, to.getName());
			Button button = new LinkButton("QR", e -> {
				WebDialog ww = new WebDialog();
				ww.setCloseOnEsc(true);
				ww.setCloseOnOutsideClick(true);
				Image image = new Image(new StreamResource(to.getName(), () -> {
					try {
						File file = QRCode.from(link).file();
						return new FileInputStream(file);
					} catch (IOException ex) {
						ex.printStackTrace();
						return null;
					}
				}), link);
				ww.addComponent(image);
				ww.setComponentAlignment(image, Alignment.CENTER);
				ww.open();
			});
			return button;
		})).setHeader("QR").setTextAlign(ColumnTextAlign.CENTER).setWidth("45px").setFlexGrow(0);

		grid.addColumn(new LocalDateTimeRenderer<>(FMItemTO::getLastModified, "d.MM.yyyy HH:mm")).setHeader("Upraveno")
				.setAutoWidth(true).setTextAlign(ColumnTextAlign.END).setSortProperty("lastModified");

		grid.addSelectionListener(e -> {
			Set<FMItemTO> value = e.getAllSelectedItems();
			statusLabel.setText(value.isEmpty() ? listFormatterValue : selectFormatter.format(value.size()));
		});

		grid.addItemClickListener(e -> {
			if (e.getClickCount() > 1)
				handleGridDblClick(e.getItem());
			else
				handleGridSingleClick(e.getItem(), e.isShiftKey());
		});

		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Obsah
		filterNameField = UIUtils.addHeaderTextField(filteringHeader.getCell(nameColumn), e -> {
			filterName = e.getValue();
			populateGrid();
		});

		populateGrid();
	}

	private void handleGridDblClick(FMItemTO item) {
		if (item.isDirectory())
			handleGotoDirFromCurrentDirAction(item);
		else
			handleDownloadAction(item);
	}

	private void handleGridSingleClick(FMItemTO item, boolean shift) {
		if (shift) {
			if (grid.getSelectedItems().contains(item))
				grid.deselect(item);
			else
				grid.select(item);
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
		ButtonLayout buttonsLayout = new ButtonLayout();
		buttonsLayout.add(new CreateGridButton("Vytvo??it nov?? adres????", e -> handleNewDirectory()));

		GridButton<FMItemTO> downloadButton = new GridButton<>("St??hnout", this::handleDownloadAction, grid);
		downloadButton.setIcon(new Image(ImageIcon.DOWN_16_ICON.createResource(), "St??hnout"));
		buttonsLayout.add(downloadButton);

		GridButton<FMItemTO> gotoButton = new GridButton<>("P??ej??t",
				items -> handleGotoDirFromCurrentDirAction(items.iterator().next()), grid);
		gotoButton.setIcon(new Image(ImageIcon.RIGHT_16_ICON.createResource(), "P??ej??t"));
		gotoButton.setEnableResolver(items -> items.size() == 1 && items.iterator().next().isDirectory());

		buttonsLayout.add(gotoButton);
		buttonsLayout.add(new ModifyGridButton<FMItemTO>("P??ejmenovat", this::handleRenameAction, grid));
		buttonsLayout.add(new DeleteGridButton<FMItemTO>("Smazat", this::handleDeleteAction, grid));

		layout.add(buttonsLayout);
	}

	private void handleNewDirectory() {
		new FileNameDialog((s, w) -> {
			switch (explorer.createNewDir(s.getName())) {
			case SUCCESS:
				populateGrid();
				w.close();
				break;
			case ALREADY_EXISTS:
				UIUtils.showWarning("Nezda??ilo se vytvo??it nov?? adres???? - adres???? s t??mto jm??nem ji?? existuje.");
				break;
			case NOT_VALID:
				UIUtils.showWarning(
						"Nezda??ilo se vytvo??it nov?? adres???? - c??lov?? um??st??n?? adres????e se nach??z?? mimo povolen?? rozsah soubor?? k prohl????en??.");
				break;
			default:
				UIUtils.showWarning("Nezda??ilo se vytvo??it nov?? adres???? - do??lo k syst??mov?? chyb??.");
			}
		}).open();
	}

	private void handleDeleteAction(Set<FMItemTO> items) {
		FileProcessState overallResult = FileProcessState.SUCCESS;
		for (FMItemTO p : items) {
			FileProcessState partialResult = explorer.deleteFile(p.getName());
			if (!partialResult.equals(FileProcessState.SUCCESS))
				overallResult = partialResult;
		}
		if (!overallResult.equals(FileProcessState.SUCCESS))
			UIUtils.showWarning("N??kter?? soubory se nezda??ilo smazat.");
		populateGrid();
	}

	private void handleGotoDirFromCurrentDirAction(FMItemTO item) {
		if (FileProcessState.SUCCESS.equals(explorer.goToDirFromCurrentDir(item.getName()))) {
			filterNameField.setValue("");
			refreshView();
			updatePageState();
		}
	}

	private void refreshView() {
		populateBreadcrumb();
		populateGrid();
	}

	private void handleRenameAction(final FMItemTO item) {
		new FileNameDialog(item, (s, w) -> {
			switch (explorer.renameFile(item.getName(), s.getName())) {
			case SUCCESS:
				populateGrid();
				w.close();
				break;
			case ALREADY_EXISTS:
				UIUtils.showWarning("P??ejmenov??n?? se nezda??ilo - soubor s t??mto n??zvem ji?? existuje.");
				break;
			case NOT_VALID:
				UIUtils.showWarning(
						"P??ejmenov??n?? se nezda??ilo - c??lov?? um??st??n?? souboru se nach??z?? mimo povolen?? rozsah soubor?? k prohl????en??.");
				break;
			default:
				UIUtils.showWarning("P??ejmenov??n?? se nezda??ilo - do??lo k syst??mov?? chyb??.");
				break;
			}
		}).open();
	}

	private String getDownloadLink(FMItemTO item) {
		return urlBase + explorer.getDownloadLink(UIUtils.getContextPath(), item.getName());
	}

	private void handleDownloadAction(FMItemTO item) {
		UI.getCurrent().getPage().open(getDownloadLink(item));
	}

	private void handleDownloadAction(Set<FMItemTO> items) {
		FMItemTO item = items.iterator().next();
		if (items.size() == 1 && !item.isDirectory()) {
			handleDownloadAction(item);
		} else {
			logger.info("zipFMthread: {}", Thread.currentThread().getId());
			progressIndicatorWindow = new ProgressDialog();
			eventBus.subscribe(FMPage.this);
			explorer.zipFiles(items);
		}
	}

	@Handler
	protected void onProcessStart(final FMZipProcessStartEvent event) {
		progressIndicatorWindow.runInUI(() -> {
			progressIndicatorWindow.setTotal(event.getCountOfStepsToDo());
			progressIndicatorWindow.open();
		});
	}

	@Handler
	protected void onProcessProgress(FMZipProcessProgressEvent event) {
		progressIndicatorWindow.runInUI(() -> progressIndicatorWindow.indicateProgress(event.getStepDescription()));
	}

	@Handler
	protected void onProcessResult(final FMZipProcessResultEvent event) {
		progressIndicatorWindow.runInUI(() -> {
			if (progressIndicatorWindow != null)
				progressIndicatorWindow.close();

			if (event.isSuccess()) {
				WebDialog win = new WebDialog();
				win.addDialogCloseActionListener(e -> fmService.deleteZipFile(event.getZipFile()));
				Anchor link = new Anchor(new StreamResource("fm_" + System.currentTimeMillis() + ".zip", () -> {
					try {
						return Files.newInputStream(event.getZipFile());
					} catch (IOException e1) {
						e1.printStackTrace();
						return null;
					}
				}), "St??hnout ZIP souboru");
				link.setTarget("_blank");
				win.addComponent(link, Alignment.CENTER);

				Button proceedButton = new Button("Zav????t", e -> win.close());
				win.addComponent(proceedButton, Alignment.CENTER);

				win.open();
			} else {
				UIUtils.showWarning(event.getResultDetails());
			}
		});
		eventBus.unsubscribe(FMPage.this);
	}

	private void updatePageState() {
		// Tohle je pot??eba pushovat cel?? znova od ko??ene webu, proto??e jakmile
		// se ve stavu objev?? "/", je to br??no jako nov?? ko??en a dal???? pushState
		// nahrad?? pouze posledn?? chunk
		// TODO
		String currentURL = explorer.getCurrentURL("", fmPageFactory.getPageName());
		History history = UI.getCurrent().getPage().getHistory();
		history.pushState(null, currentURL.substring(1));
	}

}
