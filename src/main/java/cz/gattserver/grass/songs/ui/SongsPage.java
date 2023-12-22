package cz.gattserver.grass.songs.ui;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.internal.AllowInert;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import cz.gattserver.common.vaadin.LinkButton;
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
		getTabVariable(SongsPage.SONG_ID_TAB_VAR, val -> {
			songId = val;

			UI.getCurrent().access(() -> {
				setTabVariable(SongsPage.SONG_ID_TAB_VAR, null);

				if (songId != null) {
					UI.getCurrent().getPage().getHistory().replaceState(null, "songs/" + songId);
				} else if (parameter != null)
					songId = Long.parseLong(parameter);

				if (tabsMenu == null)
					init();

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
				grid.scrollToIndex(indexMap.get(id));
			}
		};
		grid.setMultiSort(false);
		UIUtils.applyGrassDefaultStyle(grid);

		grid.addColumn(SongOverviewTO::getId).setHeader("Id").setSortable(true).setWidth("50px").setFlexGrow(0);
		Grid.Column<SongOverviewTO> nazevColumn = grid
				.addColumn(new ComponentRenderer<>(to -> new LinkButton(to.getName(), e -> {
					songId = to.getId();
					UI.getCurrent().navigate(SongPage.class, new RouteParam("id", to.getId()));
				}))).setHeader("Název").setSortable(true);
		Grid.Column<SongOverviewTO> authorColumn = grid.addColumn(SongOverviewTO::getAuthor).setHeader("Autor")
				.setSortable(true).setWidth("250px").setFlexGrow(0);
		Grid.Column<SongOverviewTO> yearColumn = grid.addColumn(SongOverviewTO::getYear).setHeader("Rok").setWidth(
						"60px")
				.setSortable(true).setFlexGrow(0);
		grid.setWidthFull();
		grid.setHeight("600px");
		layout.add(grid);

		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Název
		UIUtils.addHeaderTextField(filteringHeader.getCell(nazevColumn), e -> {
			filterTO.setName(e.getValue());
			populate();
		});

		// Autor
		UIUtils.addHeaderTextField(filteringHeader.getCell(authorColumn), e -> {
			filterTO.setAuthor(e.getValue());
			populate();
		});

		// Rok
		UIUtils.addHeaderTextField(filteringHeader.getCell(yearColumn), e -> {
			filterTO.setYear(StringUtils.isBlank(e.getValue()) ? null : Integer.valueOf(e.getValue()));
			populate();
		});

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

		btnLayout.add(new CreateGridButton("Přidat", event ->
				new SongDialog(to -> {
					to = songsService.saveSong(to);
					populate();
					selectSong(to, true);
				}).open()
		));

		btnLayout.add(new ModifyGridButton<>("Upravit", event ->
				new SongDialog(songsService.getSongById(grid.getSelectedItems().iterator().next().getId()), to -> {
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
			if (switchToDetail)
				UI.getCurrent().navigate("songs/" + to.getId());
		}
	}

	public void populate() {
		List<SongOverviewTO> songs = songsService.getSongs(filterTO, grid.getSortOrder());
		for (int i = 0; i < songs.size(); i++)
			indexMap.put(songs.get(i).getId(), i);
		grid.setItems(songs);
	}
}