package cz.gattserver.grass.core.ui.pages;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServletRequest;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.core.interfaces.NodeOverviewTO;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.modules.SectionService;
import cz.gattserver.grass.core.modules.register.ModuleRegister;
import cz.gattserver.grass.core.services.CoreACLService;
import cz.gattserver.grass.core.services.NodeService;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.services.VersionInfoService;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass.core.ui.util.UIUtils;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@JsModule("context://js/jquery.js")
public class MainView extends Div implements AfterNavigationObserver, RouterLayout {

    private static final long serialVersionUID = 8095742933880807949L;
    private static final Logger log = LoggerFactory.getLogger(MainView.class);

    @Resource(name = "nodePageFactory")
    protected PageFactory nodePageFactory;

    @Resource(name = "settingsPageFactory")
    protected PageFactory settingsPageFactory;

    @Resource(name = "registrationPageFactory")
    protected PageFactory registrationPageFactory;

    @Resource(name = "quotesPageFactory")
    protected PageFactory quotesPageFactory;

    private Div contentDiv;

    public MainView() {
        SpringContextHelper.inject(this);

        setId("main-div");

        Div holder = new Div();
        holder.setId("holder");
        add(holder);

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

        // TODO
//        if (!isMobileDevice()) {
//            Div quotes = new Div();
//            quotes.setId("quotes");
//            logoLine.add(quotes);
//            createQuotes(quotes);
//        }

        Div menuWrapper = new Div();
        menuWrapper.setId("menu-wrapper");
        top.add(menuWrapper);

        Div menuLeft = new Div();
        menuWrapper.add(menuLeft);

        Div menuRight = new Div();
        menuWrapper.add(menuRight);

        createMenuItems(menuLeft, menuRight);

        contentDiv = new Div();
        contentDiv.setId("content");
        holder.add(contentDiv);

        Div bottomHolder = new Div();
        bottomHolder.setId("bottom-holder");
        add(bottomHolder);

        Div bottom = new Div();
        bottom.setId("bottom");
        bottomHolder.add(bottom);

        VersionInfoService versionInfoService = SpringContextHelper.getBean(VersionInfoService.class);
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
        ComponentFactory componentFactory = new ComponentFactory();

        SecurityService securityService = SpringContextHelper.getBean(SecurityService.class);
        UserInfoTO currentUser = securityService.getCurrentUser();

        /**
         * Sections menu
         */

        // sekce článků je rozbalená rovnou jako její kořenové kategorie
        NodeService nodeFacade = SpringContextHelper.getBean(NodeService.class);
        List<NodeOverviewTO> nodes = nodeFacade.getRootNodes();
        for (NodeOverviewTO node : nodes)
            menuLeft.add(new Anchor(UIUtils.getPageURL(nodePageFactory, node.getId() + "-" + node.getName()),
                    node.getName()));

        // externí sekce
        ModuleRegister serviceHolder = SpringContextHelper.getBean(ModuleRegister.class);
        CoreACLService coreACL = SpringContextHelper.getBean(CoreACLService.class);
        for (SectionService section : serviceHolder.getSectionServices())
            if (coreACL.canShowSection(section, currentUser)) menuLeft.add(
                    new Anchor(UIUtils.getPageURL(section.getSectionPageFactory()), section.getSectionCaption()));

        /**
         * User menu
         */

        // Přihlášení
        if (!coreACL.isLoggedIn(currentUser)) {
            Div loginBtn = componentFactory.createInlineButton("Přihlásit", e -> new LoginDialog().open());
            menuRight.add(loginBtn);
        }

        final UserInfoTO userInfoDTO = currentUser;
        if (coreACL.canShowUserDetails(userInfoDTO, currentUser)) {
            // Nastavení
            menuRight.add(new Anchor(UIUtils.getPageURL(settingsPageFactory), "Nastavení"));

            // Odhlášení
            Div logoutBtn = componentFactory.createInlineButton("Odhlásit (" + userInfoDTO.getName() + ")",
                    e -> securityService.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null));
            menuRight.add(logoutBtn);
        }
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        contentDiv.getElement().appendChild(content.getElement());
    }

    @Override
    public void removeRouterLayoutContent(HasElement oldContent) {
        contentDiv.removeAll();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
    }
}