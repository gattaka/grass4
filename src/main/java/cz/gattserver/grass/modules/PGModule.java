package cz.gattserver.grass.modules;

import com.vaadin.flow.component.html.Image;
import cz.gattserver.grass.core.modules.ContentModule;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Component("pgModules")
public class PGModule implements ContentModule {

	public static final String ID = "cz.gattserver.grass3.pg:0.0.1";

	@Resource(name = "pgViewerPageFactory")
	private PageFactory pgViewerPageFactory;

	@Resource(name = "pgEditorPageFactory")
	private PageFactory pgEditorPageFactory;

	@Override
	public String getCreateNewContentLabel() {
		return "Vytvořit novou galerii";
	}

	@Override
	public Image getContentIcon() {
		return ImageIcon.IMG_16_ICON.createImage();
	}

	@Override
	public String getContentID() {
		return ID;
	}

	@Override
	public PageFactory getContentEditorPageFactory() {
		return pgEditorPageFactory;
	}

	@Override
	public PageFactory getContentViewerPageFactory() {
		return pgViewerPageFactory;
	}

}
