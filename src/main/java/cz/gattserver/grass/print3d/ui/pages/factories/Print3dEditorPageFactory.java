package cz.gattserver.grass.print3d.ui.pages.factories;

import cz.gattserver.grass.core.ui.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("print3dEditorPageFactory")
public class Print3dEditorPageFactory extends AbstractPageFactory {

	public Print3dEditorPageFactory() {
		super("print3d-editor");
	}

}
