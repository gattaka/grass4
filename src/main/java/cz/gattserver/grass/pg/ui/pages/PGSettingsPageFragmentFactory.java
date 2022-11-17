package cz.gattserver.grass.pg.ui.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.validator.StringLengthValidator;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.common.vaadin.Breakline;
import cz.gattserver.common.vaadin.LinkButton;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.common.vaadin.dialogs.InfoDialog;
import cz.gattserver.common.vaadin.dialogs.WarnDialog;
import cz.gattserver.grass.core.events.EventBus;
import cz.gattserver.grass.core.ui.components.button.SaveButton;
import cz.gattserver.grass.pg.config.PGConfiguration;
import cz.gattserver.grass.pg.events.impl.PGProcessProgressEvent;
import cz.gattserver.grass.pg.events.impl.PGProcessResultEvent;
import cz.gattserver.grass.pg.events.impl.PGProcessStartEvent;
import cz.gattserver.grass.pg.interfaces.PGSettingsItemTO;
import cz.gattserver.grass.pg.interfaces.PhotogalleryPayloadTO;
import cz.gattserver.grass.pg.interfaces.PhotogalleryRESTOverviewTO;
import cz.gattserver.grass.pg.interfaces.PhotogalleryTO;
import cz.gattserver.grass.pg.service.PGService;
import cz.gattserver.grass.core.services.FileSystemService;
import cz.gattserver.grass.core.ui.dialogs.ProgressDialog;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;
import net.engio.mbassy.listener.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class PGSettingsPageFragmentFactory extends AbstractPageFragmentFactory {

	private static final Logger logger = LoggerFactory.getLogger(PGSettingsPageFragmentFactory.class);

	@Autowired
	private PGService pgService;

	@Autowired
	private EventBus eventBus;

	@Autowired
	private FileSystemService fileSystemService;

	@Resource(name = "pgViewerPageFactory")
	private PageFactory photogalleryViewerPageFactory;

	private String filterName;

	private ProgressDialog progressIndicatorWindow;

	@Override
	public void createFragment(Div layout) {
		final PGConfiguration configuration = pgService.loadConfiguration();
		final FileSystem fs = fileSystemService.getFileSystem();

		layout.add(new H2("Nastavení fotogalerie"));

		Binder<PGConfiguration> binder = new Binder<>();

		// Název adresářů miniatur
		final TextField miniaturesDirField = new TextField("Název adresářů miniatur");
		miniaturesDirField.setValue(String.valueOf(configuration.getMiniaturesDir()));
		miniaturesDirField.setWidth("300px");
		layout.add(miniaturesDirField);

		binder.forField(miniaturesDirField).asRequired("Nesmí být prázdné")
				.withValidator(new StringLengthValidator("Neodpovídá povolené délce", 1, 1024))
				.bind(PGConfiguration::getMiniaturesDir, PGConfiguration::setMiniaturesDir);

		layout.add(new Breakline());

		// Kořenový adresář fotogalerií
		final TextField rootDirField = new TextField("Kořenový adresář fotogalerií");
		rootDirField.setValue(String.valueOf(configuration.getRootDir()));
		rootDirField.setWidth("300px");
		layout.add(rootDirField);

		layout.add(new Breakline());

		binder.forField(rootDirField).asRequired("Kořenový adresář je povinný").withValidator((val, c) -> {
			try {
				return Files.exists(fs.getPath(val)) ? ValidationResult.ok()
						: ValidationResult.error("Kořenový adresář musí existovat");
			} catch (InvalidPathException e) {
				return ValidationResult.error("Neplatná cesta");
			}
		}).bind(PGConfiguration::getRootDir, PGConfiguration::setRootDir);

		// Save tlačítko
		ButtonLayout btnLayout = new ButtonLayout();
		SaveButton saveButton = new SaveButton(event -> {
			if (binder.validate().isOk()) {
				configuration.setRootDir(rootDirField.getValue());
				configuration.setMiniaturesDir(miniaturesDirField.getValue());
				pgService.storeConfiguration(configuration);
				UI.getCurrent().getPage().reload();
			}
		});
		binder.addValueChangeListener(l -> saveButton.setEnabled(binder.isValid()));
		btnLayout.add(saveButton);
		layout.add(btnLayout);

		Path path = fileSystemService.getFileSystem().getPath(configuration.getRootDir());

		if (Files.exists(path)) {
			layout.add(new H2("Přehled adresářů"));

			Grid<PGSettingsItemTO> grid = new Grid<>();
			UIUtils.applyGrassDefaultStyle(grid);
			grid.setWidthFull();
			grid.setHeight("500px");

			layout.add(grid);

			Column<PGSettingsItemTO> nameColumn = grid
					.addColumn(new TextRenderer<>(to -> to.getPath().getFileName().toString())).setHeader("Název")
					.setFlexGrow(80);

			grid.addColumn(new ComponentRenderer<Component, PGSettingsItemTO>(to -> {
				if (to.getOverviewTO() == null) {
					return new Text("Nepoužívá se");
				} else {
					Anchor a = new Anchor(
							UIUtils.getPageURL(photogalleryViewerPageFactory, URLIdentifierUtils
									.createURLIdentifier(to.getOverviewTO().getId(), to.getOverviewTO().getName())),
							"Odkaz");
					a.setTarget("_blank");
					return a;
				}
			})).setHeader("Odkaz");

			grid.addColumn(p -> p.getSize() == null ? "N/A" : HumanBytesSizeFormatter.format(p.getSize()))
					.setHeader("Velikost").setTextAlign(ColumnTextAlign.END).setWidth("80px").setFlexGrow(0)
					.setSortable(true);

			grid.addColumn(p -> p.getFilesCount() == null ? "N/A" : p.getFilesCount()).setHeader("Soubory")
					.setTextAlign(ColumnTextAlign.END).setWidth("80px").setFlexGrow(0).setSortable(true);

			SimpleDateFormat sdf = new SimpleDateFormat("d.M.yyyy");
			grid.addColumn(p -> sdf.format(p.getDate())).setHeader("Datum").setTextAlign(ColumnTextAlign.END)
					.setWidth("70px").setFlexGrow(0).setSortable(true);

			grid.addColumn(new ComponentRenderer<Button, PGSettingsItemTO>(item -> {
				Button button = new LinkButton("Přegenerovat", be -> {
					new ConfirmDialog("Opravdu přegenerovat galerii?", e -> {
						UUID operationId = UUID.randomUUID();

						PhotogalleryTO to = pgService.getPhotogalleryForDetail(item.getOverviewTO().getId());
						progressIndicatorWindow = new ProgressDialog();

						eventBus.subscribe(PGSettingsPageFragmentFactory.this);

						PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO(to.getContentNode().getName(),
								to.getPhotogalleryPath(), to.getContentNode().getContentTagsAsStrings(),
								to.getContentNode().isPublicated(), true);
						pgService.modifyPhotogallery(operationId, to.getId(), payloadTO, LocalDateTime.now());
					}).open();
				});
				button.setVisible(item.getOverviewTO() != null);
				return button;
			})).setHeader("Přegenerování").setTextAlign(ColumnTextAlign.CENTER);

			grid.addColumn(new ComponentRenderer<Button, PGSettingsItemTO>(
					item -> new LinkButton(item.getOverviewTO() == null ? "Smazat adresář" : "Smazat galerii", be -> {
						String caption = item.getOverviewTO() == null ? "Opravdu smazat adresář?"
								: "Opravdu smazat galerii (záznam v kategorii a data v adresáři)?";
						new ConfirmDialog(caption, e -> deleteItem(item, path, grid)).open();
					}))).setHeader("Smazání").setWidth("110px").setFlexGrow(0).setTextAlign(ColumnTextAlign.CENTER);

			HeaderRow filteringHeader = grid.appendHeaderRow();

			// Obsah
			UIUtils.addHeaderTextField(filteringHeader.getCell(nameColumn), e -> {
				filterName = e.getValue();
				populateGrid(grid, path);
			});

			populateGrid(grid, path);
		}
	}

	protected void deleteItem(PGSettingsItemTO item, Path path, Grid<PGSettingsItemTO> grid) {
		if (item.getOverviewTO() == null) {
			try (Stream<Path> s = Files.walk(item.getPath())) {
				s.sorted(Comparator.reverseOrder()).forEach(p -> {
					try {
						logger.info("Zkouším mazat '" + p.getFileName().toString() + "'");
						Files.delete(p);
					} catch (IOException e2) {
						logger.error("Nezdařilo se smazat adresář " + p.getFileName().toString(), e2);
					}
				});
			} catch (IOException e1) {
				logger.error("Nezdařilo se smazat adresář " + item.getPath().getFileName().toString(), e1);
				WarnDialog warnSubwindow = new WarnDialog(
						"Při mazání adresáře došlo k chybě (" + e1.getMessage() + ")");
				warnSubwindow.open();
			}
		} else {
			if (!pgService.deletePhotogallery(item.getOverviewTO().getId())) {
				WarnDialog warnSubwindow = new WarnDialog("Při mazání galerie se nezdařilo smazat některé soubory.");
				warnSubwindow.open();
			}
		}
		populateGrid(grid, path);
	}

	@Handler
	protected void onProcessStart(final PGProcessStartEvent event) {
		progressIndicatorWindow.runInUI(() -> {
			progressIndicatorWindow.setTotal(event.getCountOfStepsToDo());
			progressIndicatorWindow.open();
		});
	}

	@Handler
	protected void onProcessProgress(PGProcessProgressEvent event) {
		progressIndicatorWindow.runInUI(() -> progressIndicatorWindow.indicateProgress(event.getStepDescription()));
	}

	@Handler
	protected void onProcessResult(final PGProcessResultEvent event) {
		progressIndicatorWindow.runInUI(() -> {
			if (progressIndicatorWindow != null)
				progressIndicatorWindow.close();
			if (event.isSuccess())
				new InfoDialog("Přegenerování dopladlo úspěšně").open();
			else
				new WarnDialog("Při přegenerování došlo k chybám: ", event.getResultDetails()).open();
		});
		eventBus.unsubscribe(PGSettingsPageFragmentFactory.this);
	}

	private Long getFileSize(Path path) {
		try {
			if (!Files.isDirectory(path))
				return Files.size(path);
			try (Stream<Path> stream = Files.list(path)) {
				Long sum = 0L;
				for (Iterator<Path> it = stream.iterator(); it.hasNext();)
					sum += getFileSize(it.next());
				return sum;
			}
		} catch (Exception e) {
			logger.error("Nezdařilo se zjistit velikost souboru " + path.getFileName().toString(), e);
			return null;
		}
	}

	private PGSettingsItemTO createItem(Path path) {
		PhotogalleryRESTOverviewTO to = pgService.getPhotogalleryByDirectory(path.getFileName().toString());
		Long size = getFileSize(path);
		Long filesCount = null;
		Date date = null;
		try {
			FileTime fileTime = Files.getLastModifiedTime(path);
			date = Date.from(fileTime.toInstant());
		} catch (IOException e) {
			logger.warn("Nezdařilo se zjistit datum adresáře " + path.getFileName().toString(), e);
		}
		try (Stream<Path> stream = Files.list(path)) {
			filesCount = stream.count();
		} catch (IOException e) {
			logger.error("Nezdařilo se zjistit počet položek adresáře " + path.getFileName().toString(), e);
		}
		return new PGSettingsItemTO(path, to, size, filesCount, date);
	}

	private Stream<PGSettingsItemTO> createStream(Path path) {
		try {
			// zde se úmyslně nezavírá stream, protože se předává dál do vaadin
			return Files.list(path)
					.filter(p -> p.getFileName().toString().contains(filterName == null ? "" : filterName))
					.map(this::createItem);
		} catch (IOException e) {
			logger.error("Nezdařilo se načíst galerie z " + path.getFileName().toString(), e);
			return new ArrayList<PGSettingsItemTO>().stream();
		}
	}

	private long count(Path path) {
		try (Stream<Path> stream = Files.list(path)) {
			return stream.filter(p -> p.getFileName().toString().contains(filterName == null ? "" : filterName))
					.count();
		} catch (IOException e) {
			logger.error("Nezdařilo se načíst galerie z " + path.getFileName().toString(), e);
			return 0;
		}
	}

	private void populateGrid(Grid<PGSettingsItemTO> grid, Path path) {
		FetchCallback<PGSettingsItemTO, Void> fetchCallback = q -> createStream(path).skip(q.getOffset())
				.limit(q.getLimit())
				.sorted(q.getSortingComparator().orElse(Comparator.<PGSettingsItemTO>naturalOrder()));
		CountCallback<PGSettingsItemTO, Void> countCallback = q -> (int) count(path);
		grid.setDataProvider(DataProvider.fromCallbacks(fetchCallback, countCallback));
	}

}
