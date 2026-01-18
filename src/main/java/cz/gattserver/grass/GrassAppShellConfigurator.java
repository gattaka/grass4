package cz.gattserver.grass;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.ColorScheme;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.aura.Aura;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * https://vaadin.com/docs/latest/advanced/modifying-the-bootstrap-page#application-shell-configurator
 */
@StyleSheet(Aura.STYLESHEET)
@CssImport("./styles/styles.css")
//@StyleSheet("frontend://styles/styles.css") // tohle nefunguje
public class GrassAppShellConfigurator implements AppShellConfigurator {

	@Override
	public void configurePage(AppShellSettings settings) {
		settings.addFavIcon("icon", "img/favicon.png", "16px");
	}
}
