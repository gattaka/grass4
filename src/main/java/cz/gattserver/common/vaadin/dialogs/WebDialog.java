package cz.gattserver.common.vaadin.dialogs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import cz.gattserver.common.ui.ComponentFactory;

public class WebDialog extends Dialog {

    private static final long serialVersionUID = -9184044674542039306L;

    protected ComponentFactory componentFactory;
    protected VerticalLayout layout = new VerticalLayout();

    public WebDialog(String caption) {
        init();
        setHeaderTitle(caption);
        componentFactory = new ComponentFactory();
    }

    public WebDialog() {
        init();
    }

    protected void init() {
        add(layout);
        setCloseOnOutsideClick(false);
        setCloseOnEsc(false);
        setDraggable(true);

        layout.setSpacing(true);
        layout.setPadding(false);
    }

    public void addComponent(Component component) {
        layout.add(component);
    }

    public void addComponent(Component component, Alignment alignment) {
        layout.add(component);
        setComponentAlignment(component, alignment);
    }

    public void setComponentAlignment(Component component, Alignment alignment) {
        layout.setHorizontalComponentAlignment(alignment, component);
    }

}
