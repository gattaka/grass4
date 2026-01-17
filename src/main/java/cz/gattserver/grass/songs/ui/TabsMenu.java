package cz.gattserver.grass.songs.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

public class TabsMenu extends Tabs {

    private boolean clientAction = true;

    public TabsMenu() {
        Tab listTab = new Tab();
        listTab.setLabel("Seznam");
        add(listTab);

        Tab songTab = new Tab();
        songTab.setLabel("Píseň");
        songTab.setEnabled(false);
        add(songTab);

        Tab chordsTab = new Tab();
        chordsTab.setLabel("Akordy");
        add(chordsTab);

        addSelectedChangeListener(e -> {
            if (!clientAction) return;
            switch (getSelectedIndex()) {
                case 0 -> UI.getCurrent().navigate(SongsPage.class);
                case 1 -> UI.getCurrent().navigate(SongPage.class);
                case 2 -> UI.getCurrent().navigate(ChordsPage.class);
            }
        });
    }

    private void markTab(int index) {
        clientAction = false;
        Tab tab = getTabAt(index);
        tab.setEnabled(true);
        setSelectedTab(tab);
        clientAction = true;
    }

    public void selectListTab() {
        markTab(0);
    }

    public void selectSongTab() {
        markTab(1);
    }

    public void selectChordsTab() {
        markTab(2);
    }
}
