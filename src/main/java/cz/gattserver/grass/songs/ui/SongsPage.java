package cz.gattserver.grass.songs.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.*;
import cz.gattserver.grass.core.ui.pages.template.OneColumnPage;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.songs.model.interfaces.ChordTO;
import cz.gattserver.grass.songs.model.interfaces.SongOverviewTO;
import elemental.json.JsonType;
import org.apache.commons.lang3.StringUtils;

@Route("songs")
@PageTitle("Zpěvník")
public class SongsPage extends OneColumnPage implements HasUrlParameter<String> {

	private static final long serialVersionUID = -6336711256361320029L;

	public static final String SONG_ID_TAB_VAR = "grass-songs-song-id";

	private Tabs tabSheet;
	private Tab listTab;
	private Tab chordsTab;

	private ListTab listTabContent;
	private ChordsTab chordsTabContent;

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

				if (tabSheet == null)
					init();

				if (songId != null) {
					SongOverviewTO to = new SongOverviewTO();
					to.setId(songId);
					listTabContent.selectSong(to, false);
				} else {
					listTabContent.selectSong(null, false);
				}
			});
		});
	}

	public void setSongId(Long songId) {
		this.songId = songId;
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

		chordsTab = new Tab();
		chordsTab.setLabel("Akordy");
		tabSheet.add(chordsTab);

		listTabContent = new ListTab(this);
		pageLayout.add(listTabContent);

		chordsTabContent = new ChordsTab(this);
		pageLayout.add(chordsTabContent);

		tabSheet.addSelectedChangeListener(e -> {
			switch (tabSheet.getSelectedIndex()) {
				default:
				case 0:
					switchListTab();
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

	public void selectChordsTab() {
		tabSheet.setSelectedTab(chordsTab);
		switchChordsTab();
	}

	private void switchListTab() {
		chordsTabContent.setVisible(false);
		listTabContent.getStyle().set("display", "block");
		if (songId != null) {
			SongOverviewTO to = new SongOverviewTO();
			to.setId(songId);
			listTabContent.selectSong(to, false);
		}
	}

	private void switchChordsTab() {
		chordsTabContent.setVisible(true);
		listTabContent.getStyle().set("display", "none");
	}

	public void selectChord(ChordTO chord) {
		chordsTabContent.selectChord(chord);
		selectChordsTab();
	}
}