package cz.gattserver.common.ui;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.grass.fm.interfaces.FMCreateDirectoryTO;
import lombok.*;

import java.io.Serial;
import java.util.function.Consumer;

public class NameDialog extends EditWebDialog {

    @Serial
    private static final long serialVersionUID = 3279714188741874491L;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class ValueTO {
        private String value;
    }

    public NameDialog(String caption, String placeholder, String existingValue, Consumer<ValueTO> onSave) {
        super(caption);

        final Binder<ValueTO> binder = new Binder<>();
        binder.setBean(new ValueTO());

        ComponentFactory componentFactory = new ComponentFactory();

        final TextField textField = new TextField();
        textField.setPlaceholder(placeholder);
        textField.setWidth(400, Unit.PIXELS);
        binder.forField(textField).asRequired(componentFactory.createRequiredLabel())
                .bind(ValueTO::getValue, ValueTO::setValue);
        addComponent(textField);

        ValueTO existingTO = new ValueTO(existingValue);
        if (existingTO != null) binder.readBean(existingTO);

        getFooter().add(componentFactory.createDialogSubmitOrStornoLayout(event -> {
            try {
                ValueTO to = new ValueTO();
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