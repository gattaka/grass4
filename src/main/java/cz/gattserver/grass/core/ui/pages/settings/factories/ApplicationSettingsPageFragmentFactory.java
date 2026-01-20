package cz.gattserver.grass.core.ui.pages.settings.factories;

import com.vaadin.flow.component.button.Button;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.core.config.CoreConfiguration;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ApplicationSettingsPageFragmentFactory extends AbstractPageFragmentFactory {

    private static final Double MIN_SESSION_TIMEOUT = 5.0;
    private static final Double MAX_SESSION_TIMEOUT = 60.0;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public void createFragment(Div layout) {
        final CoreConfiguration configuration = loadConfiguration();

        layout.add(new H2("Nastavení aplikace"));

        // Nadpis zůstane odsazen a jednotlivá pole se můžou mezi sebou rozsázet
        VerticalLayout settingsFieldsLayout = new VerticalLayout();
        layout.add(settingsFieldsLayout);
        settingsFieldsLayout.setPadding(false);
        settingsFieldsLayout.setSpacing(true);
        settingsFieldsLayout.setSizeFull();

        /**
         * Session Timeout
         */
        HorizontalLayout sessionTimeoutLayout = new HorizontalLayout();
        sessionTimeoutLayout.setSizeFull();
        sessionTimeoutLayout.setSpacing(true);

        Double initValue = configuration.getSessionTimeout();
        if (initValue > MAX_SESSION_TIMEOUT) {
            initValue = MAX_SESSION_TIMEOUT;
            configuration.setSessionTimeout(initValue);
            storeConfiguration(configuration);
        }
        if (initValue < MIN_SESSION_TIMEOUT) {
            initValue = MIN_SESSION_TIMEOUT;
            configuration.setSessionTimeout(initValue);
            storeConfiguration(configuration);
        }

        final Span valueSpan = new Span(initValue.toString());
        valueSpan.setWidth("3em");

        /**
         * Povolení registrací
         */
        final Checkbox allowRegistrationsBox = new Checkbox("Povolit registrace");
        allowRegistrationsBox.setValue(configuration.isRegistrations());
        allowRegistrationsBox.addValueChangeListener(
                event -> configuration.setRegistrations(allowRegistrationsBox.getValue()));

        settingsFieldsLayout.add(allowRegistrationsBox);

        /**
         * Save tlačítko
         */
        ComponentFactory componentFactory = new ComponentFactory();
        Button saveButton = componentFactory.createSaveButton(event -> storeConfiguration(configuration));
        settingsFieldsLayout.add(saveButton);
    }

    private CoreConfiguration loadConfiguration() {
        CoreConfiguration configuration = new CoreConfiguration();
        configurationService.loadConfiguration(configuration);
        return configuration;
    }

    private void storeConfiguration(CoreConfiguration configuration) {
        configurationService.saveConfiguration(configuration);
    }

}
