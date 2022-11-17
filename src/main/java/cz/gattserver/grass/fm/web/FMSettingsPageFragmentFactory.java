package cz.gattserver.grass.fm.web;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.services.FileSystemService;
import cz.gattserver.grass.core.ui.components.button.SaveButton;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.fm.config.FMConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;

public class FMSettingsPageFragmentFactory extends AbstractPageFragmentFactory {

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private FileSystemService fileSystemService;

	@Override
	public void createFragment(Div layout) {
		final FMConfiguration configuration = loadConfiguration();
		final FileSystem fs = fileSystemService.getFileSystem();

		layout.add(new H2("Nastavení správce souborů"));

		// Kořenový adresář
		final TextField outputPathField = new TextField("Nastavení kořenového adresáře");
		outputPathField.setValue(configuration.getRootDir());
		outputPathField.setWidth("300px");
		layout.add(outputPathField);

		Binder<FMConfiguration> binder = new Binder<>();
		binder.forField(outputPathField).asRequired("Kořenový adresář je povinný").withValidator((val, c) -> {
			try {
				return Files.exists(fs.getPath(val)) ? ValidationResult.ok()
						: ValidationResult.error("Kořenový adresář musí existovat");
			} catch (InvalidPathException e) {
				return ValidationResult.error("Neplatná cesta");
			}
		}).bind(FMConfiguration::getRootDir, FMConfiguration::setRootDir);

		ButtonLayout buttonLayout = new ButtonLayout();
		layout.add(buttonLayout);

		// Save tlačítko
		SaveButton saveButton = new SaveButton(e -> {
			configuration.setRootDir((String) outputPathField.getValue());
			storeConfiguration(configuration);
		});
		binder.addValueChangeListener(l -> saveButton.setEnabled(binder.isValid()));
		buttonLayout.add(saveButton);
	}

	private FMConfiguration loadConfiguration() {
		FMConfiguration configuration = new FMConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration;
	}

	private void storeConfiguration(FMConfiguration configuration) {
		configurationService.saveConfiguration(configuration);
	}

}