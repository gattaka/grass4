package cz.gattserver.grass.songs.ui;

import com.vaadin.flow.component.ClientCallable;
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
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.ui.components.button.CreateGridButton;
import cz.gattserver.grass.core.ui.components.button.DeleteGridButton;
import cz.gattserver.grass.core.ui.components.button.ModifyGridButton;
import cz.gattserver.grass.core.ui.pages.template.OneColumnPage;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.GrassMultiFileBuffer;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.songs.SongsRole;
import cz.gattserver.grass.songs.facades.SongsService;
import cz.gattserver.grass.songs.model.interfaces.SongOverviewTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route("songs")
@PageTitle("Zpěvník")
public class SongsPage extends OneColumnPage implements HasUrlParameter<String> {

    private static final long serialVersionUID = -6336711256361320029L;

    public static final String SONG_ID_TAB_VAR = "grass-songs-song-id";
    public static final String SORT_SESSION_VAR = "grass-songs-sort";
    public static final String FILTER_SESSION_VAR = "grass-songs-filter";

    @Autowired
    private SongsService songsService;

    private Grid<SongOverviewTO> grid;

    private SongOverviewTO filterTO;

    private Map<Long, Integer> indexMap = new HashMap<>();

    private TabsMenu tabsMenu;

    private Div pageLayout;
    private Long songId;

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (tabsMenu == null) init();

        getTabVariable(SongsPage.SONG_ID_TAB_VAR, val -> {
            songId = val;

            UI.getCurrent().access(() -> {
                setTabVariable(SongsPage.SONG_ID_TAB_VAR, null);
                if (songId != null) {
                    UI.getCurrent().getPage().getHistory().replaceState(null, "songs/" + songId);
                } else if (parameter != null) songId = Long.parseLong(parameter);

                if (songId != null) {
                    SongOverviewTO to = new SongOverviewTO();
                    to.setId(songId);
                    selectSong(to, false);
                } else {
                    selectSong(null, false);
                }
            });
        });
    }

    @Override
    protected void createColumnContent(Div layout) {
        tabsMenu = new TabsMenu();
        layout.add(tabsMenu);

        pageLayout = new Div();
        pageLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(pageLayout);

        filterTO = new SongOverviewTO();
        UserInfoTO user = securityService.getCurrentUser();
        filterTO.setPublicated(user.isAdmin() ? null : true);

        grid = new Grid<>() {
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

        grid.addColumn(SongOverviewTO::getId).setHeader("Id").setSortable(true).setKey("id").setWidth("50px")
                .setFlexGrow(0);
        Grid.Column<SongOverviewTO> nazevColumn =
                grid.addColumn(new ComponentRenderer<>(to -> new Anchor("song/" + to.getId(), to.getName())))
                        .setHeader("Název").setSortable(true).setKey("name");
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

            VaadinSession.getCurrent().setAttribute(SORT_SESSION_VAR, grid.getSortOrder());
            VaadinSession.getCurrent().setAttribute(FILTER_SESSION_VAR, filterTO);

            return songsService.getSongs(filterTO, q.getOffset(), q.getLimit(), q.getSortOrders()).stream();
        }, q -> songsService.getSongsCount(filterTO)));
        layout.add(grid);

        Object order = VaadinSession.getCurrent().getAttribute(SORT_SESSION_VAR);
        if (order != null) grid.sort((List<GridSortOrder<SongOverviewTO>>) order);

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

        Object filterAttr = VaadinSession.getCurrent().getAttribute(FILTER_SESSION_VAR);
        if (filterAttr != null) {
            filterTO = (SongOverviewTO) filterAttr;
            if (filterTO.getName() != null) nameField.setValue(filterTO.getName());
            if (filterTO.getAuthor() != null) authorField.setValue(filterTO.getAuthor());
            if (filterTO.getYear() != null) yearField.setValue(String.valueOf(filterTO.getYear()));
        }
        populate();

        GrassMultiFileBuffer buffer = new GrassMultiFileBuffer();

        Upload upload = new Upload(buffer);
        upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        upload.setAcceptedFileTypes("text/plain");
        upload.addSucceededListener(event -> {
            songsService.importSong(buffer.getInputStream(event.getFileName()), event.getFileName());
            populate();
        });
        layout.add(upload);
        upload.setVisible(securityService.getCurrentUser().getRoles().contains(SongsRole.SONGS_EDITOR));

        ButtonLayout btnLayout = new ButtonLayout();
        layout.add(btnLayout);

        btnLayout.setVisible(securityService.getCurrentUser().getRoles().contains(SongsRole.SONGS_EDITOR));

        btnLayout.add(new CreateGridButton("Přidat", event -> new SongDialog(to -> {
            to = songsService.saveSong(to);
            populate();
            selectSong(to, true);
        }).open()));

        btnLayout.add(new ModifyGridButton<>("Upravit",
                event -> new SongDialog(songsService.getSongById(grid.getSelectedItems().iterator().next().getId()),
                        to -> {
                            to = songsService.saveSong(to);
                            populate();
                            selectSong(to, false);
                        }).open(), grid));

        btnLayout.add(new DeleteGridButton<>("Smazat", items -> {
            for (SongOverviewTO s : items)
                songsService.deleteSong(s.getId());
            populate();
            selectSong(null, false);
        }, grid));
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
}