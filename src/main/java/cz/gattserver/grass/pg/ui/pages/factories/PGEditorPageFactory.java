package cz.gattserver.grass.pg.ui.pages.factories;

import cz.gattserver.grass.core.ui.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("pgEditorPageFactory")
public class PGEditorPageFactory extends AbstractPageFactory {

	public PGEditorPageFactory() {
		super("pg-editor");
	}

}
