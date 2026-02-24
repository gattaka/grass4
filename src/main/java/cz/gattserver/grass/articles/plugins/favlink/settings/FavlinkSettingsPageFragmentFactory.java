package cz.gattserver.grass.articles.plugins.favlink.settings;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.grass.articles.plugins.favlink.strategies.CombinedFaviconObtainStrategy;
import cz.gattserver.grass.core.services.FileSystemService;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;
import cz.gattserver.grass.core.ui.util.UIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

public class FavlinkSettingsPageFragmentFactory extends AbstractPageFragmentFactory {

    private static final Logger logger = LoggerFactory.getLogger(FavlinkSettingsPageFragmentFactory.class);

    private String filterName;

    @Override
    public void createFragment(Div div) {
        String favlinkRootPath = SpringContextHelper.getContext().getEnvironment().getProperty("favlink.root.path");
        FileSystemService fileSystemService = SpringContextHelper.getBean(FileSystemService.class);

        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setSpacing(true);
        layout.setPadding(false);
        div.add(layout);

        ComponentFactory componentFactory = new ComponentFactory();

        Path path = fileSystemService.getFileSystem().getPath(favlinkRootPath);

        if (Files.exists(path)) {
            layout.add(new H2("Přehled uložených favicon"));
            Grid<Path> grid = new Grid<>();
            grid.setWidthFull();
            grid.setHeight(500,Unit.PIXELS);
            UIUtils.applyGrassDefaultStyle(grid);

            div.add(grid);

            grid.addColumn(new IconRenderer<>(p -> {
                Image img = new Image(DownloadHandler.fromInputStream(e -> {
                    try {
                        return new DownloadResponse(Files.newInputStream(p), p.getFileName().toString(), null, -1);
                    } catch (IOException ex) {
                        logger.error("Nezdařilo se otevřít favicon " + p.getFileName().toString(), ex);
                    }
                    return null;
                }), p.getFileName().toString());
                img.setWidth(16, Unit.PIXELS);
                img.setHeight(16, Unit.PIXELS);
                img.addClassName(UIUtils.GRID_ICON_CSS_CLASS);
                return img;
            }, c -> "")).setFlexGrow(0).setWidth("31px").setHeader("").setTextAlign(ColumnTextAlign.CENTER);

            Column<Path> nameColumn = grid.addColumn(new TextRenderer<>(p -> {
                String name = p.getFileName().toString();
                int dotIndex = name.lastIndexOf('.');
                return dotIndex > 0 ? name.substring(0, dotIndex) : name;
            })).setHeader("Název").setFlexGrow(100);

            grid.addColumn(new TextRenderer<>(p -> {
                String name = p.getFileName().toString();
                int dotIndex = name.lastIndexOf('.');
                return dotIndex > 0 ? name.substring(dotIndex) : "";
            })).setHeader("Typ").setWidth("50px").setFlexGrow(0);

            grid.addColumn(new TextRenderer<>(p -> formatSize(p))).setHeader("Velikost")
                    .setTextAlign(ColumnTextAlign.END).setFlexGrow(0).setWidth("70px");

            grid.addColumn(new ComponentRenderer<>(p -> componentFactory.createInlineButton("Smazat", be -> {
                new ConfirmDialog("Opravdu smazat favicon?", e -> {
                    try {
                        Files.delete(p);
                        populateGrid(grid, path);
                    } catch (IOException e1) {
                        logger.error("Nezdařilo se smazat favicon {}", p.getFileName().toString(), e);
                    }
                }).open();
            }))).setHeader("Smazat").setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);

            grid.addColumn(new ComponentRenderer<>(p -> componentFactory.createInlineButton("Přegenerovat", be -> {
                new ConfirmDialog("Opravdu přegenerovat favicon?", e -> {
                    try {
                        Files.delete(p);
                        String fileName = p.getFileName().toString();
                        String urlName = "http://" + fileName.substring(0, fileName.lastIndexOf('.'));
                        new CombinedFaviconObtainStrategy().obtainFaviconURL(urlName, UIUtils.getContextPath());
                        populateGrid(grid, path);
                    } catch (IOException e1) {
                        logger.error("Nezdařilo se smazat favicon {}", p.getFileName().toString(), e);
                    }
                }).open();
            }))).setHeader("Přegenerovat").setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);

            HeaderRow filteringHeader = grid.appendHeaderRow();

            // Obsah
            UIUtils.addHeaderTextField(filteringHeader.getCell(nameColumn), e -> {
                filterName = e.getValue();
                populateGrid(grid, path);
            });

            populateGrid(grid, path);
        }
    }

    private Stream<Path> createStream(Path path) {
        try {
            // zde se úmyslně nezavírá stream, protože se předává dál do vaadin
            return Files.list(path).filter(p -> {
                String name = p.getFileName().toString();
                int dotIndex = name.lastIndexOf('.');
                String targetValue = dotIndex > 0 ? name.substring(0, dotIndex) : name;
                return targetValue.contains(filterName == null ? "" : filterName);
            });
        } catch (IOException e) {
            logger.error("Nezdařilo se načíst favicon sobory z " + path.getFileName().toString(), e);
        }
        return new ArrayList<Path>().stream();
    }

    private long count(Path path) {
        try (Stream<Path> stream = Files.list(path)) {
            return stream.filter(p -> p.getFileName().toString().contains(filterName == null ? "" : filterName))
                    .count();
        } catch (Exception e) {
            logger.error("Nezdařilo se načíst galerie z " + path.getFileName().toString(), e);
            return 0;
        }
    }

    private void populateGrid(Grid<Path> grid, Path path) {
        FetchCallback<Path, Void> fetchCallback = q -> createStream(path).skip(q.getOffset()).limit(q.getLimit());
        CountCallback<Path, Void> countCallback = q -> (int) count(path);
        grid.setDataProvider(DataProvider.fromCallbacks(fetchCallback, countCallback));
    }

    private String formatSize(Path path) {
        try {
            return HumanBytesSizeFormatter.format(Files.size(path));
        } catch (IOException e) {
            logger.error("Nezdařilo se zjistit velikost souboru " + path.getFileName().toString(), e);
        }
        return "";
    }
}