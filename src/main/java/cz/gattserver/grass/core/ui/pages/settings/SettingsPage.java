package cz.gattserver.grass.core.ui.pages.settings;

import java.util.Comparator;
import java.util.List;

import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.modules.register.ModuleSettingsPageFactoriesRegister;
import cz.gattserver.grass.core.ui.pages.template.TwoColumnPage;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("settings")
@PageTitle("Nastavení")
public class SettingsPage extends TwoColumnPage implements HasUrlParameter<String> {

	private static final long serialVersionUID = 935609806962179905L;

	@Autowired
	private List<ModuleSettingsPageFactory> settingsTabFactories;

	@Autowired
	private ModuleSettingsPageFactoriesRegister register;

	private ModuleSettingsPageFactory settingsTabFactory = null;

	private String moduleParameter;

	private Div layout;

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		moduleParameter = parameter;

		ModuleSettingsPageFactory moduleSettingsPageFactory = register.getFactory(moduleParameter);
		if (moduleSettingsPageFactory != null) {
			if (!moduleSettingsPageFactory.isAuthorized()) {
				throw new GrassPageException(403);
			} else {
				this.settingsTabFactory = moduleSettingsPageFactory;
			}
		}

		if (layout == null) {
			init();
		} else {
			layout.removeAll();
			createContent();
		}
	}

	@Override
	protected void createLeftColumnContent(Div leftContentLayout) {
		VerticalLayout menuLayout = new VerticalLayout();
		menuLayout.setSpacing(true);
		menuLayout.setPadding(false);
		leftContentLayout.add(menuLayout);
		settingsTabFactories.sort(Comparator.comparing(ModuleSettingsPageFactory::getSettingsCaption));
		for (ModuleSettingsPageFactory f : settingsTabFactories) {
			Anchor link = new Anchor(getPageURL(settingsPageFactory, f.getSettingsURL()), f.getSettingsCaption());
			menuLayout.add(link);
		}
	}

	@Override
	protected void createRightColumnContent(Div rightContentLayout) {
		layout = new Div();
		rightContentLayout.add(layout);
		createContent();
	}

	private void createContent() {
		if (settingsTabFactory != null) {
			settingsTabFactory.createFragmentIfAuthorized(layout);
		} else {
			Span span = new Span("Zvolte položku nastavení z menu");
			layout.add(span);
		}
	}
}