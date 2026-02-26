package cz.gattserver.grass.songs.ui;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.internal.AllowInert;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.streams.UploadHandler;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.pages.MainView;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass.hw.ui.pages.HWItemPage;
import cz.gattserver.grass.hw.ui.pages.HWItemsPage;
import cz.gattserver.grass.hw.ui.pages.HWTypesPage;
import cz.gattserver.grass.songs.SongsRole;
import cz.gattserver.grass.songs.service.SongsService;
import cz.gattserver.grass.songs.interfaces.SongOverviewTO;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.Serial;
import java.util.*;

@PageTitle("Zpěvník")
@Route(value = "songs", layout = MainView.class)
public class SongsPage extends Div implements HasUrlParameter<String> {

    @Serial
    private static final long serialVersionUID = 5455430288309036141L;

    public static final String SONG_ID_PARAM = "id";
    public static final String NAME_PARAM = "name";
    public static final String AUTHOR_PARAM = "author";
    public static final String YEAR_PARAM = "year";
    public static final String SORT_KEY_PARAM = "sort";
    public static final String SORT_DIRECTION_PARAM = "sortdir";

    private final SongsService songsService;
    private final SecurityService securityService;

    private final ComponentFactory componentFactory;

    private Grid<SongOverviewTO> grid;

    private SongOverviewTO filterTO;

    private final Map<Long, Integer> indexMap = new HashMap<>();

    private Div pageLayout;

    public SongsPage(SongsService songsService, SecurityService securityService) {
        this.songsService = songsService;
        this.securityService = securityService;
        componentFactory = new ComponentFactory();
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        removeAll();

        Div layout = componentFactory.createOneColumnLayout();
        add(layout);

        layout.add(new Menu());

        pageLayout = new Div();
        pageLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(pageLayout);

        filterTO = new SongOverviewTO();
        UserInfoTO user = securityService.getCurrentUser();
        filterTO.setPublicated(user.isAdmin() ? null : true);

        grid = new Grid<>() {
            @Serial
            private static final long serialVersionUID = -6131910831955302272L;

            @AllowInert
            @ClientCallable
            private void scrollToId(Long id) {
                // indexMap je přepočetní mapa mezi identifikátory entit a jejich indexem (řádkem) v gridu
                Integer index = indexMap.get(id);
                if (index == null) return;
                grid.scrollToIndex(index);
            }
        };
        grid.setMultiSort(false);
        UIUtils.applyGrassDefaultStyle(grid);

        Grid.Column<SongOverviewTO> idColumn =
                grid.addColumn(SongOverviewTO::getId).setHeader("Id").setSortable(true).setKey("id").setWidth("50px")
                        .setFlexGrow(0);
        Grid.Column<SongOverviewTO> nazevColumn =
                grid.addColumn(new ComponentRenderer<>(to -> createSongLink(to))).setHeader("Název").setSortable(true)
                        .setKey("name");
        Grid.Column<SongOverviewTO> authorColumn =
                grid.addColumn(SongOverviewTO::getAuthor).setHeader("Autor").setSortable(true).setKey("author")
                        .setWidth("250px").setFlexGrow(0);
        Grid.Column<SongOverviewTO> yearColumn =
                grid.addColumn(SongOverviewTO::getYear).setHeader("Rok").setWidth("60px").setSortable(true)
                        .setKey("year").setFlexGrow(0);
        grid.setWidthFull();
        grid.setHeight("600px");
        grid.setDataProvider(DataProvider.fromCallbacks(q -> {
            indexMap.clear();
            List<Long> ids = songsService.getSongsIds(filterTO, q.getSortOrders());
            for (int i = 0; i < ids.size(); i++)
                indexMap.put(ids.get(i), i);
            return songsService.getSongs(filterTO, q.getOffset(), q.getLimit(), q.getSortOrders()).stream();
        }, q -> songsService.getSongsCount(filterTO)));
        layout.add(grid);

        HeaderRow filteringHeader = grid.appendHeaderRow();

        // Název
        TextField nameField = UIUtils.addHeaderTextField(filteringHeader.getCell(nazevColumn), e -> {
            filterTO.setName(e.getValue());
            populate();
        });

        // Autor
        TextField authorField = UIUtils.addHeaderTextField(filteringHeader.getCell(authorColumn), e -> {
            filterTO.setAuthor(e.getValue());
            populate();
        });

        // Rok
        TextField yearField = UIUtils.addHeaderTextField(filteringHeader.getCell(yearColumn), e -> {
            filterTO.setYear(StringUtils.isBlank(e.getValue()) ? null : Integer.valueOf(e.getValue()));
            populate();
        });

        populate();

        QueryParameters params = event.getLocation().getQueryParameters();
        Map<String, List<String>> parametersMap = params.getParameters();
        if (parametersMap.containsKey(SONG_ID_PARAM))
            selectAndScroll(Long.parseLong(parametersMap.get(SONG_ID_PARAM).getFirst()));
        if (parametersMap.containsKey(NAME_PARAM)) nameField.setValue(parametersMap.get(NAME_PARAM).getFirst());
        if (parametersMap.containsKey(AUTHOR_PARAM)) authorField.setValue(parametersMap.get(AUTHOR_PARAM).getFirst());
        if (parametersMap.containsKey(YEAR_PARAM)) yearField.setValue(parametersMap.get(YEAR_PARAM).getFirst());
        if (parametersMap.containsKey(SORT_KEY_PARAM)) {
            List<GridSortOrder<SongOverviewTO>> sort = new ArrayList<>();
            SortDirection sortDirection = SortDirection.ASCENDING;
            if (parametersMap.containsKey(SORT_DIRECTION_PARAM))
                sortDirection = SortDirection.valueOf(parametersMap.get(SORT_DIRECTION_PARAM).getFirst());
            sort.add(new GridSortOrder<>(switch (parametersMap.get(SORT_KEY_PARAM).getFirst()) {
                case "name" -> nazevColumn;
                case "author" -> authorColumn;
                case "year" -> yearColumn;
                default -> idColumn;
            }, sortDirection));
            grid.sort(sort);
        }

        Upload upload = new Upload(UploadHandler.toTempFile((metadata, file) -> {
            songsService.importSong(new FileInputStream(file), metadata.fileName());
            populate();
        }));
        upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        upload.setAcceptedFileTypes("text/plain");
        layout.add(upload);
        upload.setVisible(securityService.getCurrentUser().getRoles().contains(SongsRole.SONGS_EDITOR));

        Div btnLayout = componentFactory.createButtonLayout();
        btnLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(btnLayout);

        btnLayout.setVisible(securityService.getCurrentUser().getRoles().contains(SongsRole.SONGS_EDITOR));

        btnLayout.add(componentFactory.createCreateButton(e -> new SongDialog(to -> {
            to = songsService.saveSong(to);
            populate();
            selectSong(to, true);
        }).open()));
        btnLayout.add(componentFactory.createEditGridButton(
                e -> new SongDialog(songsService.getSongById(grid.getSelectedItems().iterator().next().getId()), to -> {
                    to = songsService.saveSong(to);
                    populate();
                    selectSong(to, false);
                }).open(), grid));

        btnLayout.add(componentFactory.createDeleteGridSetButton(items -> {
            for (SongOverviewTO s : items)
                songsService.deleteSong(s.getId());
            populate();
            selectSong(null, false);
        }, grid));
    }

