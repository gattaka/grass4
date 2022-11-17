package cz.gattserver.grass.print3d.ui.pages.factories;


import cz.gattserver.grass.core.ui.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("print3dViewerPageFactory")
public class Print3dViewerPageFactory extends AbstractPageFactory {

	public Print3dViewerPageFactory() {
		super("print3d");
	}
}
