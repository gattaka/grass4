package cz.gattserver.grass.articles.ui.pages.settings.factories;

import cz.gattserver.grass.articles.ui.pages.settings.ArticlesSettingsPageFragmentFactory;
import cz.gattserver.grass.core.ui.pages.settings.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;
import org.springframework.stereotype.Component;

@Component
public class ArticlesSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	public ArticlesSettingsPageFactory() {
		super("Články", "articles");
	}

	public boolean isAuthorized() {
		return getUser().isAdmin();
	}

	@Override
	protected AbstractPageFragmentFactory createPageFragmentFactory() {
		return new ArticlesSettingsPageFragmentFactory();
	}
}
