package cz.gattserver.common.vaadin.dialogs;

import com.vaadin.flow.component.UI;

public class EditWebDialog extends WebDialog {

	private static final long serialVersionUID = -7932181495479039816L;

	public EditWebDialog(String caption) {
		super(caption);
	}

	public EditWebDialog() {
		super();
	}

	@Override
	protected void init() {
		super.init();
		UI.getCurrent().getPage().executeJs(
				"window.onbeforeunload = function() { return \"Opravdu si přejete ukončit editor a odejít?\" };");
	}

	protected void removeBeforeUnload() {
		UI.getCurrent().getPage().executeJs("window.onbeforeunload = null;");
	}

	@Override
	public void close() {
		super.close();
		removeBeforeUnload();
	}

}
