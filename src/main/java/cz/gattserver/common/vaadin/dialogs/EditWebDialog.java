package cz.gattserver.common.vaadin.dialogs;

import com.vaadin.flow.component.UI;

public class EditWebDialog extends WebDialog {

    private static final long serialVersionUID = -7932181495479039816L;

    private final boolean readOnly;

    public EditWebDialog(String caption) {
        this(caption, false);
    }

    public EditWebDialog(String caption, boolean readOnly) {
        super(caption);
        setCloseOnEsc(readOnly);
        this.readOnly = readOnly;
    }

    @Override
    public void open() {
        super.open();
        if (!readOnly) UI.getCurrent().getPage().executeJs(
                "window.onbeforeunload = function() { return \"Opravdu si přejete ukončit editor a odejít?\" };");
    }

    @Override
    public void close() {
        super.close();
        if (!readOnly) UI.getCurrent().getPage().executeJs("window.onbeforeunload = null;");
    }
}