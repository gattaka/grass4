package cz.gattserver.grass.core.ui.pages.settings;

import cz.gattserver.grass.core.ui.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("settingsPageFactory")
public class SettingsPageFactory extends AbstractPageFactory {

	public SettingsPageFactory() {
		super("settings");
	}

}
