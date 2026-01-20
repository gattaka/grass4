package cz.gattserver.grass.hw.ui.pages;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.util.UUID;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.grass.articles.ui.pages.settings.ArticlesSettingsPageFragmentFactory;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.services.FileSystemService;
import cz.gattserver.grass.core.ui.components.button.ImageButton;
import cz.gattserver.grass.core.ui.components.button.SaveButton;
import cz.gattserver.grass.core.ui.dialogs.ProgressDialog;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;
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

        ButtonLayout buttonLayout = new ButtonLayout();
        layout.add(buttonLayout);

        Button saveButton = new SaveButton(e -> {
            configuration.setRootDir(outputPathField.getValue());
            storeConfiguration(configuration);
        });
        binder.addValueChangeListener(l -> saveButton.setEnabled(binder.isValid()));

        ImageButton reprocessButton =
                new ImageButton("Přegenerovat miniatury", ImageIcon.GEAR2_16_ICON, e -> hwService.processMiniatures());

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
