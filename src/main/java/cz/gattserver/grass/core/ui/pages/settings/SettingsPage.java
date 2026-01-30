package cz.gattserver.grass.core.ui.pages.settings;

import java.util.Comparator;
import java.util.List;

import com.vaadin.flow.router.*;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.modules.register.ModuleSettingsPageFactoriesRegister;
import cz.gattserver.grass.core.ui.pages.MainView;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@PageTitle("Nastavení")
@Route(value = "settings", layout = MainView.class)
public class SettingsPage extends Div implements HasUrlParameter<String> {

    private static final long serialVersionUID = 935609806962179905L;

    private List<ModuleSettingsPageFactory> settingsTabFactories;
    private ModuleSettingsPageFactoriesRegister register;

    public SettingsPage(List<ModuleSettingsPageFactory> settingsTabFactories,
                        ModuleSettingsPageFactoriesRegister register) {
        this.settingsTabFactories = settingsTabFactories;
        this.register = register;
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        removeAll();
        ComponentFactory componentFactory = new ComponentFactory();
        Div leftLayout = componentFactory.createLeftColumnLayout();
        Div rightLayout = componentFactory.createRightColumnLayout();
        add(leftLayout);
        add(rightLayout);

        createLeftColumnContent(leftLayout);
        ModuleSettingsPageFactory moduleSettingsPageFactory = register.getFactory(parameter);
        if (moduleSettingsPageFactory != null && !moduleSettingsPageFactory.isAuthorized())
            throw new GrassPageException(403);

        createRightColumnContent(rightLayout, moduleSettingsPageFactory);
    }

    protected void createLeftColumnContent(Div leftContentLayout) {
        VerticalLayout menuLayout = new VerticalLayout();
        menuLayout.setSpacing(true);
        menuLayout.setPadding(false);
        leftContentLayout.add(menuLayout);
        settingsTabFactories.sort(Comparator.comparing(ModuleSettingsPageFactory::getSettingsCaption));
        for (ModuleSettingsPageFactory f : settingsTabFactories) {
            RouterLink link = new RouterLink(f.getSettingsCaption(), SettingsPage.class, f.getSettingsURL());
            menuLayout.add(link);
        }
    }

    protected void createRightColumnContent(Div rightContentLayout, ModuleSettingsPageFactory factory) {
        if (factory != null) {
            factory.createFragmentIfAuthorized(rightContentLayout);
        } else {
            Span span = new Span("Zvolte položku nastavení z menu");
            rightContentLayout.add(span);
        }
    }
}