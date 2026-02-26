package cz.gattserver.grass.songs.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.RouterLink;

public class Menu extends Div {
    public Menu() {
        setId("small-menu");
        add(new RouterLink("Seznam", SongsPage.class));
        add(new RouterLink("Akordy", ChordsPage.class));
    }
}