package cz.gattserver.grass.songs.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.LinkButton;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.components.button.CreateGridButton;
import cz.gattserver.grass.core.ui.components.button.DeleteGridButton;
import cz.gattserver.grass.core.ui.components.button.ModifyGridButton;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.GrassMultiFileBuffer;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.songs.SongsRole;
import cz.gattserver.grass.songs.facades.SongsService;
import cz.gattserver.grass.songs.model.interfaces.SongOverviewTO;
import cz.gattserver.grass.songs.model.interfaces.SongTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.Resource;
import java.util.List;

public class ListTab extends Div {

	private static final long serialVersionUID = 594189301140808163L;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private SongsService songsService;

	@Resource(name = "songsPageFactory")
	private SongsPageFactory pageFactory;

	private Grid<SongOverviewTO> grid;

	private SongOverviewTO filterTO;

	private SongsPage songsPage;

	public ListTab(SongsPage songsPage) {
		SpringContextHelper.inject(this);
		filterTO = new SongOverviewTO();
		UserInfoTO user = securityService.getCurrentUser();
		filterTO.setPublicated(user.isAdmin() ? null : true);
		this.songsPage = songsPage;

		grid = new Grid<>();
		grid.setMultiSort(false);
		UIUtils.applyGrassDefaultStyle(grid);

		grid.addColumn(SongOverviewTO::getId).setHeader("Id").setSortable(true).setWidth("50px").setFlexGrow(0);
		Column<SongOverviewTO> nazevColumn = grid
				.addColumn(new ComponentRenderer<Button, SongOverviewTO>(
						to -> new LinkButton(to.getName(), e -> selectSong(to, true))))
				.setHeader("Název").setSortable(true);
		Column<SongOverviewTO> authorColumn = grid.addColumn(SongOverviewTO::getAuthor).setHeader("Autor")
				.setSortable(true).setWidth("250px").setFlexGrow(0);
		Column<SongOverviewTO> yearColumn = grid.addColumn(SongOverviewTO::getYear).setHeader("Rok").setWidth("60px")
				.setSortable(true).setFlexGrow(0);
		grid.setWidthFull();
		grid.setHeight("600px");
		add(grid);

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
		add(upload);
		upload.setVisible(securityService.getCurrentUser().getRoles().contains(SongsRole.SONGS_EDITOR));

		ButtonLayout btnLayout = new ButtonLayout();
		add(btnLayout);

		btnLayout.setVisible(securityService.getCurrentUser().getRoles().contains(SongsRole.SONGS_EDITOR));

		btnLayout.add(new CreateGridButton("Přidat", event -> {
			new SongDialog() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(SongTO to) {
					to = songsService.saveSong(to);
					populate();
					selectSong(to, true);
				}
			}.open();
		}));

		btnLayout.add(new ModifyGridButton<SongOverviewTO>("Upravit", event -> {
			new SongDialog(songsService.getSongById(grid.getSelectedItems().iterator().next().getId())) {

				private static final long serialVersionUID = 5264621441522056786L;

				@Override
				protected void onSave(SongTO to) {
					to = songsService.saveSong(to);
					populate();
					selectSong(to, false);
				}
			}.open();
		}, grid));

		btnLayout.add(new DeleteGridButton<SongOverviewTO>("Smazat", items -> {
			for (SongOverviewTO s : items)
				songsService.deleteSong(s.getId());
			populate();
			selectSong(null, false);
		}, grid));
	}

	public void selectSong(SongOverviewTO to, boolean switchToDetail) {
		grid.select(to);
		if (switchToDetail)
			songsPage.selectSong(to.getId());
	}

	public void populate() {
		List<SongOverviewTO> songs = songsService.getSongs(filterTO, grid.getSortOrder());
		grid.setItems(songs);
	}

}
