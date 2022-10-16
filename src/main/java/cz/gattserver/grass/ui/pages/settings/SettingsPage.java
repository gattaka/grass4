package cz.gattserver.grass.ui.pages.settings;

import java.util.List;

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

import cz.gattserver.grass.exception.GrassPageException;
import cz.gattserver.grass.modules.register.ModuleSettingsPageFactoriesRegister;
import cz.gattserver.grass.ui.pages.template.TwoColumnPage;

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

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		moduleParameter = parameter;
		init();
	}

	@Override
	protected void createPageElements(Div layout) {
		ModuleSettingsPageFactory moduleSettingsPageFactory = register.getFactory(moduleParameter);

		if (moduleSettingsPageFactory != null) {
			if (!moduleSettingsPageFactory.isAuthorized()) {
				throw new GrassPageException(403);
			} else {
				this.settingsTabFactory = moduleSettingsPageFactory;
			}
		}

		super.createPageElements(layout);
	}

	@Override
	protected void createLeftColumnContent(Div leftContentLayout) {
		VerticalLayout menuLayout = new VerticalLayout();
		menuLayout.setSpacing(true);
		menuLayout.setPadding(false);
		leftContentLayout.add(menuLayout);
		settingsTabFactories.sort((a, b) -> a.getSettingsCaption().compareTo(b.getSettingsCaption()));
		for (ModuleSettingsPageFactory f : settingsTabFactories) {
			Anchor link = new Anchor(getPageURL(settingsPageFactory, f.getSettingsURL()), f.getSettingsCaption());
			menuLayout.add(link);
		}
	}

	@Override
	protected void createRightColumnContent(Div rightContentLayout) {
		if (settingsTabFactory != null) {
			settingsTabFactory.createFragmentIfAuthorized(rightContentLayout);
		} else {
			Span span = new Span("Zvolte položku nastavení z menu");
			rightContentLayout.add(span);
		}
	}

}
