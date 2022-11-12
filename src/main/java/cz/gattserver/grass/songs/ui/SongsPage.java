package cz.gattserver.grass.songs.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.gattserver.grass.core.ui.pages.template.OneColumnPage;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.songs.model.interfaces.ChordTO;

@Route("songs")
@PageTitle("Zpěvník")
public class SongsPage extends OneColumnPage {

	private static final long serialVersionUID = -6336711256361320029L;

	private Tabs tabSheet;
	private Tab listTab;
	private Tab songTab;
	private Tab chordsTab;

	private ListTab listTabContent;
	private SongTab songTabContent;
	private ChordsTab chordsTabContent;

	private Div pageLayout;

	public SongsPage() {
		init();
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
		listTabContent.setVisible(true);
	}

	private void switchSongTab() {
		songTabContent.setVisible(true);
		chordsTabContent.setVisible(false);
		listTabContent.setVisible(false);
	}

	private void switchChordsTab() {
		songTabContent.setVisible(false);
		chordsTabContent.setVisible(true);
		listTabContent.setVisible(false);
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
