package cz.gattserver.common.vaadin.dialogs;

import cz.gattserver.grass.core.ui.util.UIUtils;

import java.io.Serial;

public class EditWebDialog extends WebDialog {

    @Serial
    private static final long serialVersionUID = -432908336516831406L;

    protected final boolean readOnly;

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
        if (!readOnly) UIUtils.addOnbeforeunloadWarning();
    }

    @Override
    public void close() {
        if (!readOnly) {
            UIUtils.removeOnbeforeunloadWarning().then(e -> super.close());
        } else {
            super.close();
        }
    }
}