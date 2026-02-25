package cz.gattserver.common.vaadin.dialogs;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class ConfirmDialog extends WebDialog {

    @Serial
    private static final long serialVersionUID = 2220892707142552312L;

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
        super("Potvrzení operace");
        this.confirmAction = confirmAction;

        content.setWidth(350, Unit.PIXELS);

        addComponent(content, Alignment.STRETCH);
        HorizontalLayout btnLayout = new HorizontalLayout();
        addComponent(btnLayout, Alignment.END);

        btnLayout.add(componentFactory.createDialogSubmitOrStornoLayout(e -> {
            ConfirmDialog.this.confirmAction.onConfirm(e);
            close();
        }, e -> close()));
    }
}