package cz.gattserver.grass.fm.web;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.fm.interfaces.FMCreateDirectoryTO;
import cz.gattserver.grass.fm.interfaces.FMItemTO;

import java.util.function.Consumer;

public class FileNameDialog extends EditWebDialog {

    public FileNameDialog(Consumer<FMCreateDirectoryTO> onSave) {
        this(null, onSave);
    }

    public FileNameDialog(String existingName, Consumer<FMCreateDirectoryTO> onSave) {
        super("Zadání názvu");
        init(new FMCreateDirectoryTO(existingName), onSave);
    }

    private void init(FMCreateDirectoryTO existingTO, Consumer<FMCreateDirectoryTO> onSave) {
        final Binder<FMCreateDirectoryTO> binder = new Binder<>();
        binder.setBean(new FMCreateDirectoryTO());

        ComponentFactory componentFactory = new ComponentFactory();

        final TextField textField = new TextField();
        textField.setPlaceholder("Název souboru");
        textField.setWidth(400, Unit.PIXELS);
        binder.forField(textField).asRequired(componentFactory.createRequiredLabel())
                .bind(FMCreateDirectoryTO::getName, FMCreateDirectoryTO::setName);
        addComponent(textField);

        if (existingTO != null) binder.readBean(existingTO);

        getFooter().add(componentFactory.createDialogSubmitOrStornoLayout(event -> {
            try {
                FMCreateDirectoryTO to = new FMCreateDirectoryTO();
                binder.writeBean(to);
                onSave.accept(to);
                close();
            } catch (ValidationException e) {
                // UI
            }
        }, e -> close()));
        textField.focus();
    }
}