package cz.gattserver.grass.songs.ui;

import cz.gattserver.grass.core.ui.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("songsPageFactory")
public class SongsPageFactory extends AbstractPageFactory {

	public SongsPageFactory() {
		super("songs");
	}
}
