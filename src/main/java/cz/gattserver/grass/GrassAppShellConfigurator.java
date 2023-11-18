package cz.gattserver.grass;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.theme.Theme;

/**
 * https://vaadin.com/docs/latest/advanced/modifying-the-bootstrap-page#application-shell-configurator
 */
@Theme(value = "grass")
public class GrassAppShellConfigurator implements AppShellConfigurator {

	@Override
	public void configurePage(AppShellSettings settings) {
		settings.addFavIcon("icon", "img/favicon.png", "16px");
	}
}