    private Anchor createSongLink(SongOverviewTO to) {
        return componentFactory.createAnchor(to.getName(), e -> {
            Map<String, String> params = new LinkedHashMap<>();
            params.put(SONG_ID_PARAM, to.getId().toString());
            if (filterTO.getName() != null) params.put(NAME_PARAM, filterTO.getName().toString());
            if (filterTO.getAuthor() != null) params.put(AUTHOR_PARAM, filterTO.getAuthor().toString());
            if (filterTO.getYear() != null) params.put(YEAR_PARAM, filterTO.getYear().toString());
            if (!grid.getSortOrder().isEmpty()) {
                params.put(SORT_KEY_PARAM, grid.getSortOrder().getFirst().getSorted().getKey());
                params.put(SORT_DIRECTION_PARAM, grid.getSortOrder().getFirst().getDirection().name());
            }

            String listURL = RouteConfiguration.forSessionScope().getUrl(SongsPage.class);
            QueryParameters queryParams = QueryParameters.simple(params);
            UI.getCurrent().getPage().getHistory().replaceState(null, listURL + "?" + queryParams.getQueryString());

            UI.getCurrent().navigate(SongPage.class, to.getId());
        }, e -> UI.getCurrent().getPage()
                .open(RouteConfiguration.forSessionScope().getUrl(SongPage.class, to.getId()), "_blank"));
    }

    public void selectSong(SongOverviewTO to, boolean switchToDetail) {
        grid.select(to);
        if (to != null) {
            grid.getElement().callJsFunction("$server.scrollToId", to.getId().toString());
            if (switchToDetail) UI.getCurrent().navigate("songs/" + to.getId());
        }
    }

    public void populate() {
        grid.getDataProvider().refreshAll();
    }

    public void selectAndScroll(Long id) {
        SongOverviewTO songTO = new SongOverviewTO();
        songTO.setId(id);
        grid.select(songTO);
        grid.getElement().callJsFunction("$server.scrollToId", id.toString());
    }
}