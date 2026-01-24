package cz.gattserver.grass.core.ui.pages.template;

import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.internal.AllowInert;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.Theme;

import cz.gattserver.common.exception.ApplicationErrorHandler;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.grass.core.ui.js.JScriptItem;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass.core.ui.util.UIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Základní layout pro stránky systému Grass. Volá {@link SpringContextHelper}
 * pro injektování závislostí. Poskytuje metody pro vyhazování chyb na stránce,
 * přidávání JS a CSS zdrojů a získávání URL informací.
 * <p>
 * Anotace {@link CssImport}, {@link JsModule} a {@link Theme} jsou ve web
 * modulu (aby fungoval incremental build)
 *
 * @author Hynek
 */
public abstract class GrassPage extends Div {

	private static final long serialVersionUID = 7952966362953000385L;

	private static final Logger logger = LoggerFactory.getLogger(GrassPage.class);

	private transient SecurityService securityFacade;

	private Consumer<Long> getTabVariableConsumer;

    protected ComponentFactory componentFactory;

	/**
	 * Konstruktor stránky. Slouží pro přípravu dat pro její sestavení, ale sám
	 * ještě nesestavuje.
	 */
	public GrassPage() {
		SpringContextHelper.inject(this);
		VaadinSession.getCurrent().setErrorHandler(new ApplicationErrorHandler());
        componentFactory = new ComponentFactory();
	}

	protected boolean isMobileDevice() {
		return UIUtils.isMobileDevice();
	}

	public void init() {
		createPageElements(this);
		setId("main-div");
		UI.getCurrent().getPage().addJavaScript("context://js/jquery.js");
	}

	protected abstract void createPageElements(Div div);

	/**
	 * Získá aktuálního přihlášeného uživatele jako {@link UserInfoTO} objekt
	 */
	public UserInfoTO getUser() {
		if (securityFacade == null)
			securityFacade = SpringContextHelper.getBean(SecurityService.class);
		return securityFacade.getCurrentUser();
	}

	public PendingJavaScriptResult loadJS(JScriptItem... scripts) {
		return UIUtils.loadJS(scripts);
	}

	public String getContextPath() {
		return UIUtils.getContextPath();
	}

	public String getPageURL(PageFactory pageFactory) {
		return UIUtils.getPageURL(pageFactory);
	}

	public String getPageURL(String suffix) {
		return UIUtils.getPageURL(suffix);
	}

	public String getPageURL(PageFactory pageFactory, String... relativeURLs) {
		return UIUtils.getPageURL(pageFactory, relativeURLs);
	}

	public void loadCSS(String link) {
		UIUtils.loadCSS(link);
	}

}
