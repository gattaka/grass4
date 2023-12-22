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

		Tab chordsTab = new Tab();
		chordsTab.setLabel("Akordy");
		add(chordsTab);

		addSelectedChangeListener(e -> {
			if (!clientAction)
				return;
			switch (getSelectedIndex()) {
				case 0 -> UI.getCurrent().navigate(SongsPage.class);
				case 1 -> UI.getCurrent().navigate(ChordsPage.class);
			}
		});
	}

	private void markTab(int index) {
		clientAction = false;
		setSelectedTab(getTabAt(index));
		clientAction = true;
	}

	public void selectListTab() {
		markTab(0);
	}

	public void selectChordsTab() {
		markTab(1);
	}
}
