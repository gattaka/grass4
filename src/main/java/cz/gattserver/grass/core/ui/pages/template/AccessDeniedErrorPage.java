package cz.gattserver.grass.core.ui.pages.template;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.Route;

@Route("forbidden")
@Tag(Tag.DIV)
public class AccessDeniedErrorPage extends ErrorPage {

	private static final long serialVersionUID = 4576353466500365046L;

	@Override
	protected String getErrorText() {
		return "403 - Nemáte oprávnění k provedení této operace";
	}

	@Override
	protected String getErrorImage() {
		return "VAADIN/img/403.png";
	}
}
