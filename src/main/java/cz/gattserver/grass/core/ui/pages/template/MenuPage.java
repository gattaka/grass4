package cz.gattserver.grass.core.ui.pages.template;

import java.util.List;

import javax.annotation.Resource;

import cz.gattserver.grass.core.interfaces.NodeOverviewTO;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.modules.SectionService;
import cz.gattserver.grass.core.modules.register.ModuleRegister;
import cz.gattserver.grass.core.services.CoreACLService;
import cz.gattserver.grass.core.services.NodeService;
import cz.gattserver.grass.core.services.VersionInfoService;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.server.VaadinRequest;

public abstract class MenuPage extends GrassPage {

	private static final long serialVersionUID = 8095742933880807949L;

	@Autowired
	protected VersionInfoService versionInfoService;

	@Lazy
	@Autowired
	protected ModuleRegister serviceHolder;

	@Autowired
	protected CoreACLService coreACL;

	@Autowired
	protected NodeService nodeFacade;

	@Resource(name = "homePageFactory")
	protected PageFactory homePageFactory;

	@Resource(name = "nodePageFactory")
	protected PageFactory nodePageFactory;

	@Resource(name = "loginPageFactory")
	protected PageFactory loginPageFactory;

	@Resource(name = "settingsPageFactory")
	protected PageFactory settingsPageFactory;

	@Resource(name = "registrationPageFactory")
	protected PageFactory registrationPageFactory;

	@Resource(name = "quotesPageFactory")
	protected PageFactory quotesPageFactory;

	@Override
	protected void createPageElements(Div payload) {
		Div holder = new Div();
		holder.setId("holder");
		payload.add(holder);

		Div topHolder = new Div();
		topHolder.setId("top-holder");
		holder.add(topHolder);

		Div top = new Div();
		top.setId("top");
		topHolder.add(top);

		Div logoLine = new Div();
		logoLine.setId("logo-line");
		top.add(logoLine);

		// homelink (přes logo)
		Div homelinkDiv = new Div();
		homelinkDiv.setId("homelink");
		logoLine.add(homelinkDiv);
		String url = VaadinRequest.getCurrent().getContextPath();
		Anchor homelink = new Anchor(url, new Image("VAADIN/img/logo.png", "Gattserver"));
		homelinkDiv.add(homelink);

		if (!isMobileDevice()) {
			Div quotes = new Div();
			quotes.setId("quotes");
			logoLine.add(quotes);
			createQuotes(quotes);
		}

		Div menu = new Div();
		menu.setId("menu-wrapper");
		top.add(menu);

		Div menuExpander = new Div();
		menuExpander.setId("menu");
		menu.add(menuExpander);

		createMenuItems(menuExpander);

		Div content = new Div();
		content.setId("content");
		holder.add(content);

		createCenterElements(content);

		Div bottomHolder = new Div();
		bottomHolder.setId("bottom-holder");
		payload.add(bottomHolder);

		Div bottom = new Div();
		bottom.setId("bottom");
		bottomHolder.add(bottom);

		bottom.add(new Span("Powered by GRASS " + versionInfoService.getProjectVersion() + " © 2012-2021 Hynek Uhlíř"));

		Div bottomShadow = new Div();
		bottomShadow.setId("bottom-shadow");
		bottomHolder.add(bottomShadow);
	}

	protected Div createMenuComponent(Div menu, Anchor component) {
		return createMenuComponent(menu, component, false);
	}

	protected Div createMenuComponent(Div menu, Anchor component, boolean rightMenu) {
		Div wrapper = new Div(component);
		wrapper.add(component);
		menu.add(wrapper);
		wrapper.addClassName("menu-item");
		if (rightMenu)
			wrapper.addClassName("menu-item-right");
		return wrapper;
	}

	/**
	 * Získá menu
	 */
	protected void createMenuItems(Div menu) {

		/**
		 * Sections menu
		 */

		// sekce článků je rozbalená rovnou jako její kořenové kategorie
		List<NodeOverviewTO> nodes = nodeFacade.getRootNodes();
		for (NodeOverviewTO node : nodes)
			createMenuComponent(menu,
					new Anchor(getPageURL(nodePageFactory, node.getId() + "-" + node.getName()), node.getName()));

		// externí sekce
		for (SectionService section : serviceHolder.getSectionServices())
			if (coreACL.canShowSection(section, getUser()))
				createMenuComponent(menu,
						new Anchor(getPageURL(section.getSectionPageFactory()), section.getSectionCaption()));

		Div wrapper = createMenuComponent(menu, new Anchor(getPageURL(quotesPageFactory), "Hlášky"));
		wrapper.setId("quotes-menu-item");

		/**
		 * User menu
		 */

		// Registrace
		if (coreACL.canRegistrate(getUser()))
			createMenuComponent(menu, new Anchor(getPageURL(registrationPageFactory), "Registrace"), true);

		// Přihlášení
		if (!coreACL.isLoggedIn(getUser()))
			createMenuComponent(menu, new Anchor(getPageURL(loginPageFactory), "Přihlášení"), true);

		// Přehled o uživateli
		final UserInfoTO userInfoDTO = getUser();
		if (coreACL.canShowUserDetails(userInfoDTO, getUser())) {
			// odhlásit
			createMenuComponent(menu, new Anchor(getPageURL("logout"), "Odhlásit (" + userInfoDTO.getName() + ")"),
					true);

			// nastavení
			createMenuComponent(menu, new Anchor(getPageURL(settingsPageFactory), "Nastavení"), true);
		}

	}

	/**
	 * Získá hlášky
	 * 
	 * @param layout
	 *            layout, do kterého bude vytvořen obsah
	 */
	protected abstract void createQuotes(Div layout);

	/**
	 * Získá obsah
	 * 
	 * @param layout
	 *            layout, do kterého bude vytvořen obsah
	 */
	protected abstract void createCenterElements(Div layout);

}
