package cz.gattserver.grass.hw.ui.pages;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.services.FileSystemService;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;
import cz.gattserver.grass.hw.service.HWService;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;

import cz.gattserver.grass.hw.HWConfiguration;

public class HWSettingsPageFragmentFactory extends AbstractPageFragmentFactory {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private FileSystemService fileSystemService;

    @Autowired
    private HWService hwService;

    @Override
    public void createFragment(Div div) {
        final HWConfiguration configuration = loadConfiguration();
        final FileSystem fs = fileSystemService.getFileSystem();

        div.add(new H2("Nastavení evidence HW"));

        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setSpacing(true);
        layout.setPadding(false);
        div.add(layout);

        /**
         * Kořenový adresář
         */
        final TextField outputPathField = new TextField("Nastavení kořenového adresáře");
        outputPathField.setWidth("300px");
        outputPathField.setValue(configuration.getRootDir());
        layout.add(outputPathField);

        Binder<HWConfiguration> binder = new Binder<>();
        binder.forField(outputPathField).asRequired("Kořenový adresář je povinný").withValidator((val, c) -> {
            try {
                return Files.exists(fs.getPath(val)) ? ValidationResult.ok() :
                        ValidationResult.error("Kořenový adresář musí existovat");
            } catch (InvalidPathException e) {
                return ValidationResult.error("Neplatná cesta");
            }
        }).bind(HWConfiguration::getRootDir, HWConfiguration::setRootDir);

        ComponentFactory componentFactory = new ComponentFactory();

        Div buttonLayout = componentFactory.createButtonLayout();
        layout.add(buttonLayout);

        Button saveButton = componentFactory.createSaveButton(e -> {
            configuration.setRootDir(outputPathField.getValue());
            storeConfiguration(configuration);
        });
        binder.addValueChangeListener(l -> saveButton.setEnabled(binder.isValid()));

        Button reprocessButton =
                new Button("Přegenerovat miniatury", VaadinIcon.REFRESH.create(), e -> hwService.processMiniatures());

        buttonLayout.add(saveButton, reprocessButton);
    }

    private HWConfiguration loadConfiguration() {
        HWConfiguration configuration = new HWConfiguration();
        configurationService.loadConfiguration(configuration);
        return configuration;
    }

    private void storeConfiguration(HWConfiguration configuration) {
        configurationService.saveConfiguration(configuration);
    }

}
