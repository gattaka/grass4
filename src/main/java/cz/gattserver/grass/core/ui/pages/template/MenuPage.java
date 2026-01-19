package cz.gattserver.grass.core.ui.pages.template;

import java.time.LocalDate;
import java.util.List;

import com.vaadin.flow.server.VaadinServletRequest;
import cz.gattserver.common.vaadin.InlineButton;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.pages.LoginDialog;
import jakarta.annotation.Resource;

import cz.gattserver.grass.core.interfaces.NodeOverviewTO;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.modules.SectionService;
import cz.gattserver.grass.core.modules.register.ModuleRegister;
import cz.gattserver.grass.core.services.CoreACLService;
import cz.gattserver.grass.core.services.NodeService;
import cz.gattserver.grass.core.services.VersionInfoService;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.server.VaadinRequest;

public abstract class MenuPage extends GrassPage {

    private static final long serialVersionUID = 8095742933880807949L;
    private static final Logger log = LoggerFactory.getLogger(MenuPage.class);

    @Autowired
    protected VersionInfoService versionInfoService;

    @Lazy
    @Autowired
    protected ModuleRegister serviceHolder;

    @Autowired
    protected CoreACLService coreACL;

    @Autowired
    protected NodeService nodeFacade;

    @Autowired
    protected SecurityService securityService;

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
        Anchor homelink = new Anchor(url, new Image("img/logo.png", "Gattserver"));
        homelinkDiv.add(homelink);

        if (!isMobileDevice()) {
            Div quotes = new Div();
            quotes.setId("quotes");
            logoLine.add(quotes);
            createQuotes(quotes);
        }

        Div menuWrapper = new Div();
        menuWrapper.setId("menu-wrapper");
        top.add(menuWrapper);

        Div menuLeft = new Div();
        menuWrapper.add(menuLeft);

        Div menuRight = new Div();
        menuWrapper.add(menuRight);

        createMenuItems(menuLeft, menuRight);

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

        bottom.add(new Span(
                "Powered by GRASS " + versionInfoService.getProjectVersion() + " © 2012-" + LocalDate.now().getYear() +
                        " Hynek Uhlíř"));

        Div bottomShadow = new Div();
        bottomShadow.setId("bottom-shadow");
        bottomHolder.add(bottomShadow);
    }

    /**
     * Získá menu
     */
    protected void createMenuItems(Div menuLeft, Div menuRight) {

        /**
         * Sections menu
         */

        // sekce článků je rozbalená rovnou jako její kořenové kategorie
        List<NodeOverviewTO> nodes = nodeFacade.getRootNodes();
        for (NodeOverviewTO node : nodes)
            menuLeft.add(new Anchor(getPageURL(nodePageFactory, node.getId() + "-" + node.getName()), node.getName()));

        // externí sekce
        for (SectionService section : serviceHolder.getSectionServices())
            if (coreACL.canShowSection(section, getUser()))
                menuLeft.add(new Anchor(getPageURL(section.getSectionPageFactory()), section.getSectionCaption()));

        /**
         * User menu
         */

        // Přihlášení
        if (!coreACL.isLoggedIn(getUser())) {
            InlineButton loginBtn = new InlineButton("Přihlásit", e -> new LoginDialog().open());
            menuRight.add(loginBtn);
        }

        final UserInfoTO userInfoDTO = getUser();
        if (coreACL.canShowUserDetails(userInfoDTO, getUser())) {
            // Nastavení
            menuRight.add(new Anchor(getPageURL(settingsPageFactory), "Nastavení"));

            // Odhlášení
            InlineButton logoutBtn = new InlineButton("Odhlásit (" + userInfoDTO.getName() + ")",
                    e -> securityService.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null));
            menuRight.add(logoutBtn);
        }
    }

    /**
     * Získá hlášky
     *
     * @param layout layout, do kterého bude vytvořen obsah
     */
    protected abstract void createQuotes(Div layout);

    /**
     * Získá obsah
     *
     * @param layout layout, do kterého bude vytvořen obsah
     */
    protected abstract void createCenterElements(Div layout);

}