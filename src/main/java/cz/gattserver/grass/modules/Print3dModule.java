package cz.gattserver.grass.modules;

import com.vaadin.flow.component.html.Image;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.grass.core.modules.ContentModule;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Component("print3dModule")
public class Print3dModule implements ContentModule {

	public static final String ID = "cz.gattserver.grass3.print3d:0.0.1";

	@Resource(name = "print3dViewerPageFactory")
	private PageFactory print3dViewerPageFactory;

	@Resource(name = "print3dEditorPageFactory")
	private PageFactory print3dEditorPageFactory;

	@Override
	public String getCreateNewContentLabel() {
		return "Vytvořit nový 3D projekt";
	}

	@Override
	public Image getContentIcon() {
		return ImageIcon.STOP_16_ICON.createImage();
	}

	@Override
	public String getContentID() {
		return ID;
	}

	@Override
	public PageFactory getContentEditorPageFactory() {
		return print3dEditorPageFactory;
	}

	@Override
	public PageFactory getContentViewerPageFactory() {
		return print3dViewerPageFactory;
	}
}