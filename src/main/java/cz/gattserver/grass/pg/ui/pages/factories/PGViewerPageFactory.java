package cz.gattserver.grass.pg.ui.pages.factories;

import cz.gattserver.grass.core.ui.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("pgViewerPageFactory")
public class PGViewerPageFactory extends AbstractPageFactory {

	public PGViewerPageFactory() {
		super("photogallery");
	}
}
