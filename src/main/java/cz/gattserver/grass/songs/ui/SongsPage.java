package cz.gattserver.grass.songs.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.*;
import cz.gattserver.grass.core.ui.pages.template.OneColumnPage;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.songs.model.interfaces.ChordTO;
import cz.gattserver.grass.songs.model.interfaces.SongOverviewTO;
import cz.gattserver.grass.songs.model.interfaces.SongTO;

import java.util.Optional;

@Route("songs/:id?([0-9]*)")
@PageTitle("Zpěvník")
public class SongsPage extends OneColumnPage implements BeforeEnterObserver {

	private static final long serialVersionUID = -6336711256361320029L;

	private Tabs tabSheet;
	private Tab listTab;
	private Tab songTab;
	private Tab chordsTab;

	private ListTab listTabContent;
	private SongTab songTabContent;
	private ChordsTab chordsTabContent;

	private Div pageLayout;
	private Long songId;

	public SongsPage() {
		init();
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		Optional<Long> param = event.getRouteParameters().get("id").map(Long::parseLong);
		if (param.isPresent()) {
			songId = param.get();
			selectSong(songId);
		} else {
			selectListTab();
		}

		if (songId != null) {
			SongOverviewTO to = new SongOverviewTO();
			to.setId(songId);
			listTabContent.selectSong(to, false);
		}
	}

	@Override
	protected void createColumnContent(Div layout) {
		tabSheet = new Tabs();
		layout.add(tabSheet);

		pageLayout = new Div();
		pageLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		layout.add(pageLayout);

		listTab = new Tab();
		listTab.setLabel("Seznam");
		tabSheet.add(listTab);

		songTab = new Tab();
		songTab.setLabel("Písnička");
		songTab.setEnabled(false);
		tabSheet.add(songTab);

		chordsTab = new Tab();
		chordsTab.setLabel("Akordy");
		tabSheet.add(chordsTab);

		listTabContent = new ListTab(this);
		pageLayout.add(listTabContent);

		chordsTabContent = new ChordsTab(this);
		pageLayout.add(chordsTabContent);

		songTabContent = new SongTab(this);
		pageLayout.add(songTabContent);

		tabSheet.addSelectedChangeListener(e -> {
			switch (tabSheet.getSelectedIndex()) {
				default:
				case 0:
					switchListTab();
					break;
				case 1:
					switchSongTab();
					break;
				case 2:
					switchChordsTab();
					break;
			}
		});
		switchListTab();
	}

	public void selectListTab() {
		tabSheet.setSelectedTab(listTab);
		switchListTab();
	}

	public void selectSongTab() {
		tabSheet.setSelectedTab(songTab);
		switchSongTab();
	}

	public void selectChordsTab() {
		tabSheet.setSelectedTab(chordsTab);
		switchChordsTab();
	}

	private void switchListTab() {
		songTabContent.setVisible(false);
		chordsTabContent.setVisible(false);
		listTabContent.getStyle().set("display", "block");
		if (songId != null) {
			SongOverviewTO to = new SongOverviewTO();
			to.setId(songId);
			listTabContent.selectSong(to, false);
		}
	}

	private void switchSongTab() {
		songTabContent.setVisible(true);
		chordsTabContent.setVisible(false);
		listTabContent.getStyle().set("display", "none");
	}

	private void switchChordsTab() {
		songTabContent.setVisible(false);
		chordsTabContent.setVisible(true);
		listTabContent.getStyle().set("display", "none");
	}

	public void selectChord(ChordTO chord) {
		chordsTabContent.selectChord(chord);
		selectChordsTab();
	}

	public void selectSong(Long id) {
		if (id != null) {
			songTabContent.selectSong(id);
			songTab.setEnabled(true);
		}
		selectSongTab();
	}
}