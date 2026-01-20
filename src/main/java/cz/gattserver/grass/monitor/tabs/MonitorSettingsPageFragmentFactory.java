package cz.gattserver.grass.monitor.tabs;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import cz.gattserver.grass.core.services.FileSystemService;
import cz.gattserver.grass.core.ui.components.button.SaveButton;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;

import cz.gattserver.grass.monitor.config.MonitorConfiguration;
import cz.gattserver.grass.monitor.services.MonitorService;

public class MonitorSettingsPageFragmentFactory extends AbstractPageFragmentFactory {

	@Autowired
	private MonitorService monitorFacade;

	@Autowired
	private FileSystemService fileSystemService;

	@Override
	public void createFragment(Div div) {
		final MonitorConfiguration configuration = monitorFacade.getConfiguration();
		final FileSystem fs = fileSystemService.getFileSystem();

        div.add(new H2("Nastavení system monitoru"));

        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setSpacing(true);
        layout.setPadding(false);
        div.add(layout);

		/**
		 * Adresář skriptů
		 */
		final TextField scriptsDirField = new TextField("Adresář skriptů");
		scriptsDirField.setWidth("300px");
		scriptsDirField.setValue(String.valueOf(configuration.getScriptsDir()));
		layout.add(scriptsDirField);

		Binder<MonitorConfiguration> binder = new Binder<>();
		binder.forField(scriptsDirField).asRequired("Adresář skriptů je povinný").withValidator((val, c) -> {
			try {
				return Files.exists(fs.getPath(val)) ? ValidationResult.ok()
						: ValidationResult.error("Adresář skriptů musí existovat");
			} catch (InvalidPathException e) {
				return ValidationResult.error("Neplatná cesta");
			}
		}).bind(MonitorConfiguration::getScriptsDir, MonitorConfiguration::setScriptsDir);

		/**
		 * Save tlačítko
		 */
		ButtonLayout buttonLayout = new ButtonLayout();
		SaveButton saveButton = new SaveButton("Uložit", event -> {
			if (binder.validate().isOk()) {
				configuration.setScriptsDir(scriptsDirField.getValue());
				monitorFacade.storeConfiguration(configuration);
			}
		});
		binder.addValueChangeListener(l -> saveButton.setEnabled(binder.isValid()));
		buttonLayout.add(saveButton);
		layout.add(buttonLayout);
	}

}
