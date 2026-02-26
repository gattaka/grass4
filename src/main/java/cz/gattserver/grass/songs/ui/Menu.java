package cz.gattserver.grass.songs.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.RouterLink;

import java.io.Serial;

public class Menu extends Div {

    @Serial
    private static final long serialVersionUID = -3004570156334075466L;

    public Menu() {
        setId("small-menu");
        add(new RouterLink("Seznam", SongsPage.class));
        add(new RouterLink("Akordy", ChordsPage.class));
    }
}