package cz.gattserver.grass.campgames.ui.pages;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import cz.gattserver.common.ui.ComponentFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;

import cz.gattserver.grass.campgames.CampgamesConfiguration;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.services.FileSystemService;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;

public class CampgamesSettingsPageFragmentFactory extends AbstractPageFragmentFactory {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private FileSystemService fileSystemService;

    @Override
    public void createFragment(Div div) {
        final CampgamesConfiguration configuration = loadConfiguration();
        final FileSystem fs = fileSystemService.getFileSystem();

        div.add(new H2("Nastavení evidence táborových her"));

        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setSpacing(true);
        layout.setPadding(false);
        div.add(layout);

        /**
         * Kořenový adresář
         */
        final TextField outputPathField = new TextField("Nastavení kořenového adresáře");
        outputPathField.setValue(configuration.getRootDir());
        outputPathField.setWidth("300px");
        layout.add(outputPathField);

        Binder<CampgamesConfiguration> binder = new Binder<>();
        binder.forField(outputPathField).asRequired("Kořenový adresář je povinný").withValidator((val, c) -> {
            try {
                return Files.exists(fs.getPath(val)) ? ValidationResult.ok() :
                        ValidationResult.error("Kořenový adresář musí existovat");
            } catch (InvalidPathException e) {
                return ValidationResult.error("Neplatná cesta");
            }
        }).bind(CampgamesConfiguration::getRootDir, CampgamesConfiguration::setRootDir);

        // Save tlačítko
        ComponentFactory componentFactory = new ComponentFactory();
        Button saveButton = componentFactory.createSaveButton(e -> {
            configuration.setRootDir(outputPathField.getValue());
            storeConfiguration(configuration);
            Notification.show("Uložení proběhlo úspěšně", 1000, Position.MIDDLE);
        });
        binder.addValueChangeListener(l -> saveButton.setEnabled(binder.isValid()));

        Div buttonLayout = componentFactory.createButtonLayout();
        layout.add(buttonLayout);
        buttonLayout.add(saveButton);
    }

    private CampgamesConfiguration loadConfiguration() {
        CampgamesConfiguration configuration = new CampgamesConfiguration();
        configurationService.loadConfiguration(configuration);
        return configuration;
    }

    private void storeConfiguration(CampgamesConfiguration configuration) {
        configurationService.saveConfiguration(configuration);
    }

}
