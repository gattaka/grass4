package cz.gattserver.common.vaadin.dialogs;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.io.Serializable;

public class ConfirmDialog extends WebDialog {

    private static final long serialVersionUID = 4123506060675738841L;

    private ConfirmAction confirmAction;

    public interface ConfirmAction extends Serializable {
        void onConfirm(ClickEvent<?> event);
    }

    /**
     * Nejobecnější dotaz na potvrzení operace
     */
    public ConfirmDialog(ConfirmAction confirmAction) {
        this("Opravdu si přejete provést tuto operaci ?", confirmAction);
    }

    /**
     * Okno bude vytvořeno s popiskem, ve kterém bude předaný text
     *
     * @param content text popisku okna
     */
    public ConfirmDialog(String content, ConfirmAction confirmAction) {
        this(new Div(content), confirmAction);
    }

    /**
     * Okno bude vytvořeno přímo s připraveným popiskem
     *
     * @param content obsah okna
     */
    public ConfirmDialog(Div content, ConfirmAction confirmAction) {
        this.confirmAction = confirmAction;

        content.setWidth(350, Unit.PIXELS);

        addComponent(content, Alignment.STRETCH);
        HorizontalLayout btnLayout = new HorizontalLayout();
        addComponent(btnLayout, Alignment.CENTER);

        Button confirm = new Button("Ano", (ComponentEventListener<ClickEvent<Button>>) event -> {
            ConfirmDialog.this.confirmAction.onConfirm(event);
            close();
        });
        btnLayout.add(confirm);

        Button close = new Button("Ne", (ComponentEventListener<ClickEvent<Button>>) event -> close());
        btnLayout.add(close);

    }

    public ConfirmAction getConfirmAction() {
        return confirmAction;
    }

    public void setConfirmAction(ConfirmAction confirmAction) {
        this.confirmAction = confirmAction;
    }

}
